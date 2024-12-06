package net.unix.module.rest.auth.controller

import io.javalin.http.Context
import javalinjwt.examples.JWTResponse
import net.unix.module.rest.annotation.RequestBody
import net.unix.module.rest.annotation.RequestMapping
import net.unix.module.rest.annotation.RequestType
import net.unix.module.rest.annotation.RestController
import net.unix.module.rest.auth.AuthService
import net.unix.module.rest.controller.Controller

@RestController("auth/")
class AuthController(
    private val authService: AuthService
) : Controller {

    @RequestMapping(RequestType.POST, "login/")
    fun handleLogin(@RequestBody login: LoginDto, context: Context) {
        val token = authService.handleLogin(login)
        if (token == null) {
            context.status(401)
            context.result("Username or password wrong!")
        } else {
            context.json(JWTResponse(token))
        }
    }

}