package net.unix.module.rest.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class RequestParam(
    val parameterName: String,
    val required: Boolean = true
)