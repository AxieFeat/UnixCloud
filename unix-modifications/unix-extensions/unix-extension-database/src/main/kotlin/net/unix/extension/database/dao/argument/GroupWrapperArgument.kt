package net.unix.extension.database.dao.argument

import net.unix.api.group.GroupWrapper
import org.jdbi.v3.core.argument.AbstractArgumentFactory
import org.jdbi.v3.core.argument.Argument
import org.jdbi.v3.core.config.ConfigRegistry
import org.jdbi.v3.core.statement.StatementContext
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Types
import java.util.*

class GroupWrapperArgument(
    private var wrapper: GroupWrapper?
) : Argument {

    @Throws(SQLException::class)
    override fun apply(position: Int, statement: PreparedStatement, ctx: StatementContext?) {
        statement.setString(position, wrapper?.name)
    }

}

class GroupWrapperArgumentFactory : AbstractArgumentFactory<GroupWrapper>(Types.VARCHAR) {

    override fun build(value: GroupWrapper?, config: ConfigRegistry?): Argument = GroupWrapperArgument(value)

}