package net.unix.cloud.logging

import java.util.logging.Level

object LogType {
    @JvmStatic
    val ERROR = CloudLevel("ERROR", 1)

    @JvmStatic
    val WARN = CloudLevel("WARN", 2)

    @JvmStatic
    val INFO = CloudLevel("INFO", 3)

    @JvmStatic
    val DEBUG = CloudLevel("DEBUG", 4)

    @JvmStatic
    val SERVICE = CloudLevel("SERVICE", 5)
}

class CloudLevel(
    name: String,
    value: Int
): Level(name, value)