package net.unix.node.command.question

import net.unix.api.terminal.Terminal
import net.unix.command.question.NextQuestion
import net.unix.command.question.Question
import net.unix.command.question.QuestionAnswer
import net.unix.command.question.QuestionArgument
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.concurrent.Callable

@Suppress("UNCHECKED_CAST")
class NextQuestionBuilder<T>(
    override val previous: Question<*>,
    override val argument: QuestionArgument<T>
) : NextQuestion<T>, KoinComponent {

    private val terminal: Terminal by inject(named("default"))

    private lateinit var create: Callable<Unit>
    private lateinit var questionAnswer: QuestionAnswer<T>

    override var answer: T? = null
        set(value) {
            field = value

            if (value != null) {
                questionAnswer.run(value)
            }
        }

    override fun answer(answer: QuestionAnswer<T>) {
        questionAnswer = answer
    }

    override fun create(callable: Callable<Unit>) {
        this.create = callable
    }

    override fun start() {
        previous.close()
        terminal.clear()
        create.call()
        CloudQuestionManager.startQuestion(this as Question<Any>)
    }

    override fun close() {
        CloudQuestionManager.closeQuestion()
    }

    override fun restart() {
        answer = null
        start()
    }

}