package com.wabadaba.dziennik

import com.wabadaba.dziennik.vo.Models
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.cache.EntityCacheBuilder
import io.requery.sql.ConfigurationBuilder
import io.requery.sql.EntityDataStore
import io.requery.sql.TableCreationMode
import org.junit.Before
import org.robolectric.RuntimeEnvironment

abstract class BaseDBTest {

    protected lateinit var dataStore: EntityDataStore<Persistable>

    @Before
    fun setupDB() {
        val source = DatabaseSource(RuntimeEnvironment.application, Models.DEFAULT, "TEST_DB", 1)
        source.setTableCreationMode(TableCreationMode.DROP_CREATE)
        source.setLoggingEnabled(true)

        val configuration = ConfigurationBuilder(source, Models.DEFAULT)
                .setEntityCache(EntityCacheBuilder(Models.DEFAULT)
                        .useReferenceCache(false)
                        .build())
                .build()
        dataStore = EntityDataStore<Persistable>(configuration)
    }


}