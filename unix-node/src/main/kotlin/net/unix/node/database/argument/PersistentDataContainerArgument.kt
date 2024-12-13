package net.unix.node.database.argument

import net.unix.api.persistence.PersistentDataContainer
import net.unix.node.CloudExtension.toJson
import org.jdbi.v3.core.argument.AbstractArgumentFactory
import org.jdbi.v3.core.argument.Argument
import org.jdbi.v3.core.config.ConfigRegistry
import org.jdbi.v3.core.statement.StatementContext
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Types
import java.util.*

class PersistentDataContainerArgument(
    private var container: PersistentDataContainer?
) : Argument {

    @Throws(SQLException::class)
    override fun apply(position: Int, statement: PreparedStatement, ctx: StatementContext?) {
        statement.setString(position, container?.serialize()?.toJson())
    }

}

class PersistentDataContainerArgumentFactory : AbstractArgumentFactory<PersistentDataContainer>(Types.VARCHAR) {

    override fun build(value: PersistentDataContainer?, config: ConfigRegistry?): Argument = PersistentDataContainerArgument(value)

}