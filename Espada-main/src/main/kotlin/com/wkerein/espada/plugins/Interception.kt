package com.wkerein.espada.plugins

import com.wkerein.espada.core.Espada.devolve
import com.wkerein.espada.core.Espada.receiveJson
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.*


val DataKey = AttributeKey<Map<String, String>>("ReceivedData")

fun Application.interception() {
    intercept(ApplicationCallPipeline.Plugins) {
        val receivedData = call.receiveJson().devolve()
        call.attributes.put(DataKey, receivedData)
    }
}

val RoutingContext.data: Map<String, String>
    get() = call.attributes[DataKey]
