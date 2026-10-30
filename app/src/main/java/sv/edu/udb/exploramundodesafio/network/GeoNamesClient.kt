package sv.edu.udb.exploramundodesafio.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sv.edu.udb.exploramundodesafio.apikeys.ApiKeysExample

object GeoNamesClient {

    private const val BASE_URL = "https://secure.geonames.org/"

    private const val GEONAMES_USERNAME =
        ApiKeysExample.GEONAMES_USERNAME

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: GeoNamesService by lazy {
        retrofit.create(GeoNamesService::class.java)
    }

    fun getUsername(): String {
        return GEONAMES_USERNAME
    }
}
