package net.unix.module.rest.javalin

import io.javalin.Javalin
import io.javalin.core.security.SecurityUtil
import io.javalin.http.HandlerType
import javalinjwt.JWTAccessManager
import javalinjwt.JavalinJWT
import net.unix.module.rest.annotation.WebExclude
import net.unix.module.rest.auth.AuthService
import net.unix.module.rest.auth.JwtProvider
import net.unix.module.rest.auth.Roles
import net.unix.module.rest.auth.controller.AuthController
import net.unix.module.rest.auth.createRolesMapping
import net.unix.module.rest.controller.ControllerHandler
import net.unix.module.rest.controller.RequestMethodData
import net.unix.module.rest.defaultcontroller.UserController
import net.unix.module.rest.defaultcontroller.filemanager.FileManagerController
import net.unix.module.rest.defaultcontroller.group.GroupActionController
import net.unix.module.rest.defaultcontroller.group.GroupController
import net.unix.module.rest.defaultcontroller.service.ServiceActionController
import net.unix.module.rest.defaultcontroller.service.ServiceController
import net.unix.module.rest.defaultcontroller.template.TemplateController
import net.unix.module.rest.jsonlib.GsonCreator

object RestServer {

    private val authService = AuthService()

    val controllerHandler = ControllerHandler(this)

    private lateinit var app: Javalin

    val webGson = GsonCreator().excludeAnnotations(WebExclude::class.java).create()

    fun start(port: Int) {

        app = Javalin.create().start(port)

        app.config.accessManager(JWTAccessManager("role", createRolesMapping(), Roles.ANYONE))
        app.before(JavalinJWT.createHeaderDecodeHandler(JwtProvider.instance.provider))
        app.before { ctx ->
            ctx.header("Access-Control-Allow-Headers", "*")
            ctx.header("Access-Control-Allow-Origin", "*")
            ctx.header("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS")
            ctx.header("Content-Type", "application/json; charset=utf-8")
        }

        app.options("/*", {
            it.status(200)
        }, SecurityUtil.roles(Roles.ANYONE))

        controllerHandler.registerController(AuthController(this.authService))
        controllerHandler.registerController(UserController(this.authService))
        controllerHandler.registerController(TemplateController())
        controllerHandler.registerController(GroupController())
        controllerHandler.registerController(GroupActionController())
        controllerHandler.registerController(ServiceController())
        controllerHandler.registerController(ServiceActionController())
        controllerHandler.registerController(FileManagerController())
    }

    fun registerRequestMethod(requestMethodData: RequestMethodData) {
        val requestHandler = JavalinRequestHandler(requestMethodData, authService)
        addToJavalin(requestHandler)
    }

    private fun addToJavalin(requestHandler: JavalinRequestHandler) {
        val requestMethodData = requestHandler.requestMethodData
        app.addHandler(
            HandlerType.valueOf(requestMethodData.requestType.name),
            requestMethodData.path,
            requestHandler,
            setOf(Roles.ANYONE)
        )
    }

    fun shutdown() {
        app.stop()
    }
}