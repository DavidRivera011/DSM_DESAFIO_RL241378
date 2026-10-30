package sv.edu.udb.exploramundodesafio.model

import com.google.gson.annotations.SerializedName

data class GeoNamesResponse(

    @SerializedName("totalResultsCount")
    val totalResultsCount: Int? = null,

    @SerializedName("geonames")
    val geonames: List<GeoCity>? = null,

    @SerializedName("status")
    val status: GeoNamesStatus? = null
)

data class GeoNamesStatus(

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("value")
    val value: Int? = null
)