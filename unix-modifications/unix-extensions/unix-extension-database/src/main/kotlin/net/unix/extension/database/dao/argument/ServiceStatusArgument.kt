package net.unix.extension.database.dao.argument

import net.unix.api.service.ServiceStatus
import org.jdbi.v3.core.argument.AbstractArgumentFactory
import org.jdbi.v3.core.argument.Argument
import org.jdbi.v3.core.config.ConfigRegistry
import org.jdbi.v3.core.statement.StatementContext
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Types
import java.util.*

class ServiceStatusArgument(
    private var status: ServiceStatus?
) : Argument {

    @Throws(SQLException::class)
    override fun apply(position: Int, statement: PreparedStatement, ctx: StatementContext?) {
        statement.setString(position, status?.name)
    }

}

class ServiceStatusArgumentFactory : AbstractArgumentFactory<ServiceStatus>(Types.VARCHAR) {

    override fun build(value: ServiceStatus?, config: ConfigRegistry?): Argument = ServiceStatusArgument(value)

}