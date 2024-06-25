package com.wkerein.espada.plugins

import com.auth0.jwt.*
import com.auth0.jwt.algorithms.*
import com.wkerein.espada.Config.Security.issuer
import com.wkerein.espada.Config.Security.jwtSecret
import com.wkerein.espada.Config.Security.validityPeriod
import com.wkerein.espada.getAccount
import com.wkerein.espada.core.Espada.throwsNull
import generator.domain.Account
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

fun Application.JWT() {
    install(Authentication) {
        jwt("basic-auth") {
            verifier(Verifier.verifier)
            validate(throwsNull {
                getAccount(it.payload.getClaim("account").asLong())?.principal
            })
        }
    }
}

object Verifier {
    private val secret = jwtSecret

    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    private val Int.day: Long inline get() = this * validityPeriod
    fun generateToken(user: Account): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("account", user.account)
        .withClaim("authority", user.authority)
        .withExpiresAt(Date(System.currentTimeMillis() + 15.day))
        .sign(algorithm)
}

val Account.principal get() = AccountPrincipal(this)

class AccountPrincipal(usr: Account) : Principal {
    val account = usr.account
    val authority = usr.authority
}
