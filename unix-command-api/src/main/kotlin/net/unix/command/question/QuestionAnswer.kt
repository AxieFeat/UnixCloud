package net.unix.command.question

fun interface QuestionAnswer<T> {
    fun run(answer: T)
}