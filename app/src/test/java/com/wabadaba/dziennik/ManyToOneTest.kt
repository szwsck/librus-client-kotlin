package com.wabadaba.dziennik

import com.wabadaba.dziennik.vo.GradeCategory
import com.wabadaba.dziennik.vo.GradeCategoryEntity
import com.wabadaba.dziennik.vo.ColorEntity
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ManyToOneTest : BaseDBTest() {

    @Test
    fun test() {
        val originalColor = ColorEntity()
        originalColor.name = "test"
        originalColor.id = "test-id"
        originalColor.rawColor = "FF00FF"

        dataStore.upsert(originalColor)

        val idColor = ColorEntity()
        idColor.id = originalColor.id
        val cat = GradeCategoryEntity()
        cat.id = "cat-id"
        cat.color = idColor

        dataStore.upsert(cat)

        val readCat = dataStore.findByKey(GradeCategory::class.java, cat.id)

        readCat.color shouldEqual originalColor
        readCat.color shouldNotEqual idColor
    }
}