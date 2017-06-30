package com.wabadaba.dziennik

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bugsnag.android.Bugsnag

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Bugsnag.init(this)
        Bugsnag.notify(RuntimeException("Non-fatal"))
    }
}
