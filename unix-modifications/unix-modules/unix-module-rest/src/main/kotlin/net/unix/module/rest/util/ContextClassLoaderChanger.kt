package net.unix.module.rest.util

fun executeWithDifferentContextClassLoader(classLoader: ClassLoader, runnable: Runnable) {
    val currentContextClassLoader = Thread.currentThread().contextClassLoader
    changedContextClassLoader(classLoader)
    runnable.run()
    changedContextClassLoader(currentContextClassLoader)
}

private fun changedContextClassLoader(classLoader: ClassLoader) {
    Thread.currentThread().contextClassLoader = classLoader
}