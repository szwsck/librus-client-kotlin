package com.wabadaba.dziennik.api

import com.nhaarman.mockito_kotlin.*
import com.wabadaba.dziennik.BaseDBTest
import com.wabadaba.dziennik.db.DatabaseManager
import com.wabadaba.dziennik.vo.Grade
import com.wabadaba.dziennik.vo.GradeEntity
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.joda.time.LocalDate
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class EntityRepositoryTest : BaseDBTest() {

    val user1Full = FullUser(
            "user1",
            "user1FirstName",
            "user1FirstName",
            5, AuthInfo("aToken1", "rToken1", 12))
    val user2Full = FullUser(
            "user2",
            "user2FirstName",
            "user2FirstName",
            5,
            AuthInfo("aToken2", "rToken2", 12))

    val userSubject: BehaviorSubject<FullUser> = BehaviorSubject.create<FullUser>()

    val grade1: Grade = GradeEntity().apply {
        setId("grade1")
        setDate(LocalDate.now())
    }
    val grade2: Grade = GradeEntity().apply {
        setId("grade2")
        setDate(LocalDate.now())
    }

    @Test
    fun shouldReadFromDatabase() {
        val datastore = spy(DatabaseManager(RuntimeEnvironment.application, user1Full).dataStore)
        val apiClientMock = mock<APIClient> {
            on { fetchEntities(Grade::class) } doReturn (Observable.just(grade1, grade2))
        }
        val entityRepository = EntityRepository(userSubject,
                {
                    datastore
                },
                {
                    apiClientMock
                })

        val testObserver1 = entityRepository.grades.test()

        userSubject.onNext(user1Full)

        testObserver1.awaitCount(2)

        testObserver1.assertValues(emptyList(), listOf(grade1, grade2))

        val testObserver2 = entityRepository.grades.test()

        testObserver2.assertValue(listOf(grade1, grade2))

        verify(datastore, times(1)).select(Grade::class)
        verify(apiClientMock, times(1)).fetchEntities(Grade::class)
    }

    @Test
    fun shouldReceiveListOnSubscribeAndAfterUserChange() {
        val apiClientMock = mock<APIClient> {
            on { fetchEntities(Grade::class) } doReturn (Observable.just(grade1, grade2))
        }
        val entityRepository = EntityRepository(userSubject, {
            fullUser ->
            DatabaseManager(RuntimeEnvironment.application, fullUser).dataStore
        }, { apiClientMock })
        val testObserver = entityRepository.grades.test()
        userSubject.onNext(user2Full)
        verify(apiClientMock, times(2)).fetchEntities(Grade::class)
        println(testObserver.values())
        testObserver.assertValues(listOf(grade1, grade2), listOf(grade1, grade2))
    }
}