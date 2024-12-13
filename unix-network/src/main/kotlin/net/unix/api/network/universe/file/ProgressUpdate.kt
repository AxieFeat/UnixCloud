package net.unix.api.network.universe.file

data class ProgressUpdate(
    val uuid: String? = null,
    val progress: Int = 0,
    val speed: Long = 0L
)