package com.wkerein.espada.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.CORS() = install(CORS) {
    allowMethod(HttpMethod.Options)
    allowMethod(HttpMethod.Get)
    allowMethod(HttpMethod.Post)
    allowMethod(HttpMethod.Put)
    allowMethod(HttpMethod.Delete)
    allowMethod(HttpMethod.Patch)
    allowHeaders { _ -> true }
    exposeHeader(HttpHeaders.Referrer)
    allowXHttpMethodOverride()
    allowSameOrigin = true
    allowCredentials = true
    allowNonSimpleContentTypes = true
}
