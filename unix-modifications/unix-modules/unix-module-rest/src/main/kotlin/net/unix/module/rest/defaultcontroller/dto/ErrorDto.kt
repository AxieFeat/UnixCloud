package net.unix.module.rest.defaultcontroller.dto

@Suppress("unused")
class ErrorDto(
    val errorClass: String,
    error: String?
) {

    val error = error ?: ""


    companion object {
        fun fromException(ex: Throwable): ErrorDto {
            return ErrorDto(ex::class.java.name, ex.message)
        }
    }

}