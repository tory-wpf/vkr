package com.example.vkr

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import io.realm.Realm

class App: Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var ctx: Context
    }
    override fun onCreate() {
        super.onCreate()
        ctx = this
        Realm.init(this)
    }
}