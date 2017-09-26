package com.wabadaba.dziennik.api

import com.wabadaba.dziennik.vo.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.toSingle
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import org.joda.time.DateTimeConstants.MONDAY
import org.joda.time.LocalDate
import kotlin.reflect.KClass

class EntityRepository(userObservable: Observable<FullUser>,
                       private val datastoreCreator: ((FullUser) -> KotlinReactiveEntityStore<Persistable>),
                       private val apiClient: RefreshableAPIClient) {

    @Suppress("UNCHECKED_CAST")
    private val allEntityTypes = Observable.fromIterable(Models.DEFAULT.types)
            .map { it.baseType.kotlin as KClass<Persistable> }

    private val gradesSubject: BehaviorSubject<List<Grade>> = BehaviorSubject.create()
    val grades: Observable<List<Grade>> = gradesSubject

    private val attendancesSubject: BehaviorSubject<List<Attendance>> = BehaviorSubject.create()
    val attendances: Observable<List<Attendance>> = attendancesSubject

    private val lessonsSubject: BehaviorSubject<List<Lesson>> = BehaviorSubject.create()
    val lessons: Observable<List<Lesson>> = lessonsSubject

    private val eventsSubject: BehaviorSubject<List<Event>> = BehaviorSubject.create()
    val events: Observable<List<Event>> = eventsSubject

    private val announcementsSubject: BehaviorSubject<List<Announcement>> = BehaviorSubject.create()
    val announcements: Observable<List<Announcement>> = announcementsSubject

    private val luckyNumberSubject: BehaviorSubject<List<LuckyNumber>> = BehaviorSubject.create()
    val luckyNumber: Observable<List<LuckyNumber>> = luckyNumberSubject

    private val refreshSubject = BehaviorSubject.createDefault<Unit>(Unit)

    private lateinit var datastore: KotlinReactiveEntityStore<Persistable>

    init {
        val dbLoad = userObservable
                .doOnNext { datastore = datastoreCreator(it) }
                .doOnNext { loadBaseEntities() }

        Observables.combineLatest(dbLoad, refreshSubject) { user: FullUser, _: Unit -> user }
                .subscribeOn(Schedulers.io())
                .subscribe { refreshAll() }

    }

    fun refresh() = refreshSubject.onNext(Unit)

    private fun refreshAll() {
        apiClient.refreshIfNeeded()
                .subscribe {
                    allEntityTypes
                            .flatMapSingle { kClass ->
                                download(kClass, when (kClass) {
                                    Lesson::class -> listOf(Pair("weekStart", getDefaultWeekStart().toString("yyyy-MM-dd")))
                                    Attendance::class -> listOf(Pair("showPresences", "false"))
                                    else -> emptyList()
                                })
                            }.toList()
                            .flatMapObservable { lists -> this.delete().andThen(Observable.fromIterable(lists)) }
                            .flatMapSingle(this::upsert)
                            .ignoreElements()
                            .subscribeOn(Schedulers.io())
                            .subscribe {
                                println("Loading entities")
                                loadBaseEntities()
                            }
                }
    }

    private fun download(kClass: KClass<Persistable>, queryParams: List<Pair<String, String>>): Single<MutableList<Persistable>>? {
        return apiClient
                .fetchEntities(kClass, queryParams)
                .toList()
                .onErrorResumeNext {
                    if (it is HttpException.NotActive) {
                        println("${kClass.simpleName} is disabled, returning empty list")
                        Single.just(emptyList())
                    } else Single.error(it)
                }
                .doOnSuccess { println("downloaded $it") }
                .subscribeOn(Schedulers.io())
    }

    private fun delete() = allEntityTypes
            .flatMapSingle { datastore.delete(it).toSingle() }
            .ignoreElements()


    private fun upsert(it: List<Persistable>): Single<Iterable<Persistable>>? {
        println("saving $it")
        return datastore.upsert(it).subscribeOn(Schedulers.io())
    }


    /**
     * Waits until source observable completes, then emits all items at once
     */
    private fun <T> Observable<T>.waitForAll(): Observable<T> = this.toList().flatMapObservable { Observable.fromIterable(it) }

    private fun loadBaseEntities() {
        datastore.select(Grade::class)
                .get()
                .observable()
                .toList()
                .subscribe(gradesSubject::onNext)
        datastore.select(Attendance::class)
                .get()
                .observable()
                .toList()
                .subscribe(attendancesSubject::onNext)
        datastore.select(Lesson::class)
                .get()
                .observable()
                .toList()
                .subscribe(lessonsSubject::onNext)
        datastore.select(Event::class)
                .get()
                .observable()
                .toList()
                .subscribe(eventsSubject::onNext)
        datastore.select(Announcement::class)
                .get()
                .observable()
                .toList()
                .subscribe(announcementsSubject::onNext)
        datastore.select(LuckyNumber::class)
                .get()
                .observable()
                .toList()
                .subscribe(luckyNumberSubject::onNext)
    }

    private fun getDefaultWeekStart() = LocalDate.now().plusDays(2).withDayOfWeek(MONDAY)
}