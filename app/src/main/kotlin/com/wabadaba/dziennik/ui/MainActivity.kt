package com.wabadaba.dziennik.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.builders.footer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import co.zsmb.materialdrawerkt.draweritems.profile.profile
import co.zsmb.materialdrawerkt.draweritems.profile.profileSetting
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.bugsnag.android.Bugsnag
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.api.HttpException
import com.wabadaba.dziennik.api.User
import com.wabadaba.dziennik.api.UserRepository
import com.wabadaba.dziennik.di.ViewModelFactory
import com.wabadaba.dziennik.ui.login.LoginActivity
import com.wabadaba.dziennik.vo.LuckyNumber
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.android.synthetic.main.activity_main.*
import mu.KotlinLogging
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MainViewModel

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var fragmentRepository: FragmentRepository

    @Inject
    lateinit var entityRepository: EntityRepository

    @Inject
    lateinit var servicesChecker: GPServicesChecker

    private val logger = KotlinLogging.logger { }

    private val SETTING_ADD_ACCOUNT = 934L
    private val SETTING_LOGOUT = 935L
    private val ITEM_DIVIDER = 936L
    private val ITEM_LUCKY_NUMBER = 937L


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

        MainApplication.mainComponent.inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        val servicesAvailable = servicesChecker.check(this)

        if (servicesAvailable) {
            userRepository.allUsers.observeOn(AndroidSchedulers.mainThread())
                    .subscribe { users ->
                        if (users.isEmpty()) {
                            logger.info { "No users, redirecting to login" }
                            redirectToLogin()
                            finish()
                        } else {
                            logger.info { "${users.size} users logged in" }
                            setupDrawer(users)
                            RxJavaPlugins.setErrorHandler(errorHandler)
                            switchFragment(fragmentRepository.currentFragment)
                            drawer.setSelection(fragmentRepository.currentFragment.drawerId)
                        }
                    }
        }
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
                for ((login, _, _, studentFirstName, studentLastName) in users) {
                    profile {
                        val generator = ColorGenerator.MATERIAL
                        val color = generator.getColor(login)
                        iconDrawable = TextDrawable.builder()
                                .beginConfig()
                                .height(48)
                                .width(48)
                                .endConfig()
                                .buildRect(studentFirstName.substring(0, 1), color)
                        email = login
                        nameShown = true
                        name = "$studentFirstName $studentLastName"
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

            divider { identifier = ITEM_DIVIDER }
            primaryItem {
                icon = R.drawable.ic_sentiment_very_satisfied_black_24dp
                iconTintingEnabled = true
                selectable = false
                identifier = ITEM_LUCKY_NUMBER
            }

            footer {
                primaryItem {
                    nameRes = R.string.suggest_an_idea
                    icon = R.drawable.ic_lightbulb_outline_black_24dp
                    iconTintingEnabled = true
                    selectable = false
                    onClick { _ ->
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://dziennik.idea.informer.com/")))
                        true
                    }
                }
                attachItem(fragmentRepository.settingsFragment.toDrawerItem())
            }
        }
        entityRepository.luckyNumber.observeOn(AndroidSchedulers.mainThread())
                .subscribe { luckyNumbers ->
                    val luckyNumber = luckyNumbers.sortedBy(LuckyNumber::date).reversed().firstOrNull()
                    addLuckyNumberSection(luckyNumber)
                }
    }

    private fun addLuckyNumberSection(luckyNumber: LuckyNumber?) {
        with(drawer) {
            if (luckyNumber?.number != null) {
                val dividerItem = DividerDrawerItem().withIdentifier(ITEM_DIVIDER)
                val luckyNumberItem = PrimaryDrawerItem().withIdentifier(ITEM_LUCKY_NUMBER)
                        .withIcon(R.drawable.ic_sentiment_very_satisfied_black_24dp)
                        .withIconTintingEnabled(true)
                        .withName("Szczęśliwy numerek: ${luckyNumber.number}")
                        .withSelectable(false)
                        .withOnDrawerItemClickListener { _, _, _ ->
                            showLuckyNumber(luckyNumber)
                            false
                        }
                if (getDrawerItem(ITEM_DIVIDER) == null) addItem(dividerItem)
                else updateItem(dividerItem)
                if (getDrawerItem(ITEM_LUCKY_NUMBER) == null) addItem(luckyNumberItem)
                else updateItem(luckyNumberItem)
            } else {
                removeItem(ITEM_DIVIDER)
                removeItem(ITEM_LUCKY_NUMBER)
            }
        }
    }

    private fun showLuckyNumber(luckyNumber: LuckyNumber) {
        if (luckyNumber.date != null)
            Toast.makeText(this, (luckyNumber.date)!!.toString("EEEE, d MMMM"), Toast.LENGTH_LONG).show()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_refresh -> {
                Toast.makeText(this, "Odświeżanie...", Toast.LENGTH_SHORT).show()
                entityRepository.refresh()
            }
        }
        return false
    }

}