package net.unix.node.group.wrapper

import net.unix.api.group.wrapper.GroupWrapper
import net.unix.api.group.wrapper.GroupWrapperFactory
import net.unix.command.question.NextQuestion
import net.unix.command.question.Question
import net.unix.node.command.question.NextQuestionBuilder
import net.unix.node.command.question.argument.primitive.QuestionNumberArgument
import net.unix.node.command.question.argument.primitive.QuestionStringArgument
import net.unix.node.logging.CloudLogger
import java.util.concurrent.CompletableFuture

@Suppress("UNCHECKED_CAST", "LABEL_NAME_CLASH")
object GroupJVMWrapperFactory : GroupWrapperFactory {

    private fun readResolve(): Any = GroupJVMWrapperFactory

    override val name: String = "JVM"

    override fun createBySerialized(serialized: Map<String, Any>): GroupJVMWrapper {
        return GroupJVMWrapper.deserialize(serialized)
    }

    override fun questionBuilder(context: NextQuestion<GroupWrapperFactory>): CompletableFuture<GroupWrapper> {
        val completable = CompletableFuture<GroupWrapper>()

        context.run {
            (this as NextQuestionBuilder).next(QuestionNumberArgument.int(1, 4)) {
                val wrapperFactory = (this.previous as Question<GroupWrapperFactory>).answer
                var executableFile: String? = null
                var startedLine: String? = null
                var stopCommand: String? = null

                create {
                    CloudLogger.info("You in group create mode.")
                    CloudLogger.info("Configuring wrapper of group.")
                    CloudLogger.info("#################################")
                    CloudLogger.info("	Current wrapper settings:")
                    CloudLogger.info("	 [*] Wrapper name: <green>${wrapperFactory?.name ?: "<red>-</red>"}")
                    CloudLogger.info("	 [1] Executable file: <green>${executableFile ?: "<red>-</red>"}")
                    CloudLogger.info("	 [2] Started line: <green>${startedLine ?: "<red>-</red>"}")
                    CloudLogger.info("	 [3] Stop command: <green>${stopCommand ?: "<red>-</red>"}")
                    CloudLogger.info("	 [4] Save and return")
                    CloudLogger.info("#################################")
                }

                answer {
                    when(answer) {
                        1 -> {
                            next(QuestionStringArgument()) {
                                create {
                                    CloudLogger.info("You in group create mode.")
                                    CloudLogger.info("Configuring wrapper of group.")
                                    CloudLogger.info("Set executable file path")
                                }

                                answer { name ->
                                    executableFile = name
                                    close()
                                    previous.start()
                                }
                            }.start()
                        }
                        2 -> {
                            next(QuestionStringArgument()) {
                                create {
                                    CloudLogger.info("You in group create mode.")
                                    CloudLogger.info("Configuring wrapper of group.")
                                    CloudLogger.info("Set started line of service")
                                }

                                answer { line ->
                                    startedLine = line
                                    close()
                                    previous.start()
                                }
                            }.start()
                        }
                        3 -> {
                            next(QuestionStringArgument()) {
                                create {
                                    CloudLogger.info("You in group create mode.")
                                    CloudLogger.info("Configuring wrapper of group.")
                                    CloudLogger.info("Set stop command")
                                }

                                answer { command ->
                                    stopCommand = command
                                    close()
                                    previous.start()
                                }
                            }.start()
                        }

                        4 -> {
                            next(QuestionNumberArgument.int(1, 2)) {
                                create {
                                    CloudLogger.info("You in group create mode.")
                                    CloudLogger.info("Configuring wrapper of group.")
                                    CloudLogger.info("Do you want exit from configuring wrapper of group?")
                                    CloudLogger.info("  [1] - Yes")
                                    CloudLogger.info("  [2] - No")
                                    if (executableFile == null) {
                                        CloudLogger.warning("<yellow>Attention! You no set one of this required params:")
                                        CloudLogger.warning("<yellow> - Executable file")
                                        CloudLogger.warning("<yellow>If you exit - settings will not be applied!")
                                    }
                                }

                                answer { result ->
                                    when(result) {
                                        1 -> {
                                            if(executableFile == null || wrapperFactory == null) {
                                                close()
                                                context.previous.start()

                                                return@answer
                                            }
                                            completable.complete(
                                                wrapperFactory.createBySerialized(
                                                    mapOf(
                                                        "name" to wrapperFactory.name,
                                                        "executable-file" to executableFile!!,
                                                        "stop-command" to (stopCommand ?: "stop"),
                                                        "started-line" to (startedLine ?: "Started in")
                                                    )
                                                )
                                            )
                                            close()
                                            context.previous.start()
                                        }
                                        2 -> {
                                            close()
                                            previous.start()
                                        }
                                    }
                                }
                            }.start()
                        }
                    }
                }
            }.start()
        }

        return completable
    }

}