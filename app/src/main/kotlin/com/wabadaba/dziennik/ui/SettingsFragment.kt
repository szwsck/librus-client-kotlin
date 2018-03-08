package com.wabadaba.dziennik.ui

import android.app.Activity
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceFragmentCompat
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainApplication.mainComponent.inject(this)

        val defaultFragmentPreference: ListPreference = findPreference("defaultFragment") as ListPreference

        val fragmentRepo = FragmentRepository(activity as Activity)

        val titles = fragmentRepo.mainFragments.map { context?.getString(it.title) }.toTypedArray()
        val ids = fragmentRepo.mainFragments.map { it.fragmentId }.toTypedArray()

        defaultFragmentPreference.apply {
            entries = titles
            entryValues = ids
            if (value == null) {
                setValueIndex(0)
            }
        }

    }
}