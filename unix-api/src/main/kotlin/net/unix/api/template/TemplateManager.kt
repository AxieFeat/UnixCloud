package net.unix.api.template

import net.unix.api.pattern.manager.Manager
import net.unix.api.remote.RemoteAccessible
import net.unix.api.service.Service
import java.rmi.RemoteException

/**
 * Manager for [Template]'s.
 *
 * With this, you can control the [Template]'s for [Service]'s.
 */
interface TemplateManager : Manager<Template>, RemoteAccessible {

    /**
     * All [Template]'s.
     */
    @get:Throws(RemoteException::class)
    val templates: Set<Template>

    /**
     * Factory for creation new [Template]'s.
     */
    @get:Throws(RemoteException::class)
    @set:Throws(RemoteException::class)
    var factory: TemplateFactory

    /**
     * Register template in [templates].
     *
     * @param value Template to register.
     */
    @Throws(RemoteException::class)
    override fun register(value: Template)

    /**
     * Unregister template from [templates].
     *
     * @param value Template to unregister.
     */
    @Throws(RemoteException::class)
    override fun unregister(value: Template)

    /**
     * Delete template. It will be unregistered via [unregister].
     *
     * @param template Template to delete.
     */
    @Throws(RemoteException::class)
    fun delete(template: Template)

    /**
     * Get [Template] by [name].
     *
     * @param name Template name.
     *
     * @return [Template] instance or null, if not founded.
     */
    @Throws(RemoteException::class)
    override fun get(name: String): Template?

    companion object
}