package net.unix.example.app

import net.unix.driver.JVMServiceInstance
import java.util.Scanner

fun main() {
    JVMServiceInstance.install()

    val scanner = Scanner(System.`in`)

    println("Your write: ${scanner.nextLine()}")
}