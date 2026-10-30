package sv.edu.udb.exploramundodesafio.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import sv.edu.udb.exploramundodesafio.model.WeatherResponse

interface WeatherService {

    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") coordinates: String,
        @Query("lang") language: String = "es"
    ): Response<WeatherResponse>
}
