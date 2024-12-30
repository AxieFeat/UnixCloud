package net.unix.extension.database.dao.argument

import org.jdbi.v3.core.argument.AbstractArgumentFactory
import org.jdbi.v3.core.argument.Argument
import org.jdbi.v3.core.config.ConfigRegistry
import org.jdbi.v3.core.statement.StatementContext
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Types
import java.util.*

class UUIDArgument(
    private var uuid: UUID?
) : Argument {

    @Throws(SQLException::class)
    override fun apply(position: Int, statement: PreparedStatement, ctx: StatementContext?) {
        statement.setString(position, uuid.toString())
    }

}

class UUIDArgumentFactory : AbstractArgumentFactory<UUID>(Types.VARCHAR) {

    override fun build(value: UUID?, config: ConfigRegistry?): Argument = UUIDArgument(value)

}