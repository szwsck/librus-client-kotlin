package com.wabadaba.dziennik.api

import android.content.Context
import android.preference.PreferenceManager

class AuthInfoRepository(val login: String, context: Context) {

    val prefs = PreferenceManager.getDefaultSharedPreferences(context)!!

    private val prefKey = "${login}_auth_info"

    fun saveAuthInfo(authInfo: AuthInfo) {
        val stringValue = Parser.mapper.writeValueAsString(authInfo)
        prefs.edit()
                .putString(prefKey, stringValue)
                .apply()
    }

    fun getAuthInfo(): AuthInfo {
        val stringValue = prefs.getString(prefKey, null) ?: throw IllegalStateException("Authorization info for user $login not found")
        return Parser.parse(stringValue)
    }

    fun deleteAuthInfo() {
        prefs.edit()
                .remove(prefKey)
                .apply()
    }
}