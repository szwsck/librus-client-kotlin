package com.wabadaba.dziennik.api

import com.nhaarman.mockito_kotlin.*
import com.wabadaba.dziennik.InMemoryEntityStore
import com.wabadaba.dziennik.vo.Grade
import com.wabadaba.dziennik.vo.GradeEntity
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.requery.Persistable
import org.joda.time.LocalDate
import org.junit.Test
import kotlin.reflect.KClass

class EntityRepositoryTest {

    private val user1Full = FullUser(
            "user1",
            "user1FirstName",
            "user1FirstName",
            5, AuthInfo("aToken1", "rToken1"))
    private val user2Full = FullUser(
            "user2",
            "user2FirstName",
            "user2FirstName",
            5,
            AuthInfo("aToken2", "rToken2"))

    private val userSubject: BehaviorSubject<FullUser> = BehaviorSubject.create<FullUser>()

    private val grade1: Grade = GradeEntity().apply {
        setId("grade1")
        setDate(LocalDate.now())
    }
    private val grade2: Grade = GradeEntity().apply {
        setId("grade2")
        setDate(LocalDate.now())
    }

    @Test
    fun shouldReadFromDatabase() {
        val apiClientMock = mock<RefreshableAPIClient> {
            on { fetchEntities(any<KClass<out Persistable>>(), any()) } doReturn Observable.empty<Persistable>()
            on { fetchEntities(Grade::class) } doReturn Observable.just(grade1, grade2)
            on { refreshIfNeeded() } doReturn Completable.complete()
        }
        val entityRepository = EntityRepository(userSubject,
                { InMemoryEntityStore.getDatastore() },
                apiClientMock
        )

        val testObserver1 = entityRepository.grades.test()

        userSubject.onNext(user1Full)

        testObserver1.awaitCount(2)

        testObserver1.assertValues(emptyList(), listOf(grade1, grade2))

        val testObserver2 = entityRepository.grades.test()

        testObserver2.assertValue(listOf(grade1, grade2))

        verify(apiClientMock, times(1)).fetchEntities(Grade::class)
    }

    @Test
    fun shouldReceiveListOnSubscribeAndAfterUserChange() {
        val apiClientMock = mock<RefreshableAPIClient> {
            on { fetchEntities(any<KClass<out Persistable>>(), any()) } doReturn (Observable.empty<Persistable>())
            on { fetchEntities(Grade::class) } doReturn (Observable.just(grade1, grade2))
            on { refreshIfNeeded() } doReturn Completable.complete()
        }
        val entityRepository = EntityRepository(userSubject,
                { InMemoryEntityStore.getDatastore() },
                apiClientMock
        )
        val testObserver = entityRepository.grades.test()
        userSubject.onNext(user1Full)
        testObserver.awaitCount(2)
        userSubject.onNext(user2Full)
        testObserver.awaitCount(4)
        verify(apiClientMock, times(2)).fetchEntities(Grade::class)
        testObserver.assertValues(emptyList(), listOf(grade1, grade2), emptyList(), listOf(grade1, grade2))
    }
}