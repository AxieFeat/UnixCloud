package net.unix.command.question

/**
 * This interface represents child question.
 */
interface NextQuestion<T> : Question<T> {

    /**
     * Parent question.
     */
    val previous: Question<*>

}