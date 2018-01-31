package protokot.example.com.protokot.screens.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import protokot.example.com.protokot.R
import protokot.example.com.protokot.data.LogInRequest
import protokot.example.com.protokot.data.SharedConstants
import protokot.example.com.protokot.data.putString
import protokot.example.com.protokot.network.ILibraryRetrofit
import protokot.example.com.protokot.network.LibraryService
import protokot.example.com.protokot.screens.main.MainActivity
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.lang.Exception

/**
 * Activity handling login screen
 */
class LoginActivity : AppCompatActivity() {

    /**
     * Log in subscription
     */
    private var subscription: Subscription? = null

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, LoginActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener { v -> validateForm(loginField.text.toString(), passwordField.text.toString()) }
        createAccountButton.setOnClickListener { v -> startActivity(CreateAccountActivity.newIntent(baseContext)) }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.let { it.unsubscribe() }
    }

    private fun logUserIn(email: String, password: String) {
        showLoader()
        loginButton.isEnabled = false

        val service = LibraryService("http://10.0.2.2:3000/", ILibraryRetrofit::class.java)
        subscription = service.serviceInstance.logUserIn(LogInRequest(email, password))
                .subscribeOn(Schedulers.io())
                .flatMap { response ->
                    return@flatMap if (response.token != null && response.user != null) {
                        val shared = baseContext.getSharedPreferences(SharedConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
                        shared.putString(SharedConstants.FIRSTNAME_KEY, response.user.firstName)
                        shared.putString(SharedConstants.LASTNAME_KEY, response.user.lastName)
                        shared.putString(SharedConstants.EMAIL_KEY, response.user.email)
                        shared.putString(SharedConstants.TOKEN_KEY, response.token)

                        Observable.just(true)
                    } else {
                        Observable.error<Exception>(Exception("Error occured while logging user in"))
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate { hideLoader() }
                .doOnError { _ ->
                    loginButton.isEnabled = true
                    showError()
                }
                .subscribe({ _ ->
                    startActivity(MainActivity.newIntent(baseContext))
                    finish()
                }, { e -> e.printStackTrace() })
    }

    private fun validateForm(login: String, password: String) {
        if (login.isBlank() || login.isEmpty()) {
            loginLayout.isErrorEnabled = true
            loginLayout.error = getString(R.string.error_login_empty)
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(login).matches()) {
            loginLayout.isErrorEnabled = true
            loginLayout.error = getString(R.string.error_login_format)
        } else if (password.isBlank() || password.isEmpty()) {
            loginLayout.isErrorEnabled = false
            passwordLayout.isErrorEnabled = true
            passwordLayout.error = getString(R.string.error_password_empty)
        } else {
            loginLayout.isErrorEnabled = false
            passwordLayout.isErrorEnabled = false
            logUserIn(login, password)
        }
    }

    private fun showLoader() {
        progressBar.visibility = View.VISIBLE
        progressText.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        progressBar.visibility = View.INVISIBLE
        progressText.visibility = View.INVISIBLE
    }

    private fun showError() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.warning))
        dialog.setMessage(getString(R.string.error_login))
        dialog.setPositiveButton(getString(R.string.button_ok), null)
        dialog.show()
    }
}