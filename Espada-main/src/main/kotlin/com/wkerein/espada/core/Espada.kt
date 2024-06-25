package com.wkerein.espada.core

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.wkerein.espada.Config.Espada.generatedPackageName
import com.wkerein.espada.core.Espada.firstLowercase
import com.wkerein.espada.core.Espada.firstUppercase
import com.wkerein.espada.core.Espada.toCamel
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.intellij.lang.annotations.Language
import org.ktorm.dsl.*
import org.ktorm.schema.BaseTable
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.valueParameters

object Espada {
    fun <C, P, R> throwsNull(fn: C.(P) -> R): C.(P) -> R? = { p: P ->
        try {
            this.fn(p)
        } catch (e: Exception) {
            null
        }
    }

    infix fun <C, P, R> Function<Any?>.throwsNull(fn: C.(P) -> R): C.(P) -> R? = { p: P ->
        try {
            this.fn(p)
        } catch (e: Exception) {
            null
        }
    }

    operator fun <T> List<T>.get(iter: IntProgression) = buildList {
        try {
            if (iter.first >= 0 && iter.last >= 0) iter.forEach { add(this@get[it]) }
            else if (iter.first < 0 && iter.last < 0) iter.forEach { add(this@get[size + it]) }
            else {
                var pos = if (iter.first >= 0) iter.first else iter.last
                val neg = if (iter.first < 0) iter.first else iter.last
                while (pos <= this@get.size + neg) {
                    add(this@get[pos])
                    pos -= iter.step
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            return@buildList
        }
    }

    fun <T> Iterator<T>.first() = next()

    fun <T> Iterator<T>.firstOrNull() = next()

    fun Query.firstOrNull(): QueryRowSet? {
        for (i in this) return i
        return null
    }

    @JvmField
    val parser = Gson()

    @JvmStatic
    fun String.intoJson(): JsonObject {
        return parser.fromJson(this, JsonObject::class.java) ?: JsonObject()
    }

    @JvmStatic
    fun <K, V> Map<K, V>.asJson(): JsonObject {
        return parser.toJson(this).intoJson()
    }

    @JvmStatic
    fun Map<String, JsonElement>.devolve() = buildMap<String, String> {
        (this@devolve.map { s -> s.key to s.value.asString }).forEach { put(it.first, it.second) }
    }

    @JvmStatic
    val JsonObject.jsonString: String get() = (JsonObject::toString)(this)

    @JvmStatic
    fun String.flatten() = this.intoJson().asMap()

    fun QuerySource.select(vararg tables: BaseTable<*>): Query = select(buildList {
        tables.forEach { addAll(it.columns) }
    })

    fun kotlinx.serialization.json.JsonObject.encoded() = Json.encodeToString(
        kotlinx.serialization.json.JsonObject.serializer(),
        this
    )

    fun kotlinx.serialization.json.JsonArray.encoded() = Json.encodeToString(
        kotlinx.serialization.json.JsonArray.serializer(),
        this
    )

    fun Query.collect() = buildJsonArray {
        forEach { row ->
            add(row.toJsonObject())
        }
        this@collect.rowSet.beforeFirst()
    }

    fun QueryRowSet.toJsonObject() = buildJsonObject {
        for (i in 1..metaData.columnCount) {
            val classTable: KClass<*> =
                Class.forName(
                    "${generatedPackageName}.${
                        metaData.getTableName(i).firstUppercase()
                    }s"
                ).kotlin
            val propName = metaData.getColumnName(i).toCamel().firstLowercase()
            val singleton: Any = classTable.java.getField("INSTANCE")[null]

            val target = classTable.memberProperties.find { it.name == propName } as? KProperty1<Any?, *>
            val prop = target?.get(singleton)
            val getFunc = this@toJsonObject::class.members
                .filterIsInstance<KFunction<*>>()
                .filter { it.name == "get" }
                .firstOrNull { it.valueParameters[0].type.classifier == target?.returnType?.classifier }

            when (val receiver = getFunc?.call(this@toJsonObject, prop)) {
                null -> {}
                is Number -> put(metaData.getColumnName(i), receiver)
                is Boolean -> put(metaData.getColumnName(i), receiver)
                is Date -> put(metaData.getColumnName(i), receiver.time)
                else -> put(metaData.getColumnName(i), receiver.toString())
            }
        }
    }

    @JvmStatic
    fun <T> List<T>.byFirst(fn: (T) -> Unit) = if (this.isNotEmpty()) fn(this[0]) else Unit

    @JvmStatic
    fun <T> List<T>.associateByIndex() = buildMap { forEachIndexed { it, index -> put(it, index) } }

    @JvmStatic
    suspend fun JsonObject.submit(target: RoutingContext) {
        target.call.respond(this)
    }

    @JvmStatic
    suspend fun Any.submit(target: RoutingContext) {
        target.call.respond(this)
    }

    @JvmStatic
    suspend fun ApplicationCall.receiveJson() = this.receive<String>().flatten()

    @JvmStatic
    fun String?.assertAsInt() = this?.toInt()!!

    @JvmStatic
    fun String?.asInt() = this?.toIntOrNull()

    @JvmStatic
    fun String.firstUppercase() = this.replaceFirstChar { it.uppercase() }

    @JvmStatic
    fun String.firstLowercase() = this.replaceFirstChar { it.lowercase() }

    @JvmStatic
    fun String.toCamel() = this.split('_').mapIndexed { index, s ->
        if (index != 0) s.firstUppercase() else s
    }.joinToString("")

    inline fun <reified T : Any> Route.postRecall(
        path: String, crossinline body: suspend RoutingContext.() -> T
    ) = this.post(path) {
        try {
            call.respond(body())
        } catch (e: Exception) {
            call.response.status(HttpStatusCode.BadRequest)
        }
    }

    suspend inline fun RoutingContext.recall(code: HttpStatusCode) {
        call.response.status(code)
        call.respond("")
    }

    suspend inline fun <reified T : Any> RoutingContext.recall(code: HttpStatusCode, message: T) {
        call.response.status(code)
        call.respond(message)
    }
}