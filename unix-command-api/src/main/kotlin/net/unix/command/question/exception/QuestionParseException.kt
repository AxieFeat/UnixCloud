package net.unix.command.question.exception

/**
 * Throw, if question has parse exception.
 */
class QuestionParseException(
    message: String? = null,
    throwable: Throwable? = null
) : RuntimeException(message, throwable)