package com.wkerein.espada

object Config {
    object Espada {
        val generatedPackageName = "generator.domain"
    }

    object Database {
        val url = ""
        val driver = "com.mysql.cj.jdbc.Driver"
        val user = "root"
        val password = ""
    }

    object Server {
        val port = 8089
        val host = "0.0.0.0"
    }

    object Security {
        val jwtSecret = ""
        val issuer = ""
        val validityPeriod = 36_000_000L * 24 * 15
        val encryptKeySpec = jwtSecret
    }
}