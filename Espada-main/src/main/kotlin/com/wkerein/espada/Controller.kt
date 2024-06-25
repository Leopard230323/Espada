package com.wkerein.espada

import com.wkerein.espada.Config.Security.validityPeriod
import com.wkerein.espada.plugins.AccountPrincipal
import com.wkerein.espada.plugins.Verifier.generateToken
import com.wkerein.espada.plugins.data
import com.wkerein.espada.core.Espada.asInt
import com.wkerein.espada.core.Espada.assertAsInt
import com.wkerein.espada.core.Espada.collect
import com.wkerein.espada.core.Espada.encoded
import com.wkerein.espada.core.Espada.postRecall
import com.wkerein.espada.core.Espada.recall
import com.wkerein.espada.core.Security.PBKDF2
import com.wkerein.espada.core.Security.pbkdf2
import generator.domain.Account
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*

fun Route.institutionController() = postRecall("/institution") {
    if (call.principal<AccountPrincipal>()!!.authority lowerThan "junior") recall(Unauthorized)
    queryInstitutions(
        iName = data["name"], year = data["year"].asInt(),
        quarter = data["quarter"].asInt(), area = data["area"],
        method = Methods.Institutions.entries[data["method"].assertAsInt()],
    ).collect().encoded()
}

@OptIn(ExperimentalStdlibApi::class)
fun Route.authenticationController() {
    postRecall("/auth/login") {
        val account = getAccount(data["account"]!!.toLong())!!
        val hash = account.password.substringBefore('-')
        val salt = account.password.substringAfter('-').hexToByteArray()
        if (data["password"]?.pbkdf2(salt) != hash) recall(Unauthorized)
        else {
            call.response.cookies.append(
                name = "token",
                value = generateToken(account),
                maxAge = validityPeriod,
                domain = call.request.origin.serverHost,
                path = "/"
            )
            "success"
        }
    }

    postRecall("/auth/register") {
        Account {
            account = data["account"]!!.toLong()
            password = data["password"]!!.PBKDF2
            authority = "none"
        }.let {
            putAccount(it)
            call.response.cookies.append(
                name = "token",
                value = generateToken(it),
                maxAge = validityPeriod,
                domain = call.request.origin.serverHost,
                path = "/"
            )
            "success"
        }
    }
}

infix fun String.lowerThan(other: String): Boolean {
    fun String.value() = when (this) {
        "none" -> 0
        "junior" -> 1
        "senior" -> 2
        "superuser" -> 3
        else -> throw RuntimeException("Illegal privilege class")
    }
    return this.value() < other.value()
}

