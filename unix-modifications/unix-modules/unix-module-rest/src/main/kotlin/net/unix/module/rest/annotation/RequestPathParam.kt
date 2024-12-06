package net.unix.module.rest.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class RequestPathParam(
    val parameterName: String
)