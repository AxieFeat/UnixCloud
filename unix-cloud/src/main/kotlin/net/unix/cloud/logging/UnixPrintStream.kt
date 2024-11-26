package net.unix.cloud.logging

import java.io.PrintStream

object UnixPrintStream : PrintStream(System.out) {
}