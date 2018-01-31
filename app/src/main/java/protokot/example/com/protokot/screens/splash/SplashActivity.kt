package protokot.example.com.protokot.screens.splash

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import protokot.example.com.protokot.data.SharedConstants
import protokot.example.com.protokot.screens.login.LoginActivity
import protokot.example.com.protokot.screens.main.MainActivity

/**
 * Activity handling splash screen
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val shared = baseContext.getSharedPreferences(SharedConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        if (shared.getString(SharedConstants.TOKEN_KEY, null) != null) {
            startActivity(MainActivity.newIntent(baseContext))
        } else {
            startActivity(LoginActivity.newIntent(baseContext))
        }
        finish()
    }
}