package com.wabadaba.dziennik

import com.wabadaba.dziennik.vo.Grade
import com.wabadaba.dziennik.vo.GradeEntity
import org.joda.time.LocalDate
import org.junit.Test

class InMemoryEntityStoreTest {

    private val grade1: Grade = GradeEntity().apply {
        setId("grade1")
        setDate(LocalDate.now())
    }
    private val grade2: Grade = GradeEntity().apply {
        setId("grade2")
        setDate(LocalDate.now())
    }

    @Test
    fun shouldUpsertAndSelect() {
        val datastore = InMemoryEntityStore.getDatastore()
        datastore.upsert(listOf(grade1, grade2))
        val testObserver = datastore.select(Grade::class)
                .get().observable()
                .test()
        testObserver.awaitCount(2)
        testObserver.assertValues(grade1, grade2)
    }
}