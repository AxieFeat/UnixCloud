package net.unix.extension.database.dao.mysql

import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.CloudServiceStatus
import net.unix.extension.database.dao.ServiceManagerDao
import net.unix.extension.database.dao.argument.PersistentDataContainerArgumentFactory
import net.unix.extension.database.dao.argument.ServiceStatusArgumentFactory
import net.unix.extension.database.dao.argument.UUIDArgumentFactory
import net.unix.extension.database.dao.entity.JVMServiceEntry
import org.jdbi.v3.sqlobject.config.RegisterArgumentFactory
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.util.*

@RegisterArgumentFactory(value = UUIDArgumentFactory::class)
@RegisterArgumentFactory(value = ServiceStatusArgumentFactory::class)
@RegisterArgumentFactory(value = PersistentDataContainerArgumentFactory::class)
interface ServiceManagerMySQL : ServiceManagerDao {

    @get:SqlQuery("SELECT * FROM `services`")
    @get:RegisterConstructorMapper(value = JVMServiceEntry::class)
    override val services: Set<JVMServiceEntry>

    @SqlQuery("SELECT * FROM `services` WHERE `node` = :NODE")
    @RegisterConstructorMapper(value = JVMServiceEntry::class)
    override fun servicesOfNode(@Bind("NODE") node: String): Set<JVMServiceEntry>

    @SqlUpdate("INSERT INTO `services` (" +
                    "`node`, " +
                    "`uuid`, " +
                    "`name`, " +
                    "`group`, " +
                    "`static`, " +
                    "`persistent`, " +
                    "`created`, " +
                    "`status`" +
            ") VALUES (" +
                    ":NODE, " +
                    ":UUID, " +
                    ":NAME, " +
                    ":GROUP, " +
                    ":STATIC, " +
                    ":PERSISTENT, " +
                    ":STATUS" +
            ")")
    override fun create(
        @Bind("NODE") node: String,
        @Bind("UUID") uuid: UUID,
        @Bind("NAME") name: String,
        @Bind("GROUP") group: String,
        @Bind("STATIC") static: Boolean,
        @Bind("PERSISTENT") persistent: PersistentDataContainer,
        @Bind("CREATED") created: Long,
        @Bind("STATUS") status: CloudServiceStatus?
    )

    @SqlUpdate("DELETE FROM `services` WHERE `node` = :NODE AND `uuid` = :UUID")
    override fun delete(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID)

    @SqlQuery("SELECT EXISTS (SELECT 1 FROM `services` WHERE `node` = :NODE AND `name` = :NAME GROUP BY `name` HAVING COUNT(*) > 1 ) AS has_duplicate_name")
    override fun duplicates(@Bind("NODE") node: String, @Bind("NAME") name: String): Boolean

    @SqlQuery("SELECT * FROM `services` WHERE `node` = :NODE AND `name` = :NAME")
    @RegisterConstructorMapper(value = JVMServiceEntry::class)
    override fun get(@Bind("NODE") node: String, @Bind("NAME") name: String): List<JVMServiceEntry>

    @SqlQuery("SELECT * FROM `services` WHERE `node` = :NODE AND `uuid` = :UUID")
    @RegisterConstructorMapper(value = JVMServiceEntry::class)
    override fun get(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID): JVMServiceEntry?

    @SqlUpdate("UPDATE `services` SET `name` = :NAME WHERE `node` = :NODE and `uuid` = :UUID")
    override fun setServiceName(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID, @Bind("NAME") name: String)

    @SqlUpdate("UPDATE `services` SET `group` = :GROUP WHERE `node` = :NODE and `uuid` = :UUID")
    override fun setServiceGroup(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID, @Bind("GROUP") group: String)

    @SqlUpdate("UPDATE `services` SET `static` = :STATIC WHERE `node` = :NODE and `uuid` = :UUID")
    override fun setServiceStatic(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID, @Bind("STATIC") static: Boolean)

    @SqlUpdate("UPDATE `services` SET `persistent` = :PERSISTENT WHERE `node` = :NODE and `uuid` = :UUID")
    override fun setServicePersistentContainer(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID, @Bind("PERSISTENT") persistent: PersistentDataContainer)

    @SqlUpdate("UPDATE `services` SET `create` = :CREATE WHERE `node` = :NODE and `uuid` = :UUID")
    override fun setServiceCreateDate(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID, @Bind("CREATE") create: Long)

    @SqlUpdate("UPDATE `services` SET `status` = :STATUS WHERE `node` = :NODE and `uuid` = :UUID")
    override fun setServiceStatus(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID, @Bind("STATUS") status: CloudServiceStatus)
}