package com.wabadaba.dziennik.vo

import com.wabadaba.dziennik.BaseDBTest
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RelationalLinkTest : BaseDBTest() {

    @Test
    fun manyToOne() {
        val colorId = "test-id"

        val idColor = ColorEntity()
        idColor.setId(colorId)

        val originalColor = ColorEntity()
        originalColor.setName("test")
        originalColor.setId(colorId)
        originalColor.setRawColor("FF00FF")
        dataStore.upsert(originalColor)

        val cat = GradeCategoryEntity()
        cat.setId("cat-id")
        cat.setColor(idColor)
        dataStore.upsert(cat)

        val readCat = dataStore.findByKey(GradeCategory::class, cat.id)

        readCat!!.color!!.rawColor shouldEqual "FF00FF"
    }

    @Test
    fun oneToMany() {
        val gradeId = "gradeID"
        val gradeValue = "3+"

        val idGrade = GradeEntity()
        idGrade.setId(gradeId)

        val grade = GradeEntity()
        grade.setId(gradeId)
        grade.setGrade(gradeValue)
        dataStore.upsert(grade)

        val comment1 = GradeCommentEntity()
        comment1.setId("id1")
        comment1.setText("comment 1")
        comment1.setGrade(idGrade)
        dataStore.upsert(comment1)

        val comment2 = GradeCommentEntity()
        comment2.setId("id2")
        comment2.setText("comment 2")
        comment2.setGrade(idGrade)
        dataStore.upsert(comment2)

        val readGrade = dataStore.findByKey(Grade::class, gradeId)

        readGrade!!.comments.size shouldEqual 2
        readGrade.grade shouldEqual gradeValue
        readGrade.id shouldEqual gradeId
    }

    @Test
    fun multiLevel() {
        val teacherId = "321"
        val teacherFirstName = "f"
        val teacher = TeacherEntity().apply {
            setId(teacherId)
            setFirstName(teacherFirstName)
            setLastName("l")
        }

        val idTeacher = TeacherEntity().apply { setId(teacherId) }

        val gradeId = "gradeid"

        val grade = GradeEntity().apply {
            setId(gradeId)
        }

        val commentId = "321321"
        val comment = GradeCommentEntity().apply {
            setId(commentId)
            setAddedBy(idTeacher)
            setGrade(grade)
        }

        dataStore.upsert(teacher)
        dataStore.upsert(grade)
        dataStore.upsert(comment)

        val foundGrade = dataStore.findByKey(Grade::class, gradeId)

        foundGrade!!.comments.size shouldEqual 1
        foundGrade.comments.first().addedBy!!.firstName shouldEqual teacherFirstName
    }

    @Test
    fun emptyOneToMany() {
        val grade = GradeEntity()
        val gradeId = "gradeid"
        grade.setId(gradeId)
        dataStore.upsert(grade)

        val foundGrade = dataStore.findByKey(Grade::class, gradeId)

        foundGrade!!.comments shouldNotEqual null
        foundGrade.comments.size shouldEqual 0
    }
}