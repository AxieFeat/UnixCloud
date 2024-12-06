package net.unix.module.rest.controller

import io.javalin.http.Context
import net.unix.module.rest.annotation.*
import net.unix.module.rest.auth.user.User
import net.unix.module.rest.javalin.RestServer
import java.lang.reflect.Method
import java.lang.reflect.Parameter

class ControllerHandler(private val restServer: RestServer) {


    fun registerController(controller: Controller) {
        val javaClass = controller::class.java
        val restController = javaClass.getAnnotation(RestController::class.java)!!
        val methods = javaClass.declaredMethods
        val methodsWithAnnotation = methods.filter { it.isAnnotationPresent(RequestMapping::class.java) }

        methodsWithAnnotation.forEach {
            registerMethod(restController, it, controller)
        }
    }

    private fun registerMethod(restController: RestController, method: Method, controller: Controller) {
        val requestMapping = method.getAnnotation(RequestMapping::class.java)
        val parameterDataList = method.parameters.map { createParameterData(it) }
        val requestMethodData = RequestMethodData(
            controller,
            method,
            restController.path + requestMapping.additionalPath,
            requestMapping.requestType,
            requestMapping.permission,
            parameterDataList
        )
        restServer.registerRequestMethod(requestMethodData)
    }

    private fun createParameterData(parameter: Parameter): RequestMethodData.RequestParameterData {
        if (parameter.type == Context::class.java)
            return createContextParameterData()
        if (parameter.isAnnotationPresent(RequestingUser::class.java)) {
            if (parameter.type != User::class.java) {
                throw IllegalStateException("RequestingUser annotation can only be used on User")
            }
            val requestingUser = parameter.getAnnotation(RequestingUser::class.java)!!
            return createRequestingUserParameterData(requestingUser)
        }

        val requestParam = parameter.getAnnotation(RequestParam::class.java)
        if (requestParam != null) {
            return RequestMethodData.RequestParameterData(parameter.type, requestParam)
        }
        val requestBody = parameter.getAnnotation(RequestBody::class.java)
        if (requestBody != null) {
            return RequestMethodData.RequestParameterData(parameter.type, requestBody)
        }
        val requestPathParam = parameter.getAnnotation(RequestPathParam::class.java)
        if (requestPathParam != null) {
            return RequestMethodData.RequestParameterData(parameter.type, requestPathParam)
        }
        throw IllegalArgumentException("Parameter with type ${parameter.type.name} in not annotated with RequestBody, RequestParam or RequestPathParam")
    }

    private fun createRequestingUserParameterData(requestingUser: RequestingUser): RequestMethodData.RequestParameterData {
        return RequestMethodData.RequestParameterData(User::class.java, requestingUser)
    }

    private fun createContextParameterData(): RequestMethodData.RequestParameterData {
        return RequestMethodData.RequestParameterData(Context::class.java, null)
    }


}