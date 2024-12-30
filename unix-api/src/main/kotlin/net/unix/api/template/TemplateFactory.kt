package net.unix.api.template

import net.unix.api.persistence.PersistentDataContainer

/**
 * This interface represents a simple factory of [Template]'s.
 */
interface TemplateFactory {

    /**
     * Create new instance of [Template].
     *
     * @param name Name of template.
     * @param persistent Persistent container of template.
     * @param files Files of template.
     * @param backFiles Back files of template.
     *
     * @return New instance of [Template].
     */
    fun create(
        name: String,
        persistent: PersistentDataContainer,
        files: MutableList<CloudFile>,
        backFiles: MutableList<CloudFile>
    ): Template

}