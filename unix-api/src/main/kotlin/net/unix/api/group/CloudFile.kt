package net.unix.api.group

import java.nio.file.Path

data class CloudFile(
    val from: Path,
    val to: Path
)