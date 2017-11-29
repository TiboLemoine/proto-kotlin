package protokot.example.com.protokot.screens.splash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import protokot.example.com.protokot.screens.login.LoginActivity

/**
 * Activity handling splash screen
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(LoginActivity.newIntent(baseContext))
    }
}