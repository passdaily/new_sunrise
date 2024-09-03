package info.passdaily.new_sun_rise_app.typeofuser.parent.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.new_sun_rise_app.MainRepository
import info.passdaily.new_sun_rise_app.services.Resource
import kotlinx.coroutines.Dispatchers

class TrackViewModel(private val mainRepository: MainRepository) : ViewModel() {


    var TAG = "TrackViewModel"


    fun getTrackMap(dummy: Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getTrackMap(dummy)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getGpsLocation(VEHICLE_ID : Int,TERMINAL_ID: String,SIM_NUMBER: String) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getGpsLocation(VEHICLE_ID,TERMINAL_ID,SIM_NUMBER)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}