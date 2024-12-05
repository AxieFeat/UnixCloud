package net.unix.node.command.aether

import com.mojang.brigadier.Command
import net.unix.command.CommandLiteralBuilder
import net.unix.command.sender.CommandSender

@Suppress("NAME_SHADOWING")
class AetherLiteralBuilder(
    name: String
) : CommandLiteralBuilder<CommandSender>(name) {

    companion object {
        /**
         * Create instance of [AetherLiteralBuilder]
         *
         * @param literal Literal name
         */
        fun literal(literal: String): AetherLiteralBuilder {
            return AetherLiteralBuilder(literal)
        }
    }

    override fun execute(command: Command<CommandSender>): AetherLiteralBuilder {

        val command = Command { context ->
            command.run(context)
            1
        }

        return super.executes(command) as AetherLiteralBuilder
    }

}