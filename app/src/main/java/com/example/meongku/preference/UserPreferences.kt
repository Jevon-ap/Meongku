package com.example.meongku.preference

import android.content.Context
import android.content.SharedPreferences
import com.example.meongku.api.user.User

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean("isLoggedIn", false)
        set(value) = prefs.edit().putBoolean("isLoggedIn", value).apply()

    var idToken: String?
        get() = prefs.getString("idToken", null)
        set(value) = prefs.edit().putString("idToken", value).apply()

    var uid: String?
        get() = prefs.getString("uid", null)
        set(value) = prefs.edit().putString("uid", value).apply()

    var userData: String?
        get() = prefs.getString("userData", null)
        set(value) = prefs.edit().putString("userData", value).apply()

    fun clear() {
        prefs.edit().apply {
            remove("isLoggedIn")
            remove("idToken")
            remove("uid")
            remove("userData")
        }.apply()
    }
}