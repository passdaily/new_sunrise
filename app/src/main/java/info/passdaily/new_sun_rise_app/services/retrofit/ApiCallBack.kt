package info.passdaily.new_sun_rise_app.services.retrofit

interface ApiCallBack<T> {

    fun  onFailure(error : String)
    fun  onError(error : String)
    fun  onSuccess(response : T)
}