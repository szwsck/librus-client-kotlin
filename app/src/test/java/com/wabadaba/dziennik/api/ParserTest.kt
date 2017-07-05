package com.wabadaba.dziennik.api

import com.wabadaba.dziennik.BaseParseTest
import com.wabadaba.dziennik.vo.Me
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBe
import org.junit.Test

class ParserTest : BaseParseTest() {

    class TestSubject(var name: String)

    @Test
    fun shouldParseSingleObject() {
        val testObject = parseList("/single-object.json", TestSubject::class)
        //then
        testObject.singleOrError().blockingGet().name shouldEqualTo "some name"
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
        val testObject = parseList("/Disabled.json", TestSubject::class)
        testObject.toList().blockingGet().size shouldEqualTo 0
    }

    @Test(expected = ParseException::class)
    fun shouldFailOnMalformed() {
        //when
        parseList("/Malformed.txt", TestSubject::class)
    }

    @Test
    fun shouldDeserializeEmbeddedEntity() {
        //when
        val testObject = parseList("/endpoints/Me.json", Me::class)
        val result = testObject.singleOrError().blockingGet()
        //then
        result.account.login shouldEqualTo "12u"
    }


}