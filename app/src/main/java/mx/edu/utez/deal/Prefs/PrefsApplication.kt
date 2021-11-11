package mx.edu.utez.deal.Prefs

import android.app.Application

class PrefsApplication : Application() {

    companion object {
        lateinit var prefs: Prefs
    }

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(this)
    }
}