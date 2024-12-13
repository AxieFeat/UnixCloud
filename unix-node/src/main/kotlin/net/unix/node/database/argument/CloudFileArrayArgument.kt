package net.unix.node.database.argument

import net.unix.api.template.CloudFile
import net.unix.node.CloudExtension.toJson
import org.jdbi.v3.core.argument.AbstractArgumentFactory
import org.jdbi.v3.core.argument.Argument
import org.jdbi.v3.core.config.ConfigRegistry
import org.jdbi.v3.core.statement.StatementContext
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Types
import java.util.*

class CloudFileArrayArgument(
    private var array: List<CloudFile>?
) : Argument {

    @Throws(SQLException::class)
    override fun apply(position: Int, statement: PreparedStatement, ctx: StatementContext?) {
        statement.setString(position, array?.map { it.serialize() }?.toJson())
    }

}

class CloudFileArrayArgumentFactory : AbstractArgumentFactory<List<CloudFile>>(Types.VARCHAR) {

    override fun build(value: List<CloudFile>?, config: ConfigRegistry?): Argument = CloudFileArrayArgument(value)

}