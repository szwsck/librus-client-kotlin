package com.wabadaba.dziennik.vo

import com.wabadaba.dziennik.BaseDBTest
import com.wabadaba.dziennik.api.Parser
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

/**
 * Generic test, checking if all entities are properly parsed, saved in DB and retrieved by id.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
class EntitiesTest(
        val className: String,
        val endpoint: String,
        @Suppress("unused")
        val name: String) : BaseDBTest() {

    companion object {
        fun KClass<out Any>.findEndpoint() = this.findAnnotation<LibrusEntity>()?.endpoint
                ?: throw AssertionError("Class ${this.simpleName} not annotated with LibrusEntity annotation")

        @JvmStatic
        @Suppress("unused")
        @ParameterizedRobolectricTestRunner.Parameters(name = "{2}")
        fun data(): Collection<Array<out Any>> {
            return Models.DEFAULT
                    .types
                    .map { it.baseType.kotlin }
                    .filter { it != Me::class } //FIXME At this point you cannot deserialize embedded entity
                    .map {
                        arrayOf(it.qualifiedName!!,
                                it.findEndpoint(),
                                it.simpleName!!)
                    }
        }
    }

    @Test
    fun checkEntityClass() {
        val inputClass = this::class.java.classLoader.loadClass(className).kotlin
        inputClass.isSubclassOf(Identifiable::class) shouldBe true
        @Suppress("UNCHECKED_CAST")
        val clazz = inputClass as KClass<out Identifiable>
        val file = readFile("/endpoints/$endpoint.json")
        val parsedList = Parser.parseEntityList(file, clazz.java)
                .toList().blockingGet()
        parsedList.size shouldBeGreaterThan 0
        parsedList.forEach { original ->
            println(original)
            val inserted = dataStore.upsert(original)
            inserted shouldEqual original
            val found = dataStore.findByKey(clazz, original.id)
            found shouldEqual original
            found shouldNotBe original
        }
    }
}