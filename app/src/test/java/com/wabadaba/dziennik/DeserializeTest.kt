package com.wabadaba.dziennik

import com.wabadaba.dziennik.vo.GradeCategory
import com.wabadaba.dziennik.vo.GradeCategoryEntity
import com.wabadaba.dziennik.vo.Color
import com.wabadaba.dziennik.vo.ColorEntity
import org.amshove.kluent.shouldContain
import org.junit.Test

class DeserializeTest :BaseParseTest(){
    @Test
    fun shouldDeserializeColors(){
        val colors = parseList("/Colors.json",Color::class)
        val expected = ColorEntity()
        expected.rawColor = "FF1493"
        expected.id = "25"
        expected.name = "deeppink"
        colors!! shouldContain expected
    }

    @Test
    fun shouldDeserializeCategories(){
        val categories = parseList("/GradeCategories.json", GradeCategory::class)

        val category = GradeCategoryEntity()
        category.id = "164148"
        category.name = "kartkówka"
        category.weight = 5

        val color = ColorEntity()
        color.id="16"
        category.color = color

        categories!! shouldContain category
    }
}