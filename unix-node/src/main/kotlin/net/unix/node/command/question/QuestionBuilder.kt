package net.unix.node.command.question

import net.unix.api.terminal.Terminal
import net.unix.command.question.Question
import net.unix.command.question.QuestionAnswer
import net.unix.command.question.QuestionArgument
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.concurrent.Callable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun <T> question(argument: QuestionArgument<T>, setup: QuestionBuilder<T>.() -> Unit): QuestionBuilder<T> {
    contract {
        callsInPlace(setup, InvocationKind.EXACTLY_ONCE)
    }

    return QuestionBuilder(argument).also(setup)
}

@Suppress("UNCHECKED_CAST")
class QuestionBuilder<T>(
    override val argument: QuestionArgument<T>
) : Question<T>, KoinComponent {

    private val terminal: Terminal by inject(named("default"))

    private lateinit var create: Callable<Unit>
    private lateinit var questionAnswer: QuestionAnswer<T>

    override var answer: T? = null
        set(value) {
            field = value

            if (value != null) {
                close()
                questionAnswer.run(value)
            }
        }

    override fun answer(answer: QuestionAnswer<T>) {
        questionAnswer = answer
    }

    override fun start() {
        terminal.clear()
        create.call()
        CloudQuestionManager.startQuestion(this as Question<Any>)
    }

    override fun create(callable: Callable<Unit>) {
        this.create = callable
    }

    override fun close() {
        CloudQuestionManager.closeQuestion()
    }

    override fun restart() {
        answer = null
        start()
    }

    @OptIn(ExperimentalContracts::class)
    inline fun <T> next(argument: QuestionArgument<T>, setup: NextQuestionBuilder<T>.() -> Unit): NextQuestionBuilder<T> {
        contract {
            callsInPlace(setup, InvocationKind.EXACTLY_ONCE)
        }

        return NextQuestionBuilder(this, argument).also(setup)
    }

}