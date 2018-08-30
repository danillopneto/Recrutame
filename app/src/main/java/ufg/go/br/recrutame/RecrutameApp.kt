package ufg.go.br.recrutame

import android.app.Application
import net.danlew.android.joda.JodaTimeAndroid

class RecrutameApp : Application() {
    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
    }
}