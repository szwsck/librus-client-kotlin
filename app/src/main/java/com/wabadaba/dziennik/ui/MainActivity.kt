package com.wabadaba.dziennik.ui

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.builders.footer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.profile.profile
import co.zsmb.materialdrawerkt.draweritems.profile.profileSetting
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.api.RxHttpClient
import com.wabadaba.dziennik.api.User
import com.wabadaba.dziennik.api.UserRepository
import com.wabadaba.dziennik.di.ViewModelFactory
import com.wabadaba.dziennik.ui.grades.GradesFragment
import com.wabadaba.dziennik.ui.login.LoginActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import mu.KotlinLogging
import javax.inject.Inject

class MainActivity : AppCompatActivity(), LifecycleRegistryOwner {

    private val registry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry {
        return registry
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var rxHttpClient: RxHttpClient

    val logger = KotlinLogging.logger { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = getString(R.string.app_name)
        setSupportActionBar(toolbar_main)

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
                .subscribe { logger.info { "Current user: ${it.login}" } }
    }

    private val SETTING_ADD_ACCOUNT = 934L
    private val SETTING_LOGOUT = 935L

    private fun setupDrawer(users: List<User>) = drawer {
        actionBarDrawerToggleAnimated = true
        toolbar = toolbar_main
        accountHeader {
            background = R.drawable.navbackground
            currentHidden = true
            onProfileChanged { _, profile, current ->
                if (profile.identifier == SETTING_ADD_ACCOUNT) {
                    redirectToLogin()
                    return@onProfileChanged true
                } else if (profile.identifier == SETTING_LOGOUT) {
                    userRepository.removeUser(userRepository.currentUser.blockingFirst().login)
                    return@onProfileChanged false
                } else if (!current && profile is ProfileDrawerItem) {
                    userRepository.switchUser(profile.tag as String)
                    return@onProfileChanged false
                } else (return@onProfileChanged false)
            }
            for ((login, firstName, lastName, groupId) in users) {
                profile {
                    val generator = ColorGenerator.MATERIAL
                    val color = generator.getColor(login)
                    iconDrawable = TextDrawable.builder()
                            .beginConfig()
                            .height(48)
                            .width(48)
                            .endConfig()
                            .buildRect(firstName.substring(0, 1), color)
                    email =
                            if (groupId == 8) getString(R.string.student)
                            else if (groupId == 5) getString(R.string.parent)
                            else login
                    nameShown = true
                    name = "$firstName $lastName"
                    tag = login
                }
            }
            profileSetting {
                nameRes = R.string.add_account
                icon = R.drawable.ic_add_black_24dp
                iconTinted = true
                identifier = SETTING_ADD_ACCOUNT
            }
            profileSetting {
                nameRes = R.string.logout
                icon = R.drawable.ic_exit_to_app_black_24dp
                iconTinted = true
                identifier = SETTING_LOGOUT
            }
        }
        primaryItem {
            nameRes = R.string.grades
            icon = R.drawable.ic_assignment_black_24dp
            iconTintingEnabled = true
            onClick { _ ->
                switchFragment(GradesFragment())
                false
            }
        }
        footer {
            primaryItem(R.string.settings) {
                icon = R.drawable.ic_settings_black_24dp
                selectable = false
                iconTintingEnabled = true
                onClick { _ ->
                    logger.info { "onClick: settings" }
                    switchFragment(SettingsFragment.newInstance())
                    false
                }
            }
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.content_main, fragment)
                .commit()
    }

    fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT))
    }

}