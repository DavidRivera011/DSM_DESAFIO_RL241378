package sv.edu.udb.exploramundodesafio.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import sv.edu.udb.exploramundodesafio.model.CountriesResponse

interface RestCountriesService {

    @GET("countries/v5")
    suspend fun getCountriesByRegion(
        @Query("region") region: String,
        @Query("limit") limit: Int = 100,
        @Query("response_fields")
        fields: String =
            "names.common,names.official," +
                    "flag.url_png,flag.url_svg,flag.emoji," +
                    "capitals,region,subregion,population," +
                    "codes.alpha_2,codes.alpha_3," +
                    "currencies,languages,coordinates"
    ): Response<CountriesResponse>
}
