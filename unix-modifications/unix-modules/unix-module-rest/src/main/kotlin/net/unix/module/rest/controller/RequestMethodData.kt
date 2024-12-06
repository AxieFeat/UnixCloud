package net.unix.module.rest.controller

import net.unix.module.rest.annotation.RequestType
import java.lang.reflect.Method

class RequestMethodData(
    val controller: Controller,
    val method: Method,
    val path: String,
    val requestType: RequestType,
    val permission: String,
    val parameters: List<RequestParameterData>
) {

    class RequestParameterData(
        val parameterType: Class<*>,
        val annotation: Annotation?
    )

}