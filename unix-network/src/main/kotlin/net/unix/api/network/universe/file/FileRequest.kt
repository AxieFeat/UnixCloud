package net.unix.api.network.universe.file

data class FileRequest(
    val uuid: String? = null,
    val path: String? = null,
    val saveOn: String? = null,
)