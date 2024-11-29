package net.unix.api.i18n

import net.unix.api.LocationSpace
import java.io.File

/**
 * This interface represents manager for [SaveableLocale]'s
 */
interface SaveableLocaleManager {

    /**
     * Load all locales from [LocationSpace.language] directory.
     */
    fun loadlAll()

    /**
     * Load [SaveableLocale] from file.
     *
     * @param file From file.
     *
     * @return Instance of [SaveableLocale].
     */
    fun load(file: File): SaveableLocale

}