package com.nicadevelop.nicavpn.tools.sharedPreferences

import io.michaelrocks.paranoid.Obfuscate

@Obfuscate
abstract class DmPrefs {
    /**
     * Returns the shared preferences file name to use.
     * Subclasses should override and return the shared preferences file.
     */
    abstract val sharedPreferencesName: String?

    /**
     * Handles pref version upgrade.
     */
    abstract fun onUpgrade(oldVersion: Int, newVersion: Int)

    /**
     * @param key          The key to look up in shared prefs
     * @param defaultValue The default value if value in shared prefs is null or if
     * NumberFormatException is caught.
     * @return The corresponding value, or the default value.
     */
    abstract fun getInt(key: String?, defaultValue: Int): Int

    /**
     * @param key          The key to look up in shared prefs
     * @param defaultValue The default value if value in shared prefs is null or if
     * NumberFormatException is caught.
     * @return The corresponding value, or the default value.
     */
    abstract fun getLong(key: String?, defaultValue: Long): Long

    /**
     * @param key          The key to look up in shared prefs
     * @param defaultValue The default value if value in shared prefs is null.
     * @return The corresponding value, or the default value.
     */
    abstract fun getBoolean(key: String?, defaultValue: Boolean): Boolean

    /**
     * @param key          The key to look up in shared prefs
     * @param defaultValue The default value if value in shared prefs is null.
     * @return The corresponding value, or the default value.
     */
    abstract fun getString(key: String?, defaultValue: String?): String?

    /**
     * @param key The key to look up in shared prefs
     * @return The corresponding value, or null if not found.
     */
    abstract fun getBytes(key: String?): ByteArray?

    /**
     * @param key   The key to set in shared prefs
     * @param value The value to assign to the key
     */
    abstract fun putInt(key: String?, value: Int)

    /**
     * @param key   The key to set in shared prefs
     * @param value The value to assign to the key
     */
    abstract fun putLong(key: String?, value: Long)

    /**
     * @param key   The key to set in shared prefs
     * @param value The value to assign to the key
     */
    abstract fun putBoolean(key: String?, value: Boolean)

    /**
     * @param key   The key to set in shared prefs
     * @param value The value to assign to the key
     */
    abstract fun putString(key: String?, value: String?)

    /**
     * @param key   The key to set in shared prefs
     * @param value The value to assign to the key
     */
    abstract fun putBytes(key: String?, value: ByteArray?)

    /**
     * @param key The key to remove from shared prefs
     */
    abstract fun remove(key: String?)

    /**
     * Clear all shared prefs
     */
    abstract fun clear()

    /**
     * @param key   The key to set in shared prefs
     * @param value The value to assign to the key
     * @return The corresponding value, or the default value.
     */
    abstract fun putObject(key: String?, value: Any?): Boolean

    /**
     * @param key          The key to look up in shared prefs
     * @param defaultValue The default value if value in shared prefs is null.
     * @return The corresponding value, or the default value.
     */
    abstract fun getObject(key: String?, defaultValue: String?): String?

    companion object {
        /**
         * Shared preferences name for preferences applicable to the entire app.
         */
        val SHARED_PREFERENCES_NAME: String? = DmPrefsKeys.SHARED_PREFERENCES_NAME
    }
}