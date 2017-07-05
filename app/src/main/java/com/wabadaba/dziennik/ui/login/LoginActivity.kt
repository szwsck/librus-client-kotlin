package com.wabadaba.dziennik.ui.login

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.di.ViewModelFactory
import kotlinx.android.synthetic.main.activity_login.*
import mu.KotlinLogging
import javax.inject.Inject

class LoginActivity : LifecycleActivity() {

    val logger = KotlinLogging.logger { }

    @Inject lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as MainApplication).component.inject(this)
        setContentView(R.layout.activity_login)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)
        logInButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            viewModel.login(username, password)
                    .subscribe {
                        logger.info { "logged in!!1oneone" }
                    }
        }
    }
}
