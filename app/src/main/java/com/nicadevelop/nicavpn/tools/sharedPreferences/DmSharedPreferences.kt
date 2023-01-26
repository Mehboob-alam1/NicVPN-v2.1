package com.nicadevelop.nicavpn.tools.sharedPreferences

import android.annotation.SuppressLint
import android.content.Context
import io.michaelrocks.paranoid.Obfuscate

@Obfuscate
class DmSharedPreferences(context: Context) : DmPrefsImpl(context) {///endregion

    override val sharedPreferencesName: String?
        get() = DmPrefsKeys.SHARED_PREFERENCES_NAME

    override fun onUpgrade(oldVersion: Int, newVersion: Int) {}

    companion object {
        private const val TAG = "DmSharedPreferences."
        ///region "Singleton"
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var mDmSharedPreferences: DmSharedPreferences? = null

        fun getInstance(context: Context): DmSharedPreferences? {
            // activar log shared
            // v(LogUtil.DIAM_PROXY_TAG, TAG  // activar debug + "DmSharedPreferences.getInstance")
            if (mDmSharedPreferences == null) {
                synchronized(DmSharedPreferences::class.java) { if (mDmSharedPreferences == null) mDmSharedPreferences = DmSharedPreferences(context) }
            }
            return mDmSharedPreferences
        }
        ///endregion
    }

    init {
        if (mDmSharedPreferences != null) {
            // activar log shared
            // v(LogUtil.DIAM_PROXY_TAG, TAG  // activar debug + "DmSharedPreferences.Constructor Someone has used this method by reflection")
            throw RuntimeException("For access to this class, use the method getInstance")
        }
    }
}