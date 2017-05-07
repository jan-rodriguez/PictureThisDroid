package com.jankrodriguez.picturethis.sharedprefs

import android.content.Context
import android.content.SharedPreferences
import com.jankrodriguez.picturethis.model.User

private const val GOOGLE_ID_KEY = "user_google_id"
private const val USER_NAME_KEY = "user_name"
private const val USER_SCORE_KEY = "user_score"

fun SharedPreferences.saveUser(user: User) {
  val editor = edit()
  editor.putString(GOOGLE_ID_KEY, user.google_id)
  editor.putString(USER_NAME_KEY, user.name)
  editor.putInt(USER_SCORE_KEY, user.score)
  editor.apply()
}

fun SharedPreferences.getUser(): User? {
  val requiredKeys = listOf(GOOGLE_ID_KEY, USER_NAME_KEY)
  var googleId: String? = null
  var name: String? = null
  var score: Int = 0

  requiredKeys.forEach { key ->
    if (!contains(key)) {
      return null
    } else {
      when (key) {
        GOOGLE_ID_KEY -> googleId = getString(key, "")
        USER_NAME_KEY -> name = getString(key, "")
        USER_SCORE_KEY -> score = getInt(key, 0)
      }
    }
  }
  return User(name = name!!, google_id = googleId!!, score = score)
}

private const val USER_SHARED_PREFS_KEY = "user_shared_prefs"

fun Context.getUserSharedPreferences(): SharedPreferences {
  return getSharedPreferences(USER_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
}
