package net.unix.launcher

import net.unix.manager.NodeHandler
import net.unix.launcher.node.launchCloudInstance
import java.util.Scanner

fun main(args: Array<String>) {

    if(args.isNotEmpty()) {

        var started = false

        if(args.contains("manager")) {
            NodeHandler.start(8181)
            started = true
        }
        if(args.contains("node")) {
            launchCloudInstance()
            started = true
        }

        if(started) return
    }

    println("Select start mode: NODE, MANAGER")

    val scanner = Scanner(System.`in`)

    while (true) {
        val line = scanner.nextLine()

        if(line == "NODE") {
            launchCloudInstance()
            return
        }

        if(line == "MANAGER") {
            NodeHandler.start(8181)
            return
        }

        println("Select start mode: NODE, MANAGER")
    }
}