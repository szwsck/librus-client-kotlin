package com.wabadaba.dziennik

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.wabadaba.dziennik.ui.multiPutAll
import io.reactivex.Observable
import io.reactivex.Single
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.reactivex.ReactiveResult
import io.requery.reactivex.ReactiveScalar
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

@Suppress("UNCHECKED_CAST")
class InMemoryEntityStore {
    companion object {


        fun getDatastore(): KotlinReactiveEntityStore<Persistable> {
            val multiMap = mutableMapOf<KClass<Persistable>, List<Persistable>>()
            return mock {
                on { upsert(any<List<Persistable>>()) } doAnswer { invocation ->
                    val entities = invocation.getArgument<List<Persistable>>(0)
                    if (entities.isNotEmpty()) {
                        val kClass = entities.first()::class.superclasses[0] as KClass<Persistable>
                        multiMap.multiPutAll(kClass, entities)
                    }
                    Single.just(entities)
                }
                on { select(any<KClass<Persistable>>()) } doAnswer { invocation ->
                    val kClass = invocation.getArgument<KClass<Persistable>>(0)
                    val res = multiMap[kClass] ?: emptyList()
                    val resultMock = mock<ReactiveResult<Persistable>> {
                        on { observable() } doReturn (Observable.fromIterable(res))
                    }
                    mock { on { get() } doReturn resultMock }
                }
                on { delete(any<KClass<Persistable>>()) } doAnswer { invocation ->
                    val kClass = invocation.getArgument<KClass<Persistable>>(0)
                    multiMap.remove(kClass)
                    val resultMock = mock<ReactiveScalar<Int>> {
                        on { single() } doReturn (Single.just(0))
                    }
                    mock { on { get() } doReturn resultMock }
                }
            }
        }
    }
}