package com.wabadaba.dziennik.ui.login

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.api.*
import com.wabadaba.dziennik.ui.mainactivity.MainActivity
import com.wabadaba.dziennik.vo.Me
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject lateinit var userRepository: UserRepository

    @Inject lateinit var loginClient: LoginClient

    @Inject lateinit var httpClient: RxHttpClient

    @Inject lateinit var apiClientFactory: APIClientFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApplication.mainComponent.inject(this)
        setContentView(R.layout.activity_login)

        logInButton.setOnClickListener {
            usernameInputLayout.error = null
            passwordInputLayout.error = null

            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isEmpty()) {
                usernameInputLayout.error = getString(R.string.enter_username)
            }
            if (password.isEmpty()) {
                passwordInputLayout.error = getString(R.string.enter_password)
            }
            if (username.isNotEmpty() && password.isNotEmpty()) {

                logInButton.isClickable = false
                logInButton.isEnabled = false
                activity_login_progress.visibility = View.VISIBLE

                performLogin(username, password)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            //success
                            startMainActivity()
                        }, { exception ->
                            logInButton.isEnabled = true
                            logInButton.isClickable = true
                            activity_login_progress.visibility = View.GONE
                            if (exception is HttpException.Authorization) {
                                passwordInputLayout.error = getString(R.string.incorrect_password)
                            } else if (exception is IllegalStateException && (exception.message?.contains("already logged in") == true)) {
                                usernameInputLayout.error = getString(R.string.already_logged_in_message)
                            } else if (exception is HttpException.DeviceOffline) {
                                showErrorSnackbar(getString(R.string.device_offline_message))
                            } else if (exception is HttpException.ServerOffline) {
                                showErrorSnackbar(getString(R.string.server_offline_message))
                            } else if (exception is HttpException.Maintenance) {
                                showErrorSnackbar(getString(R.string.maintenance_message))
                            } else {
                                exception.printStackTrace()
                                showErrorSnackbar(getString(R.string.unknown_error_message))
                            }
                        })
            }
        }
    }

    private fun performLogin(username: String, password: String): Completable {
        return loginClient.login(username, password)
                .flatMap { authInfo ->
                    val apiClient = apiClientFactory.create(authInfo, httpClient)
                    apiClient.fetchEntities(Me::class)
                            .singleOrError()
                            .doOnSuccess { me ->
                                userRepository.addUser(FullUser(
                                        me.account.login,
                                        me.account.firstName,
                                        me.account.lastName,
                                        me.user.firstName,
                                        me.user.lastName,
                                        me.account.groupId,
                                        authInfo))
                            }
                }.toCompletable()
    }

    private fun showErrorSnackbar(text: String) = Snackbar
            .make(coordinatorLayout, text, Snackbar.LENGTH_SHORT)
            .show()

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
