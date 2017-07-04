package com.wabadaba.dziennik.db

import android.content.Context
import com.wabadaba.dziennik.vo.Models
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.cache.EntityCacheBuilder
import io.requery.sql.ConfigurationBuilder
import io.requery.sql.GenericMapping
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.TableCreationMode
import io.requery.sql.platform.SQLite

class DatabaseManager(context: Context, username: String) {
    val dataStore: KotlinEntityDataStore<Persistable>

    init {
        val source = DatabaseSource(context, Models.DEFAULT, username.databaseName, 1)
        source.setTableCreationMode(TableCreationMode.DROP_CREATE)
        source.setLoggingEnabled(true)

        val configuration = ConfigurationBuilder(source, Models.DEFAULT)
                .setEntityCache(EntityCacheBuilder(Models.DEFAULT)
                        .useReferenceCache(false)
                        .build())
                .setMapping(MainMapping)
                .build()
        dataStore = KotlinEntityDataStore<Persistable>(configuration)
    }

    private val String.databaseName get() = "user-data-" + this

    object MainMapping : GenericMapping(SQLite()) {
        init {
            addConverter(LocalDateConverter())
            addConverter(LocalTimeConverter())
            addConverter(LocalDateTimeConverter())
        }
    }
}