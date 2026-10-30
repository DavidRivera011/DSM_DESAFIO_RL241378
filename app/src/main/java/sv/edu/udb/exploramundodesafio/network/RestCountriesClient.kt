package sv.edu.udb.exploramundodesafio.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sv.edu.udb.exploramundodesafio.apikeys.ApiKeysExample

object RestCountriesClient {

    private const val BASE_URL = "https://api.restcountries.com/"

    private const val API_KEY =
        ApiKeysExample.REST_COUNTRIES_API_KEY

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader(
                        "Authorization",
                        "Bearer $API_KEY"
                    )
                    .build()

                chain.proceed(request)
            }
            .build()
    }

    val restCountriesService: RestCountriesService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(RestCountriesService::class.java)
    }
}
