package com.wabadaba.dziennik.api

import com.wabadaba.dziennik.vo.Average
import com.wabadaba.dziennik.vo.Grade
import com.wabadaba.dziennik.vo.Identifiable
import com.wabadaba.dziennik.vo.Models
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.toSingle
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import kotlin.reflect.KClass

class EntityRepository(userObservable: Observable<FullUser>, val datastoreCreator: ((FullUser) -> KotlinReactiveEntityStore<Persistable>), val apiClientCreator: ((FullUser) -> APIClient)) {

    private val gradesSubject: BehaviorSubject<List<Grade>> = BehaviorSubject.create()
    val grades: Observable<List<Grade>> = gradesSubject

    private val averagesSubject: BehaviorSubject<List<Average>> = BehaviorSubject.create()
    val averages: Observable<List<Average>> = averagesSubject

    private val refreshSubject = BehaviorSubject.createDefault<Unit>(Unit)

    lateinit var datastore: KotlinReactiveEntityStore<Persistable>

    init {

        val dbLoad = userObservable
                .doOnNext { datastore = datastoreCreator(it) }
                .doOnNext { loadBaseEntities() }

        Observables.combineLatest(dbLoad, refreshSubject) {
            user: FullUser, _: Unit ->
            user

        }.subscribeOn(Schedulers.io())
                .subscribe { user -> refreshAll(user) }

    }

    fun refresh() = refreshSubject.onNext(Unit)

    @Suppress("UNCHECKED_CAST")
    fun refreshAll(user: FullUser) {
        Observable.fromIterable(Models.DEFAULT.types)
                .map { it.baseType.kotlin as KClass<Identifiable> }
                .flatMapSingle { kClass ->
                    apiClientCreator(user)
                            .fetchEntities(kClass)
                            .toList()
                            .subscribeOn(Schedulers.io())
                }.toList().flatMapObservable { Observable.fromIterable(it) }
                .flatMapSingle { kClass ->
                    datastore.delete(kClass).toSingle()
                            .map { kClass }
                }.toList().flatMapObservable { Observable.fromIterable(it) }
                .flatMapSingle {
                    datastore.upsert(it)
                            .subscribeOn(Schedulers.io())
                }.toList().flatMapObservable { Observable.fromIterable(it) }
                .doOnComplete { loadBaseEntities() }
                .subscribe()
    }

    fun loadBaseEntities() {
        datastore.select(Grade::class)
                .get()
                .observable()
                .toList()
                .subscribeOn(Schedulers.io())
                .subscribe(gradesSubject::onNext)
    }
}