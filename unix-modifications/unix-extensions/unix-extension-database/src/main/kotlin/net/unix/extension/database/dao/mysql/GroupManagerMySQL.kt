package net.unix.extension.database.dao.mysql

import net.unix.api.group.wrapper.GroupWrapper
import net.unix.api.persistence.PersistentDataContainer
import net.unix.extension.database.dao.GroupManagerDao
import net.unix.extension.database.dao.argument.GroupWrapperArgumentFactory
import net.unix.extension.database.dao.argument.ListStringArgumentFactory
import net.unix.extension.database.dao.argument.PersistentDataContainerArgumentFactory
import net.unix.extension.database.dao.argument.UUIDArgumentFactory
import net.unix.extension.database.dao.entity.JVMGroupEntry
import org.jdbi.v3.sqlobject.config.RegisterArgumentFactory
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.util.*

@RegisterArgumentFactory(value = UUIDArgumentFactory::class)
@RegisterArgumentFactory(value = ListStringArgumentFactory::class)
@RegisterArgumentFactory(value = GroupWrapperArgumentFactory::class)
@RegisterArgumentFactory(value = PersistentDataContainerArgumentFactory::class)
interface GroupManagerMySQL : GroupManagerDao {

    @get:SqlQuery("SELECT * FROM `groups`")
    @get:RegisterConstructorMapper(value = JVMGroupEntry::class)
    override val groups: Set<JVMGroupEntry>

    @SqlQuery("SELECT * FROM `groups` WHERE `node` = :NODE")
    @RegisterConstructorMapper(value = JVMGroupEntry::class)
    override fun groupsOfNode(@Bind("NODE") node: String): Set<JVMGroupEntry>

    @SqlUpdate("INSERT INTO `groups` (" +
                    "`node`, " +
                    "`uuid`, " +
                    "`name`, " +
                    "`service_limit`, " +
                    "`persistent`, " +
                    "`properties`, " +
                    "`group_wrapper`, " +
                    "`templates`" +
            ") VALUES (" +
                    ":NODE, " +
                    ":UUID, " +
                    ":NAME, " +
                    ":LIMIT, " +
                    ":EXECUTABLE_FILE, " +
                    ":PERSISTENT, " +
                    ":PROPERTIES, " +
                    ":WRAPPER, " +
                    ":TEMPLATES" +
            ")")
    override fun create(
        @Bind("NODE") node: String,
        @Bind("UUID") uuid: UUID,
        @Bind("NAME") name: String,
        @Bind("LIMIT") serviceLimit: Int,
        @Bind("PERSISTENT") persistent: PersistentDataContainer,
        @Bind("PROPERTIES") properties: List<String>,
        @Bind("WRAPPER") wrapper: GroupWrapper?,
        @Bind("TEMPLATES") templates: List<String>,
    )

    @SqlUpdate("DELETE FROM `groups` WHERE `node` = :NODE AND `uuid` = :UUID")
    override fun delete(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID)

    @SqlQuery("SELECT EXISTS (SELECT 1 FROM `groups` WHERE `node` = :NODE AND `name` = :NAME GROUP BY `name` HAVING COUNT(*) > 1 ) AS has_duplicate_name")
    override fun duplicates(@Bind("NODE") node: String, @Bind("NAME") name: String): Boolean

    @SqlQuery("SELECT * FROM `groups` WHERE `node` = :NODE AND `name` = :NAME")
    @RegisterConstructorMapper(value = JVMGroupEntry::class)
    override fun get(@Bind("NODE") node: String, @Bind("NAME") name: String): List<JVMGroupEntry>

    @SqlQuery("SELECT * FROM `groups` WHERE `node` = :NODE AND `uuid` = :UUID")
    @RegisterConstructorMapper(value = JVMGroupEntry::class)
    override fun get(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID): JVMGroupEntry?

    @SqlUpdate("UPDATE `groups` SET `name` = :NAME WHERE `node` = :NODE and `uuid` = :UUID")
    override fun setGroupName(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID, @Bind("NAME") name: String)

    @SqlUpdate("UPDATE `groups` SET `service_limit` = :SERVICE_LIMIT WHERE `node` = :NODE and `uuid` = :UUID")
    override fun setGroupServiceLimit(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID, @Bind("SERVICE_LIMIT") limit: Int)

    @SqlUpdate("UPDATE `groups` SET `executable_file` = :EXECUTABLE_FILE WHERE `node` = :NODE and `uuid` = :UUID")
    override fun setGroupExecutableFile(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID, @Bind("EXECUTABLE_FILE") executable: String)

    @SqlUpdate("UPDATE `groups` SET `persistent` = :PERSISTENT WHERE `node` = :NODE and `uuid` = :UUID")
    override fun setGroupPersistentContainer(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID, @Bind("PERSISTENT") persistent: PersistentDataContainer)

    @SqlUpdate("UPDATE `groups` SET `properties` = :PROPERTIES WHERE `node` = :NODE and `uuid` = :UUID")
    override fun setGroupProperties(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID, @Bind("PROPERTIES") properties: List<String>)

    @SqlUpdate("UPDATE `groups` SET `group_wrapper` = :GROUP_WRAPPER WHERE `node` = :NODE and `uuid` = :UUID")
    override fun setGroupWrapper(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID, @Bind("GROUP_WRAPPER") wrapper: String?)

    @SqlUpdate("UPDATE `groups` SET `templates` = :TEMPLATES WHERE `node` = :NODE and `uuid` = :UUID")
    override fun setGroupTemplates(@Bind("NODE") node: String, @Bind("UUID") uuid: UUID, @Bind("TEMPLATES") templates: List<String>)

}