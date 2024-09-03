package info.passdaily.new_sun_rise_app.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AcademicListModel(
    @SerializedName("AccademicDetails")
    val accademicDetails: List<AccademicDetail>
){
    @Keep
    data class AccademicDetail(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_STATUS")
        val aCCADEMICSTATUS: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String
    )
}