package com.wabadaba.dziennik.ui


import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.wabadaba.dziennik.R


class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    companion object {
        fun newInstance(): SettingsFragment {
            val fragment = SettingsFragment()
            return fragment
        }
    }
}
