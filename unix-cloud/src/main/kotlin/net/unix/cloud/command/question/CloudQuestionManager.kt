package net.unix.cloud.command.question

import net.unix.command.question.Question
import net.unix.command.question.QuestionManager

object CloudQuestionManager : QuestionManager {
    override var activeQuestion: Question<Any>? = null

    override fun startQuestion(question: Question<Any>) {
        activeQuestion = question
    }

    override fun closeQuestion() {
        activeQuestion = null
    }
}