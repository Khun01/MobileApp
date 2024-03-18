package com.example.bagouli.Utils

import android.app.Application

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    companion object{
        private var instance : MyApp? = null
        fun getAppContext(): MyApp{
            return instance!!
        }
    }
}