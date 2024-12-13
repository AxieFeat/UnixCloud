package net.unix.api.network.universe.file

data class FileTransferChunk(
    val uuid: String? = null,
    val saveOn: String? = null,
    val name: String = "",
    val last: Boolean = false,
    val bytes: ByteArray = byteArrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileTransferChunk

        if (name != other.name) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}