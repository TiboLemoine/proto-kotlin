package protokot.example.com.protokot.screens.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_create_account.*
import protokot.example.com.protokot.R
import protokot.example.com.protokot.data.CreateUserRequest
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
import java.util.regex.Pattern

/**
 * Activity handling account creation screen
 */
class CreateAccountActivity : AppCompatActivity() {

    /**
     * Account creation subscription
     */
    private var subscription: Subscription? = null

    companion object {
        const val digitPattern: String = "^(?=.*[0-9])$"
        const val letterPattern: String = "^(?=.*[a-zA-Z])$"

        fun newIntent(context: Context): Intent = Intent(context, CreateAccountActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        validateButton.setOnClickListener { v -> validateForm(firstnameField.text.toString(), lastnameField.text.toString(), loginField.text.toString(), passwordField.text.toString(), confirmPasswordField.text.toString()) }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.let { it.unsubscribe() }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createUserAccount(firstname: String, lastname: String, email: String, password: String) {
        showLoader()
        validateButton.isEnabled = false

        val service = LibraryService("http://10.0.2.2:3000/", ILibraryRetrofit::class.java)
        subscription = service.serviceInstance.createAccount(CreateUserRequest(firstname, lastname, email, password))
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
                        Observable.error<Exception>(Exception("Error occured while creating user account"))
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate { hideLoader() }
                .doOnError { _ ->
                    validateButton.isEnabled = true
                    showError()
                }
                .subscribe({ _ ->
                    startActivity(MainActivity.newIntent(baseContext))
                    finish()
                }, { e -> e.printStackTrace() })
    }

    private fun validateForm(firstname: String, lastname: String, login: String, password: String, confirmPassword: String) {
        firstnameLayout.isErrorEnabled = false
        lastnameLayout.isErrorEnabled = false
        loginLayout.isErrorEnabled = false
        passwordLayout.isErrorEnabled = false
        confirmPasswordLayout.isErrorEnabled = false

        if (firstname.isBlank() || firstname.isEmpty()) {
            firstnameLayout.isErrorEnabled = true
            firstnameLayout.error = getString(R.string.error_firstname_empty)
        } else if (lastname.isBlank() || lastname.isEmpty()) {
            lastnameLayout.isErrorEnabled = true
            lastnameLayout.error = getString(R.string.error_lastname_empty)
        } else if (login.isBlank() || login.isEmpty()) {
            loginLayout.isErrorEnabled = true
            loginLayout.error = getString(R.string.error_login_empty)
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(login).matches()) {
            loginLayout.isErrorEnabled = true
            loginLayout.error = getString(R.string.error_login_format)
        } else if (password.isBlank() || password.isEmpty()) {
            passwordLayout.isErrorEnabled = true
            passwordLayout.error = getString(R.string.error_password_empty)
        } else if (password.trim().length < 8) {
            passwordLayout.isErrorEnabled = true
            passwordLayout.error = getString(R.string.error_password_short)
        } else if (Pattern.compile(digitPattern).matcher(password).matches()) {
            passwordLayout.isErrorEnabled = true
            passwordLayout.error = getString(R.string.error_password_digit)
        } else if (Pattern.compile(letterPattern).matcher(password).matches()) {
            passwordLayout.isErrorEnabled = true
            passwordLayout.error = getString(R.string.error_password_letter)
        } else if (password != confirmPassword) {
            confirmPasswordLayout.isErrorEnabled = true
            confirmPasswordLayout.error = getString(R.string.error_password_letter)
        } else {
            createUserAccount(firstname, lastname, login, password)
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
        dialog.setMessage(getString(R.string.error_creation))
        dialog.setPositiveButton(getString(R.string.button_ok), null)
        dialog.show()
    }
}