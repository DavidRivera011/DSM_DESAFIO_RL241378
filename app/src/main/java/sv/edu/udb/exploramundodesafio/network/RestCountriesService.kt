package sv.edu.udb.exploramundodesafio.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import sv.edu.udb.exploramundodesafio.model.CountriesResponse

interface RestCountriesService {

    @GET("countries/v5")
    fun getCountriesByRegion(
        @Query("region") region: String,
        @Query("limit") limit: Int = 100,
        @Query("response_fields") fields: String =
            "names.common,flag.url_png,flag.url_svg,flag.emoji,capitals,region,codes.alpha_2,codes.alpha_3"
    ): Call<CountriesResponse>
}
