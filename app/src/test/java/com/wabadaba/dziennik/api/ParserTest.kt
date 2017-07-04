package com.wabadaba.dziennik.api

import com.wabadaba.dziennik.BaseParseTest
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class ParserTest : BaseParseTest() {

    class TestSubject(var name: String)

    @Test
    fun shouldParseSingleObject() {
        val testObject = parse("/single-object.json", TestSubject::class)
        //then
        testObject.blockingGet().name shouldEqualTo "some name"
    }

    @Test
    fun shouldParseObjectList() {
        //when
        val testList = parseList("/object-list.json", TestSubject::class)
        //then
        testList.map { it.name }
                .test()
                .assertValues("name1", "name2")
    }

    @Test
    fun shouldNotFailOnDisabled() {
        val testObject = parse("/Disabled.json", TestSubject::class)
        testObject.blockingGet() shouldBe null
    }

    @Test(expected = ParseException::class)
    fun shouldFailOnMalformed() {
        //when
        parse("/Malformed.txt", TestSubject::class)
    }


}