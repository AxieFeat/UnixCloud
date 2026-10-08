@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package net.unix.module.rest.javalin

import io.javalin.Javalin
import io.javalin.util.legacy.legacyAccessManager
import javalinjwt.JWTAccessManager
import javalinjwt.JavalinJWT
import net.unix.module.rest.annotation.RequestType
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

        app = Javalin.create { config ->
            config.routes.before(JavalinJWT.createHeaderDecodeHandler(JwtProvider.instance.provider))
            config.routes.before { ctx ->
                ctx.header("Access-Control-Allow-Headers", "*")
                ctx.header("Access-Control-Allow-Origin", "*")
                ctx.header("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS")
                ctx.header("Content-Type", "application/json; charset=utf-8")
            }
            config.routes.options("/*", {
                it.status(200)
            }, Roles.ANYONE)
        }
        val accessManager = JWTAccessManager("role", createRolesMapping(), Roles.ANYONE)
        app.legacyAccessManager { handler, ctx, _ ->
            accessManager.handle(ctx)
            handler.handle(ctx)
        }

        controllerHandler.registerController(AuthController(this.authService))
        controllerHandler.registerController(UserController(this.authService))
        controllerHandler.registerController(TemplateController())
        controllerHandler.registerController(GroupController())
        controllerHandler.registerController(GroupActionController())
        controllerHandler.registerController(ServiceController())
        controllerHandler.registerController(ServiceActionController())
        controllerHandler.registerController(FileManagerController())

        app.start(port)
    }

    fun registerRequestMethod(requestMethodData: RequestMethodData) {
        val requestHandler = JavalinRequestHandler(requestMethodData, authService)
        addToJavalin(requestHandler)
    }

    private fun addToJavalin(requestHandler: JavalinRequestHandler) {
        val requestMethodData = requestHandler.requestMethodData
        with(app.unsafe.routes) {
            when (requestMethodData.requestType) {
                RequestType.GET ->
                    get(requestMethodData.path, requestHandler, Roles.ANYONE)
                RequestType.PUT ->
                    put(requestMethodData.path, requestHandler, Roles.ANYONE)
                RequestType.POST ->
                    post(requestMethodData.path, requestHandler, Roles.ANYONE)
                RequestType.DELETE ->
                    delete(requestMethodData.path, requestHandler, Roles.ANYONE)
            }
        }
    }

    fun shutdown() {
        app.stop()
    }
}