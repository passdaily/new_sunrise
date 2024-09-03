package info.passdaily.new_sun_rise_app.services.permission

import android.os.Build

inline fun<T> sdk29AndUp(onSdk29 : ()-> T):T?{
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
        onSdk29()
    }else  null
}