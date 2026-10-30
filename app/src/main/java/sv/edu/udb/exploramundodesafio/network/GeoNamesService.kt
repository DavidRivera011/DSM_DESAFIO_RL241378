package sv.edu.udb.exploramundodesafio.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import sv.edu.udb.exploramundodesafio.model.GeoNamesResponse

interface GeoNamesService {

    @GET("searchJSON")
    suspend fun getCities(
        @Query("country")
        countryCode: String,

        @Query("featureClass")
        featureClass: String = "P",

        @Query("cities")
        cities: String = "cities15000",

        @Query("maxRows")
        maxRows: Int = 10,

        @Query("orderby")
        orderBy: String = "population",

        @Query("username")
        username: String
    ): Response<GeoNamesResponse>
}
