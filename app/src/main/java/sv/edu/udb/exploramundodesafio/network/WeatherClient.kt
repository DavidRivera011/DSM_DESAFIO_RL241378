package sv.edu.udb.exploramundodesafio.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sv.edu.udb.exploramundodesafio.apikeys.ApiKeysExample

object WeatherClient {

    private const val BASE_URL = "https://api.weatherapi.com/v1/"

    private const val API_KEY =
        ApiKeysExample.WEATHER_API_KEY

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    val service: WeatherService by lazy {
        retrofit.create(WeatherService::class.java)
    }

    fun getApiKey(): String {
        return API_KEY
    }
}