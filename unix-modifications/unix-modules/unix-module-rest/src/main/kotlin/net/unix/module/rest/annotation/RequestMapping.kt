package net.unix.module.rest.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RequestMapping(
    val requestType: RequestType,
    val additionalPath: String = "",
    val permission: String = ""
)