package com.wabadaba.dziennik.api

import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class UserRepositoryTest {

    val user1 = User(
            "user1",
            "user1FirstName",
            "user1FirstName",
            5)
    val user2 = User(
            "user2",
            "user2FirstName",
            "user2FirstName",
            5)
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

    @Test
    fun shouldCreateUser() {
        val userRepository = UserRepository(RuntimeEnvironment.application)
        val currentUserTest = userRepository.currentUser.test()
        val allUsersTest = userRepository.allUsers.test()

        allUsersTest.assertValue(emptyList())
        currentUserTest.assertEmpty()

        userRepository.addUser(user1Full)

        currentUserTest.values()[0] shouldEqual user1Full
        allUsersTest.values()[1] shouldEqual listOf(user1)

        userRepository.addUser(user2Full)

        currentUserTest.values()[1] shouldEqual user2Full
        allUsersTest.values()[2] shouldEqual listOf(user2, user1)
    }

    @Test
    fun shouldSwitchUser() {
        val userRepository = UserRepository(RuntimeEnvironment.application)
        val currentUserTest = userRepository.currentUser.test()
        val allUsersTest = userRepository.allUsers.test()

        userRepository.addUser(user1Full)
        userRepository.addUser(user2Full)

        val countBefore = allUsersTest.valueCount()

        userRepository.switchUser(user1.login)

        currentUserTest.values().last() shouldEqual user1Full
        allUsersTest.valueCount() shouldEqual countBefore
    }

    @Test
    fun shouldRemoveOtherUser() {
        val userRepository = UserRepository(RuntimeEnvironment.application)
        val currentUserTest = userRepository.currentUser.test()
        val allUsersTest = userRepository.allUsers.test()

        userRepository.addUser(user1Full)
        userRepository.addUser(user2Full)

        userRepository.removeUser(user1.login)

        currentUserTest.values().last() shouldEqual user2Full
        allUsersTest.values().last() shouldEqual listOf(user2.login)
    }

    @Test
    fun shouldRemoveCurrentUser() {
        val userRepository = UserRepository(RuntimeEnvironment.application)
        val currentUserTest = userRepository.currentUser.test()
        val allUsersTest = userRepository.allUsers.test()

        userRepository.addUser(user1Full)
        userRepository.addUser(user2Full)

        userRepository.removeUser(user2.login)

        currentUserTest.values().last() shouldEqual user1Full
        allUsersTest.values().last() shouldEqual listOf(user1.login)
    }

    @Test
    fun shouldRemoveTheOnlyUser() {
        val userRepository = UserRepository(RuntimeEnvironment.application)
        val currentUserTest = userRepository.currentUser.test()
        val allUsersTest = userRepository.allUsers.test()

        userRepository.addUser(user1Full)

        val countBefore = currentUserTest.valueCount()

        userRepository.removeUser(user1.login)

        currentUserTest.valueCount() shouldEqualTo countBefore
        allUsersTest.values().last() shouldEqual emptyList()
    }
}