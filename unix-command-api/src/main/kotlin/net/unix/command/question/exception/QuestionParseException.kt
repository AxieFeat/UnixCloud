package net.unix.command.question.exception

class QuestionParseException(
    message: String? = null,
    throwable: Throwable? = null
) : RuntimeException(message, throwable)