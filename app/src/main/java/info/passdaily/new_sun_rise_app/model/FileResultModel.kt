package info.passdaily.new_sun_rise_app.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class FileResultModel(
    @SerializedName("DETAILS")
    val dETAILS: String,
    @SerializedName("RESULT")
    val rESULT: String
)