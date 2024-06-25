package com.wkerein.espada.core

import com.wkerein.espada.Config.Security.encryptKeySpec
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.util.Base64

object Security {
    @OptIn(ExperimentalStdlibApi::class)
    val String.PBKDF2: String
        inline get() = randomSalt.let { "${pbkdf2(it)}-${it.toHexString()}" }

    fun String.pbkdf2(
        salt: ByteArray = randomSalt,
        iterationCount: Int = 65536,
        keyLength: Int = 256
    ): String {
        val spec = PBEKeySpec(
            encryptKeySpec.toCharArray(),
            salt,
            iterationCount,
            keyLength * 8
        )
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(hash)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun String.pbkdf2(
        salt: String,
        iterationCount: Int = 65536,
        keyLength: Int = 256
    ) = pbkdf2(salt.hexToByteArray(), iterationCount, keyLength)

    val String.randomSalt: ByteArray
        inline get() = run {
            val s = ByteArray(length)
            SecureRandom().nextBytes(s)
            s
        }

    val String.MD5
        get(): String = buildList<String> {
            MessageDigest
                .getInstance("MD5")
                .digest(toByteArray())
                .forEach { b ->
                    Integer.toHexString(b.toInt() and 0xff)
                        .let { add(if (it.length < 2) "0$it" else it) }
                }
        }.joinToString("")
}
