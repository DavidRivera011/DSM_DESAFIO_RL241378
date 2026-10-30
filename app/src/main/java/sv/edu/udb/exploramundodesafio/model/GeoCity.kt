package sv.edu.udb.exploramundodesafio.model

import com.google.gson.annotations.SerializedName

data class GeoCity(
    @SerializedName("geonameId")
    val geonameId: Long? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("toponymName")
    val toponymName: String? = null,

    @SerializedName("population")
    val population: Long? = null,

    @SerializedName("lat")
    val latitude: Double? = null,

    @SerializedName("lng")
    val longitude: Double? = null,

    @SerializedName("countryCode")
    val countryCode: String? = null,

    @SerializedName("adminName1")
    val department: String? = null,

    @SerializedName("fcode")
    val featureCode: String? = null,

    @SerializedName("fcodeName")
    val featureName: String? = null
) {
    fun isCapital(): Boolean {
        return featureCode == "PPLC"
    }

    fun displayName(): String {
        return name ?: "Ciudad sin nombre"
    }

    fun displayPopulation(): String {
        return population?.toString() ?: "No disponible"
    }
}
