package net.unix.command.question

/**
 * Manager for questions.
 */
interface QuestionManager {

    /**
     * Current active question.
     */
    var activeQuestion: Question<Any>?

    /**
     * Start a question.
     *
     * @param question Question to start.
     */
    fun startQuestion(question: Question<Any>)

    /**
     * Close current active question.
     */
    fun closeQuestion()
}