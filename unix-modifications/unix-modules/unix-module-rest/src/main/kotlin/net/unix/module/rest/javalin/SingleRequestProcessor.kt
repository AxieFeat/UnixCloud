package net.unix.module.rest.javalin

import io.javalin.http.Context
import net.unix.module.rest.annotation.RequestBody
import net.unix.module.rest.annotation.RequestParam
import net.unix.module.rest.annotation.RequestPathParam
import net.unix.module.rest.annotation.RequestingUser
import net.unix.module.rest.auth.user.User
import net.unix.module.rest.controller.RequestMethodData
import net.unix.module.rest.defaultcontroller.dto.ErrorDto
import net.unix.module.rest.defaultcontroller.dto.ResultDto
import net.unix.module.rest.exception.NullResultException
import net.unix.module.rest.jsonlib.JsonLib
import java.lang.reflect.InvocationTargetException

class SingleRequestProcessor(
    private val ctx: Context,
    private val requestMethodData: RequestMethodData,
    private val requestingUser: User?
) {

    fun processRequest() {
        val parameterDataToInvokeValue = try {
            handleParameters()
        } catch (e: Exception) {
            handleClientFaultException(e)
            return
        }

        val valueArray = parameterDataToInvokeValue.values.toTypedArray()

        val result = try {
            this.requestMethodData.method.invoke(this.requestMethodData.controller, *valueArray)
        } catch (e: Exception) {
            val ex = if (e is InvocationTargetException) e.cause!! else e
            handleClientFaultException(ex)
            return
        }

        //then the result was set by the called method
        if (this.ctx.resultString() != null) {
            return
        }

        if (result == null) {
            throw NullResultException()
        }

        if (result == Unit) {
            return
        }

        ctx.status(200)
        val resultDto = ResultDto(result)
        ctx.result(JsonLib.fromObject(resultDto, RestServer.webGson).getAsJsonString())
    }

    private fun handleClientFaultException(ex: Throwable) {
        ctx.status(400)
        ctx.result(JsonLib.fromObject(ErrorDto.fromException(ex)).getAsJsonString())
    }

    private fun handleParameters(): Map<RequestMethodData.RequestParameterData, Any?> {
        val parameterDataToInvokeValue = requestMethodData.parameters.associateWith { handleValueForParameter(it) }

        if (parameterDataToInvokeValue.any { isValueIncorrect(it.key, it.value) }) {
            throw IncorrectValueException("A value is incorrect")
        }
        return parameterDataToInvokeValue
    }

    private fun isValueIncorrect(parameterData: RequestMethodData.RequestParameterData, value: Any?): Boolean {
        val annotation = parameterData.annotation
        if (annotation is RequestParam) {
            return annotation.required && value == null
        }
        if (annotation is RequestPathParam) {
            return value == null
        }
        return false
    }


    private fun handleValueForParameter(parameterData: RequestMethodData.RequestParameterData): Any? {
        if (parameterData.parameterType == Context::class.java) {
            return this.ctx
        }
        if (parameterData.parameterType == User::class.java && parameterData.annotation is RequestingUser) {
            return this.requestingUser
        }

        when (val annotation = parameterData.annotation!!) {
            is RequestBody -> {
                return JsonLib.fromJsonString(this.ctx.body(), RestServer.webGson)
                    .getObject(parameterData.parameterType)
            }
            is RequestParam -> {
                val parameter = this.ctx.req.getParameter(annotation.parameterName) ?: return null
                return JsonLib.fromJsonString(parameter).getObject(parameterData.parameterType)
            }
            is RequestPathParam -> {
                val pathParam = this.ctx.pathParam(annotation.parameterName)
                return JsonLib.fromJsonString(pathParam).getObject(parameterData.parameterType)
            }
        }
        throw IllegalStateException()
    }

    class IncorrectValueException(message: String) : Exception(message)

}