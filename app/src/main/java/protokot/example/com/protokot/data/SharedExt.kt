package protokot.example.com.protokot.data

import android.content.SharedPreferences

/**
 * Extensions for Shared Preferences
 */
fun SharedPreferences.putString(key : String, value : String) {
    this.edit().putString(key, value).apply()
}