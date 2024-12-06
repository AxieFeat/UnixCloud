package net.unix.module.rest.javalin

import com.auth0.jwt.interfaces.DecodedJWT
import io.javalin.http.Context
import io.javalin.http.Handler
import javalinjwt.JavalinJWT
import net.unix.module.rest.auth.AuthService
import net.unix.module.rest.auth.JwtProvider
import net.unix.module.rest.auth.user.User
import net.unix.module.rest.controller.RequestMethodData
import net.unix.module.rest.defaultcontroller.dto.ErrorDto
import net.unix.module.rest.jsonlib.JsonLib
import java.lang.reflect.InvocationTargetException
import java.util.Optional

class JavalinRequestHandler(
    val requestMethodData: RequestMethodData,
    private val authService: AuthService
) : Handler {

    override fun handle(ctx: Context) {
        val user = getUserByContext(ctx)

        if (!isPermitted(user)) {
            ctx.result("Unauthorized")
            ctx.status(401)
            return
        }

        try {
            SingleRequestProcessor(ctx, requestMethodData, user).processRequest()
        } catch (ex: Exception) {
            val exception = if (ex is InvocationTargetException) ex.cause!! else ex
            ctx.status(500)
            val json = JsonLib.fromObject(ErrorDto.fromException(exception))
            ctx.result(json.getAsJsonString())

            ex.printStackTrace()
        }
    }

    private fun isPermitted(user: User?): Boolean {
        return user?.hasPermission(this.requestMethodData.permission) ?: requestMethodData.permission.isEmpty()
    }

    private fun getUserByContext(context: Context): User? {
        val decodedJWTOptional: Optional<DecodedJWT> = JavalinJWT.getTokenFromHeader(context)
            .flatMap(JwtProvider.instance.provider::validateToken) as Optional<DecodedJWT>

        if (!decodedJWTOptional.isPresent) {
            return null
        }

        val decodedJWT = decodedJWTOptional.get()

        val username = decodedJWT.getClaim("username").asString()
        return authService.getUserByName(username)
    }

}