package com.wabadaba.dziennik.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.wabadaba.dziennik.BuildConfig
import com.wabadaba.dziennik.R
import kotlinx.android.synthetic.main.acitivity_about.*


class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_about)

        val manager = packageManager
        val info = manager.getPackageInfo(
                packageName, 0)
        val version = info.versionName

        val flavor = BuildConfig.FLAVOR

        activity_about_version.text = getString(R.string.app_version_placeholder, version, flavor)
    }
}