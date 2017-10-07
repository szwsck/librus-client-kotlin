package com.wabadaba.dziennik.db

import android.content.Context
import com.wabadaba.dziennik.BuildConfig
import com.wabadaba.dziennik.api.FullUser
import com.wabadaba.dziennik.vo.Models
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.cache.EntityCacheBuilder
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.ConfigurationBuilder
import io.requery.sql.GenericMapping
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.TableCreationMode
import io.requery.sql.platform.SQLite

open class DatabaseManager(
        context: Context,
        user: FullUser) {

    val dataStore: KotlinReactiveEntityStore<Persistable>

    init {
        val source = DatabaseSource(context, Models.DEFAULT, user.login.databaseName, BuildConfig.VERSION_CODE)
        source.setTableCreationMode(TableCreationMode.DROP_CREATE)
        source.setLoggingEnabled(true)

        val configuration = ConfigurationBuilder(source, Models.DEFAULT)
                .setEntityCache(EntityCacheBuilder(Models.DEFAULT)
                        .useReferenceCache(false)
                        .build())
                .setMapping(MainMapping)
                .build()
        val blockingDataStore = KotlinEntityDataStore<Persistable>(configuration)
        dataStore = KotlinReactiveEntityStore(blockingDataStore)
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