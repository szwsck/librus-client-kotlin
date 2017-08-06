package com.wabadaba.dziennik.ui.login

import android.arch.lifecycle.LifecycleActivity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.api.APIClient
import com.wabadaba.dziennik.api.FullUser
import com.wabadaba.dziennik.api.HttpException
import com.wabadaba.dziennik.api.UserRepository
import com.wabadaba.dziennik.ui.MainActivity
import com.wabadaba.dziennik.vo.Me
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : LifecycleActivity() {

    @Inject lateinit var userRepository: UserRepository

    @Inject lateinit var apiclient: APIClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as MainApplication).mainComponent.inject(this)
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
                performLogin(username, password)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            startMainActivity()
                        }, {
                            if (it is HttpException.Authorization) {
                                passwordInputLayout.error = getString(R.string.incorrect_password)
                            } else if (it is IllegalStateException && (it.message?.contains("already logged in") ?: false)) {
                                usernameInputLayout.error = getString(R.string.already_logged_in_message)
                            } else if (it is HttpException.DeviceOffline) {
                                showErrorSnackbar(getString(R.string.device_offline_message))
                            } else if (it is HttpException.ServerOffline) {
                                showErrorSnackbar(getString(R.string.server_offline_message))
                            } else if (it is HttpException.Maintenance) {
                                showErrorSnackbar(getString(R.string.maintenance_message))
                            } else {
                                it.printStackTrace()
                                showErrorSnackbar(getString(R.string.unknown_error_message))
                            }
                        })
            }
        }
    }

    private fun performLogin(username: String, password: String): Completable {
        return apiclient.login(username, password)
                .flatMap { authInfo ->
                    apiclient.fetchEntities(Me::class, authInfo.accessToken)
                            .singleOrError()
                            .doOnSuccess { me ->
                                userRepository.addUser(FullUser(
                                        me.account.login,
                                        me.account.firstName,
                                        me.account.lastName,
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
