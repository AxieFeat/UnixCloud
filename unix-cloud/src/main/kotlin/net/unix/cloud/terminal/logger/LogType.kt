package net.unix.cloud.terminal.logger

enum class LogType(
    var logName: String
) {
    ERROR("ERROR"), WARN("WARN"), INFO("INFO")
}