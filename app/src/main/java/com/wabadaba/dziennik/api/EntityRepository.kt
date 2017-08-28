package com.wabadaba.dziennik.api

import com.wabadaba.dziennik.vo.Grade
import com.wabadaba.dziennik.vo.Identifiable
import com.wabadaba.dziennik.vo.Lesson
import com.wabadaba.dziennik.vo.Models
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
                       private val apiClientCreator: ((FullUser) -> APIClient)) {

    private val gradesSubject: BehaviorSubject<List<Grade>> = BehaviorSubject.create()
    val grades: Observable<List<Grade>> = gradesSubject

    private val lessonsSubject: BehaviorSubject<Map<LocalDate, List<Lesson>>> = BehaviorSubject.create()
    val lessons: Observable<Map<LocalDate, List<Lesson>>> = lessonsSubject

    private val refreshSubject = BehaviorSubject.createDefault<Unit>(Unit)

    private lateinit var datastore: KotlinReactiveEntityStore<Persistable>

    init {

        val dbLoad = userObservable
                .doOnNext { datastore = datastoreCreator(it) }
                .doOnNext { loadBaseEntities() }

        Observables.combineLatest(dbLoad, refreshSubject) { user: FullUser, _: Unit ->
            user

        }.subscribeOn(Schedulers.io())
                .subscribe { user -> refreshAll(user) }

    }

    fun refresh() = refreshSubject.onNext(Unit)

    @Suppress("UNCHECKED_CAST")
    private fun refreshAll(user: FullUser) {
        Observable.fromIterable(Models.DEFAULT.types)
                .map { it.baseType.kotlin as KClass<Identifiable> }
                .flatMapSingle { kClass ->
                    download(user, kClass, when (kClass) {
                        Lesson::class -> listOf(Pair("weekStart", getDefaultWeekStart().toString("yyyy-MM-dd")))
                        else -> emptyList()
                    })
                }.waitForAll()
                .flatMapSingle(this::delete).waitForAll()
                .flatMapSingle(this::upsert).waitForAll()
                .subscribe { loadBaseEntities() }
    }

    private fun download(user: FullUser, kClass: KClass<Identifiable>, queryParams: List<Pair<String, String>>): Single<MutableList<Identifiable>>? {
        return apiClientCreator(user)
                .fetchEntities(kClass, queryParams)
                .toList()
                .subscribeOn(Schedulers.io())
    }

    private fun delete(kClass: List<Identifiable>): Single<List<Identifiable>>?
            = datastore.delete(kClass).toSingle().map { kClass }

    private fun upsert(it: List<Identifiable>)
            = datastore.upsert(it).subscribeOn(Schedulers.io())

    /**
     * Waits until source observable completes, then emits all items at once
     */
    private fun <T> Observable<T>.waitForAll(): Observable<T> = this.toList().flatMapObservable { Observable.fromIterable(it) }

    private fun loadBaseEntities() {
        datastore.select(Grade::class)
                .get()
                .observable()
                .toList()
                .subscribeOn(Schedulers.io())
                .subscribe(gradesSubject::onNext)
        datastore.select(Lesson::class)
                .get()
                .observable()
                .toList()
                .map { loadedLessons ->
                    val result = mutableMapOf<LocalDate, MutableList<Lesson>>()
                    for (lesson in loadedLessons) {
                        if (!result.contains(lesson.date))
                            result.put(lesson.date, mutableListOf())
                        result[lesson.date]?.add(lesson)
                    }
                    result.values.forEach { list -> list.sortBy(Lesson::lessonNumber) }
                    result
                }
                .subscribeOn(Schedulers.io())
                .subscribe(lessonsSubject::onNext)
    }

    private fun getDefaultWeekStart() = LocalDate.now().plusDays(2).withDayOfWeek(MONDAY)
}