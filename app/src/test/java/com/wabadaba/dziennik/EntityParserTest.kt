package com.wabadaba.dziennik

import com.wabadaba.dziennik.api.EntityParser
import com.wabadaba.dziennik.api.ParseException
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import kotlin.reflect.KClass

class EntityParserTest {

    class TestSubject(var name: String)

    @Test
    fun shouldParseSingleObject() {
        val testObject = parse("/single-object.json", TestSubject::class)
        //then
        testObject!!.name shouldEqualTo "some name"
    }

    @Test
    fun shouldParseObjectList() {
        //given
        val file = readFile("/object-list.json")
        //when
        val testList = EntityParser.parseList(file, TestSubject::class.java)
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

    private fun <T : Any> parse(filename: String, clazz: KClass<T>) = EntityParser.parseObject(readFile(filename), clazz.java)

    private fun readFile(filename: String): String {
        return this.javaClass.getResource(filename).readText()
    }
}