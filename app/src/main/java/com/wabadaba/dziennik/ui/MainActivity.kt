package com.wabadaba.dziennik.ui

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.builders.footer
import co.zsmb.materialdrawerkt.draweritems.profile.profile
import co.zsmb.materialdrawerkt.draweritems.profile.profileSetting
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.bugsnag.android.Bugsnag
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.wabadaba.dziennik.BuildConfig
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.api.HttpException
import com.wabadaba.dziennik.api.RxHttpClient
import com.wabadaba.dziennik.api.User
import com.wabadaba.dziennik.api.UserRepository
import com.wabadaba.dziennik.api.notification.LibrusGCMRegistrationManager
import com.wabadaba.dziennik.di.ViewModelFactory
import com.wabadaba.dziennik.ui.login.LoginActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.android.synthetic.main.activity_main.*
import mu.KotlinLogging
import javax.inject.Inject


class MainActivity : AppCompatActivity(), LifecycleRegistryOwner {

    private val senderId = BuildConfig.SENDER_ID

    private val registry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry {
        return registry
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MainViewModel

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var rxHttpClient: RxHttpClient

    @Inject
    lateinit var fragmentRepository: FragmentRepository

    private val logger = KotlinLogging.logger { }

    private val SETTING_ADD_ACCOUNT = 934L
    private val SETTING_LOGOUT = 935L

    private lateinit var drawer: Drawer

    private val errorHandler = Consumer<Throwable> { t: Throwable ->
        Bugsnag.notify(t)

        val cause = if (t is UndeliverableException || t is OnErrorNotImplementedException)
            t.cause
        else
            t

        print(cause?.message)
        cause?.printStackTrace()

        val errorMessage = when (cause) {
            is HttpException.DeviceOffline -> "Brak połączenia"
            is HttpException.ServerOffline -> "Błąd serwera"
            is HttpException.Maintenance -> "Nie udało się pobrać danych ponieważ trwa przerwa techniczna"
            else -> "Nieoczekiwany błąd"
        }
        Snackbar.make(
                findViewById(R.id.activity_main_coordinator),
                errorMessage,
                Snackbar.LENGTH_LONG)
                .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = getString(R.string.app_name)
        setSupportActionBar(toolbar_main)

        val mainApplication = applicationContext as MainApplication
        mainApplication.mainComponent.inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        if (checkGooglePlayServicesAvailable()) {
            userRepository.allUsers.observeOn(AndroidSchedulers.mainThread())
                    .subscribe { users ->
                        if (users.isEmpty()) {
                            logger.info { "No users, redirecting to login" }
                            redirectToLogin()
                            finish()
                        } else {
                            registerGCM()
                            logger.info { "${users.size} users logged in" }
                            setupDrawer(users)
                            RxJavaPlugins.setErrorHandler(errorHandler)
                            switchFragment(fragmentRepository.currentFragment)
                            drawer.setSelection(fragmentRepository.currentFragment.drawerId)
                        }
                    }
        }
    }

    private fun registerGCM() {
        userRepository.currentUser.subscribe {
            LibrusGCMRegistrationManager(it, rxHttpClient, this)
                    .register()
        }
    }

    private fun checkGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show()
            }
            return false
        }
        return true
    }

    @Suppress("DEPRECATION")
    private fun setupDrawer(users: List<User>) {
        drawer = drawer {
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
                        email = when (groupId) {
                            8 -> getString(R.string.student)
                            5 -> getString(R.string.parent)
                            else -> login
                        }
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
            fragmentRepository.mainFragments.forEach { fragmentInfo -> attachItem(fragmentInfo.toDrawerItem()) }
            footer {
                attachItem(fragmentRepository.settingsFragment.toDrawerItem())
            }
        }
    }

    private fun switchFragment(fragmentInfo: FragmentInfo) {
        Handler().post({
            fragmentRepository.currentFragment = fragmentInfo
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content_main, fragmentInfo.kClass.java.newInstance())
                    .commitAllowingStateLoss()
        })

    }

    private fun FragmentInfo.toDrawerItem() = PrimaryDrawerItem()
            .withIdentifier(drawerId)
            .withName(title)
            .withIcon(icon)
            .withIconTintingEnabled(true)
            .withOnDrawerItemClickListener { _, _, _ ->
                switchFragment(this)
                false
            }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT))
    }

}