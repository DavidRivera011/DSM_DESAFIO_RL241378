package sv.edu.udb.exploramundodesafio.model

import com.google.gson.annotations.SerializedName

data class CountriesResponse(
    @SerializedName("data")
    val data: CountriesData? = null
)

data class CountriesData(
    @SerializedName("objects")
    val objects: List<Country>? = null
)
