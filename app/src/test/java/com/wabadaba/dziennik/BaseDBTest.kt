package com.wabadaba.dziennik

import com.wabadaba.dziennik.db.DatabaseManager
import io.requery.Persistable
import io.requery.sql.KotlinEntityDataStore
import org.junit.Before
import org.robolectric.RuntimeEnvironment

abstract class BaseDBTest {

    protected lateinit var dataStore: KotlinEntityDataStore<Persistable>

    @Before
    fun setupDB() {
        val dbManager = DatabaseManager(RuntimeEnvironment.application, "testlogin")
        dataStore = dbManager.dataStore
    }

}