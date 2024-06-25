package com.wkerein.espada

import com.wkerein.espada.Config.Server.host
import com.wkerein.espada.Config.Server.port
import com.wkerein.espada.plugins.CORS
import com.wkerein.espada.plugins.JWT
import com.wkerein.espada.plugins.interception
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.tomcat.*

fun main() {
    embeddedServer(Tomcat, port = port, host = host, module = {
        interception()
        CORS()
        JWT()
        routing {
            authenticate("basic-auth") {
                institutionController()
            }
            authenticationController()
        }
    }).start(wait = true)
}
