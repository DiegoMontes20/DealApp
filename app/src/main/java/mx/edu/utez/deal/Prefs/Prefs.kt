package mx.edu.utez.deal.Prefs

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    val PREFS_NAME = "mx.edu.utez.deal"

    val STORAGE: SharedPreferences = context.getSharedPreferences(PREFS_NAME,0)


    fun save(key:String,value:String){
        STORAGE.edit().putString(key, value).apply()
    }

    fun getData(key:String):String{
        return STORAGE.getString(key,"")!!
    }

    fun deleteAll(){
        STORAGE.edit().clear().apply()
    }

}