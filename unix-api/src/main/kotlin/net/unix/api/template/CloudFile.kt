package net.unix.api.template

import java.nio.file.Path

/**
 * File copy param's
 */
data class CloudFile(
    val from: Path,
    val to: Path
) {
    companion object
}