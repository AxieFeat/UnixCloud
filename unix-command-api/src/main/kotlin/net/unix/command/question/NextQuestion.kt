package net.unix.command.question

/**
 * This interface represents child question.
 */
interface NextQuestion<T> : Question<T> {

    /**
     * Parent question.
     */
    val previous: Question<*>

    /**
     * Kill question. All chain of questions will be cancelled.
     */
    fun kill()
}