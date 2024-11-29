package net.unix.api.i18n

import net.unix.api.pattern.Saveable
import net.unix.api.pattern.Serializable
import java.io.File

/**
 * This interface represents saveable [Locale].
 */
interface SaveableLocale : Locale, Saveable, Serializable {

    /**
     * Save locale to file.
     *
     * @param file Where are safe.
     */
    override fun save(file: File)

}