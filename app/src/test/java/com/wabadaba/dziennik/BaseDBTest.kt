package com.wabadaba.dziennik

import com.wabadaba.dziennik.api.AuthInfo
import com.wabadaba.dziennik.api.FullUser
import com.wabadaba.dziennik.db.DatabaseManager
import io.reactivex.Observable
import io.requery.Persistable
import io.requery.sql.KotlinEntityDataStore
import org.junit.Before
import org.robolectric.RuntimeEnvironment

abstract class BaseDBTest : BaseTest() {

    protected lateinit var dataStore: KotlinEntityDataStore<Persistable>

    @Before
    fun setupDB() {
        val dbManager = DatabaseManager(RuntimeEnvironment.application, Observable.just(FullUser(
                "testUsername",
                "testFirstName",
                "testLastName",
                5,
                AuthInfo("aToken", "rToken", 22))))
        dataStore = dbManager.dataStore
    }

}