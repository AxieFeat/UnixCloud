package net.unix.launcher

import net.unix.center.NodeHandler
import net.unix.launcher.node.launchCloudInstance
import java.util.Scanner

fun main(args: Array<String>) {

    if(args.isNotEmpty()) {
        if (args[0] == "node") {
            launchCloudInstance()
            return
        }

        if (args[0] == "center") {
            NodeHandler.start(8181)
            return
        }
    }

    println("Select start mode: NODE, CENTER")

    val scanner = Scanner(System.`in`)

    while (true) {
        val line = scanner.nextLine()

        if(line == "NODE") {
            launchCloudInstance()
            return
        }

        if(line == "CENTER") {
            NodeHandler.start(8181)
            return
        }

        println("Select start mode: NODE, CENTER")
    }
}