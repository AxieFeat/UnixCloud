package net.unix.command.question

interface NextQuestion<T> : Question<T> {
    val previous: Question<*>

    /**
     * Kill question. All chain of questions will be cancelled.
     */
    fun kill()
}