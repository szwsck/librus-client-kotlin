package com.wabadaba.dziennik.ui

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.wabadaba.dziennik.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
            = addPreferencesFromResource(R.xml.preferences)

    companion object {
        fun newInstance(): SettingsFragment = SettingsFragment()
    }
}
