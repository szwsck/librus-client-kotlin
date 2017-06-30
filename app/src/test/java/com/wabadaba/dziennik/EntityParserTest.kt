package com.wabadaba.dziennik

import com.wabadaba.dziennik.api.ParseException
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class EntityParserTest : BaseParseTest() {

    class TestSubject(var name: String)

    @Test
    fun shouldParseSingleObject() {
        val testObject = parse("/single-object.json", TestSubject::class)
        //then
        testObject!!.name shouldEqualTo "some name"
    }

    @Test
    fun shouldParseObjectList() {
        //when
        val testList = parseList("/object-list.json", TestSubject::class)
        //then
        testList!!.map { it.name } shouldEqual listOf("name1", "name2")
    }

    @Test
    fun shouldNotFailOnDisabled() {
        val testObject = parse("/Disabled.json", TestSubject::class)
        testObject shouldBe null
    }

    @Test(expected = ParseException::class)
    fun shouldFailOnMalformed() {
        //when
        parse("/Malformed.json", TestSubject::class)
    }


}