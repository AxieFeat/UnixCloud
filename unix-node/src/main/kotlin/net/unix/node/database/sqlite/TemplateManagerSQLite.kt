package net.unix.node.database.sqlite

import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.template.CloudFile
import net.unix.node.database.TemplateManagerDao
import net.unix.node.database.argument.CloudFileArrayArgumentFactory
import net.unix.node.database.argument.PersistentDataContainerArgumentFactory
import net.unix.node.database.entity.BasicTemplateEntry
import org.jdbi.v3.sqlobject.config.RegisterArgumentFactory
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

@RegisterArgumentFactory(value = PersistentDataContainerArgumentFactory::class)
@RegisterArgumentFactory(value = CloudFileArrayArgumentFactory::class)
interface TemplateManagerSQLite : TemplateManagerDao {

    @get:SqlQuery("SELECT * FROM `templates`")
    @get:RegisterConstructorMapper(value = BasicTemplateEntry::class)
    override val templates: Set<BasicTemplateEntry>

    @SqlQuery("SELECT * FROM `templates` WHERE `node` = :NODE")
    @RegisterConstructorMapper(value = BasicTemplateEntry::class)
    override fun templatesOfNode(@Bind("NODE") node: String): Set<BasicTemplateEntry>

    @SqlUpdate("INSERT INTO `templates` (" +
                    "`node`, " +
                    "`name`, " +
                    "`persistent`, " +
                    "`files`, " +
                    "`back_files`" +
            ") VALUES (" +
                    ":NODE, " +
                    ":NAME, " +
                    ":PERSISTENT, " +
                    ":FILES, " +
                    ":BACK_FILES" +
            ") " +
            "ON DUPLICATE KEY UPDATE" +
                    "`persistent` = :PERSISTENT, " +
                    "`files` = :FILES, " +
                    "`back_files` = :BACK_FILES"
    )
    override fun create(
        @Bind("NODE") node: String,
        @Bind("NAME") name: String,
        @Bind("PERSISTENT") persistent: PersistentDataContainer,
        @Bind("FILES") files: List<CloudFile>,
        @Bind("BACK_FILES") backFiles: List<CloudFile>
    )

    @SqlUpdate("DELETE FROM `templates` WHERE `node` = :NODE AND `name` = :NAME")
    override fun delete(@Bind("NODE") node: String, @Bind("NAME") name: String)

    @SqlQuery("SELECT * FROM `templates` WHERE `node` = :NODE AND `name` = :NAME")
    @RegisterConstructorMapper(value = BasicTemplateEntry::class)
    override fun get(@Bind("NODE") node: String, @Bind("NAME") name: String): BasicTemplateEntry?

    @SqlUpdate("UPDATE `templates` SET `name` = :NEW WHERE `node` = :NODE and `name` = :NAME")
    override fun setTemplateName(@Bind("NODE") node: String, @Bind("NAME") name: String, @Bind("NEW") new: String)

    @SqlUpdate("UPDATE `templates` SET `persistent` = :PERSISTENT WHERE `node` = :NODE and `name` = :NAME")
    override fun setTemplatePersistentContainer(@Bind("NODE") node: String, @Bind("NAME") name: String, @Bind("PERSISTENT") persistent: PersistentDataContainer)

    @SqlUpdate("UPDATE `templates` SET `files` = :FILES WHERE `node` = :NODE and `name` = :NAME")
    override fun setTemplateFiles(@Bind("NODE") node: String, @Bind("NAME") name: String, @Bind("FILES") files: List<CloudFile>)

    @SqlUpdate("UPDATE `templates` SET `back_files` = :BACK_FILES WHERE `node` = :NODE and `name` = :NAME")
    override fun setTemplateBackFiles(@Bind("NODE") node: String, @Bind("NAME") name: String, @Bind("BACK_FILES") backFiles: List<CloudFile>)
}