package com.wabadaba.dziennik.ui

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.api.User
import com.wabadaba.dziennik.api.UserRepository
import com.wabadaba.dziennik.di.ViewModelFactory
import com.wabadaba.dziennik.ui.login.LoginActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import mu.KotlinLogging
import javax.inject.Inject

class MainActivity : LifecycleActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var userRepository: UserRepository

    val logger = KotlinLogging.logger { }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainApplication = applicationContext as MainApplication
        mainApplication.mainComponent.inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        userRepository.allUsers.observeOn(AndroidSchedulers.mainThread())
                .subscribe { users ->
                    if (users.isEmpty()) {
                        logger.info { "No users, redirecting to login" }
                        redirectToLogin()
                        finish()
                    } else {
                        logger.info { "${users.size} users logged in" }
                        setupDrawer(users)
                    }
                }
        userRepository.currentUser.observeOn(AndroidSchedulers.mainThread())
                .subscribe { user ->
                    logger.info { "Current user: ${user.login}" }
                    logged_in_as.text = "Logged in as ${user.login}"
                }
    }

    private val SETTING_ADD_ACCOUNT = 934L
    private val SETTING_LOGOUT = 935L

    private fun setupDrawer(users: List<User>) {
        val addAccountItem = ProfileSettingDrawerItem()
                .withName(getString(R.string.add_account))
                .withIcon(R.drawable.ic_add_black_24dp)
                .withIconTinted(true)
                .withIdentifier(SETTING_ADD_ACCOUNT)
        val logoutItem = ProfileSettingDrawerItem()
                .withName(getString(R.string.logout))
                .withIcon(R.drawable.ic_exit_to_app_black_24dp)
                .withIconTinted(true)
                .withIdentifier(SETTING_LOGOUT)

        val accountHeader = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.navbackground)
                .withCurrentProfileHiddenInList(true)
                .withSelectionListEnabledForSingleProfile(false)
                .withOnAccountHeaderListener({ _, profile, current ->
                    if (profile.identifier == SETTING_ADD_ACCOUNT) {
                        redirectToLogin()
                        return@withOnAccountHeaderListener true
                    } else if (profile.identifier == SETTING_LOGOUT) {
                        userRepository.removeUser(userRepository.currentUser.blockingFirst().login)
                        return@withOnAccountHeaderListener false
                    } else if (!current && profile is ProfileDrawerItem) {
                        userRepository.switchUser(profile.tag as String)
                        return@withOnAccountHeaderListener false
                    } else (return@withOnAccountHeaderListener false)
                })
                .withProfiles(users.map(this::getProfile) + addAccountItem + logoutItem)
                .build()

        DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(accountHeader)
                .build()
    }

    private fun getProfile(user: User): IProfile<*> {
        val generator = ColorGenerator.MATERIAL
        val color = generator.getColor(user.login)
        val icon = TextDrawable.builder()
                .beginConfig()
                .height(48)
                .width(48)
                .endConfig()
                .buildRect(user.firstName.substring(0, 1), color)
        val subtitle: String =
                if (user.groupId == 8) getString(R.string.student)
                else if (user.groupId == 5) getString(R.string.parent)
                else user.login

        return ProfileDrawerItem()
                .withName("${user.firstName} ${user.lastName}")
                .withEmail(subtitle)
                .withNameShown(true)
                .withIcon(icon)
                .withTag(user.login)
    }

    fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT))
    }

}
