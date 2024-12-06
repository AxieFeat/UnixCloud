package net.unix.module.rest.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import javalinjwt.JWTGenerator
import javalinjwt.JWTProvider
import net.unix.module.rest.auth.user.User
import java.util.*
import java.util.concurrent.TimeUnit

class JwtProvider(secret: String) {

    companion object {
        lateinit var instance: JwtProvider
    }

    private val jwtAlgorithm: Algorithm = Algorithm.HMAC512(secret)

    private val jwtGenerator = JWTGenerator { user: User, algorithm: Algorithm ->
        val token = JWT.create()
            .withClaim("username", user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)))

        token.sign(algorithm)
    }
    private val jwtVerifier: JWTVerifier = JWT.require(jwtAlgorithm).build()

    val provider = JWTProvider(jwtAlgorithm, jwtGenerator, jwtVerifier)

    init {
        instance = this
    }
}