package info.passdaily.new_sun_rise_app.lib

interface ImageUploadCallback {
    fun onProgressUpdate(percentage: Int)
    fun onError(message: String?)
    fun onSuccess(message: String?)
}