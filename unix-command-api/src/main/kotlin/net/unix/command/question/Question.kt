package net.unix.command.question

import java.util.concurrent.Callable

/**
 * This interface represents question-based command.
 */
interface Question<T>{

    /**
     * Type of answer for this question.
     */
    val argument: QuestionArgument<T>

    /**
     * Last answer to this question. If null - question is not answered.
     */
    var answer: T?

    /**
     * This function will be executed on question answered.
     */
    fun answer(answer: QuestionAnswer<T>)

    /**
     * Start question.
     */
    fun start()

    /**
     * This function will be executed on question creation.
     */
    fun create(callable: Callable<Unit>)

    /**
     * Close the question.
     */
    fun close()

    /**
     * Restart the question.
     */
    fun restart()
}