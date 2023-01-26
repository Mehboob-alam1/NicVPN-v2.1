package com.nicadevelop.nicavpn.tools.sharedPreferences

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.util.Base64
import com.nicadevelop.nicavpn.tools.Utils
import io.michaelrocks.paranoid.Obfuscate

@Obfuscate
abstract class DmPrefsImpl(private val mContext: Context) : DmPrefs() {
    override fun getInt(key: String?, defaultValue: Int): Int {
        return Integer.parseInt(getObject(key, defaultValue.toString())!!)
    }

    override fun getLong(key: String?, defaultValue: Long): Long {
        return getObject(key, defaultValue.toString())!!.toLong()
    }

    override fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return getObject(key, defaultValue.toString())!!.toBoolean()
    }

    override fun getString(key: String?, defaultValue: String?): String? {
        return getObject(key, defaultValue)
    }

    override fun getBytes(key: String?): ByteArray? {
        val byteValue = getString(key, null)
        return if (byteValue == null) null else Base64.decode(byteValue, Base64.DEFAULT)
    }

    override fun putInt(key: String?, value: Int) {
        val prefs: SharedPreferences = mContext.getSharedPreferences(
                sharedPreferencesName, Context.MODE_PRIVATE)
        val editor: Editor = prefs.edit()
        if (!putObject(key, value)) {
            editor.putInt(key, value)
            editor.apply()
        }
    }

    override fun putObject(key: String?, value: Any?): Boolean {
        /*
            Metodo para almacenar un objeto en formato exadecimal
            Se combierte primero el objeto value a un array de bites,
            luego se genera un texto entre letras y numero con una longitud
            de 512 caracteres(Este valor puede ser modificado pues se para por
            parametro al metodo getSaltString), se utiliza un separador para delimitar
            el texto generado(Este valor puede ser cambiado por ahora se utilizo el
            signo igual), una vez generado este texto se le concatena al objeto original
            ya convertido en hexadecimal, se procede a convertirlo en bites y luego a
            hexadecimal, el resultado es el que se almacena en los shared preferences
            haciendo dificil la lectura desde apps de terceros.
         */
        val prefs: SharedPreferences = mContext.getSharedPreferences(
                sharedPreferencesName, Context.MODE_PRIVATE)
        val editor: Editor = prefs.edit()
        editor.putString(key, Utils.encryptValue(value))
        editor.apply()
        return true
    }

    override fun getObject(key: String?, defaultValue: String?): String? {
        val prefs: SharedPreferences = mContext.getSharedPreferences(
                sharedPreferencesName, Context.MODE_PRIVATE)
        val hexString: String? = prefs.getString(key, defaultValue)
        if (hexString != defaultValue) {
            return Utils.decryptValue(hexString!!)
        }
        return defaultValue
    }

    override fun putLong(key: String?, value: Long) {
        putObject(key, value)
    }

    override fun putBoolean(key: String?, value: Boolean) {
        putObject(key, value)
    }

    override fun putString(key: String?, value: String?) {
        putObject(key, value)
    }

    override fun putBytes(key: String?, value: ByteArray?) {
        val encodedBytes: String? = Base64.encodeToString(value, Base64.DEFAULT)
        putString(key, encodedBytes)
    }

    override fun remove(key: String?) {
        val prefs: SharedPreferences = mContext.getSharedPreferences(
                sharedPreferencesName, Context.MODE_PRIVATE)
        val editor: Editor = prefs.edit()
        editor.remove(key)
        editor.apply()
    }

    override fun clear() {
        val prefs: SharedPreferences = mContext.getSharedPreferences(
            sharedPreferencesName, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

    }

    companion object {
        @Suppress("unused")
        private const val TAG = "DmPrefsImpl."
    }

}