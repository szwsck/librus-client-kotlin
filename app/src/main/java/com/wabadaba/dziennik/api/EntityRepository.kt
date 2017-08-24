package com.wabadaba.dziennik.api

import com.wabadaba.dziennik.vo.Grade
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import mu.KotlinLogging

class EntityRepository(userObservable: Observable<FullUser>, val datstoreCreator: ((FullUser) -> KotlinReactiveEntityStore<Persistable>), val apiClientCreator: ((FullUser) -> APIClient)) {

    private val gradesSubject: BehaviorSubject<List<Grade>> = BehaviorSubject.create()
    val grades: Observable<List<Grade>> = gradesSubject

    private val refreshSubject = BehaviorSubject.createDefault<Unit>(Unit)

    lateinit var datastore: KotlinReactiveEntityStore<Persistable>

    val logger = KotlinLogging.logger { }

    init {

        val dbLoad = userObservable
                .doOnNext { datastore = datstoreCreator(it) }
                .doOnNext { loadGradesFromDatabase() }

        Observables.combineLatest(dbLoad, refreshSubject) {
            user: FullUser, _: Unit ->
            user
        }.subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { user -> refreshAll(user) }
    }

    fun loadGradesFromDatabase(): Unit {
        datastore.select(Grade::class)
                .get()
                .observable()
                .toList()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(gradesSubject::onNext)
    }

    fun refresh() = refreshSubject.onNext(Unit)

    fun refreshAll(user: FullUser) {
        apiClientCreator(user).fetchEntities(Grade::class)
                .toList()
                .doOnSuccess {
                    System.out.println("Loaded grades from server")
                    datastore.upsert(it).subscribe()
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(gradesSubject::onNext)
    }
}