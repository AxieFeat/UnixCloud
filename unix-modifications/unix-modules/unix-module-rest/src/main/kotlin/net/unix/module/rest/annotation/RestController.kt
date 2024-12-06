package net.unix.module.rest.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class RestController(
    val path: String
)