package com.wabadaba.dziennik

import com.wabadaba.dziennik.api.AuthInfo
import com.wabadaba.dziennik.api.FullUser
import com.wabadaba.dziennik.db.DatabaseManager
import io.requery.Persistable
import io.requery.kotlin.BlockingEntityStore
import org.junit.Before
import org.robolectric.RuntimeEnvironment

abstract class BaseDBTest : BaseTest() {

    protected lateinit var dataStore: BlockingEntityStore<Persistable>
    protected lateinit var dbManager: DatabaseManager
    @Before
    fun setupDB() {
        dbManager = DatabaseManager(RuntimeEnvironment.application, FullUser(
                "testUsername",
                "testFirstName",
                "testLastName",
                5,
                AuthInfo("aToken", "rToken", 22)))
        dataStore = dbManager.dataStore.toBlocking()
    }

}