package sv.edu.udb.exploramundodesafio.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("location")
    val location: WeatherLocation? = null,

    @SerializedName("current")
    val current: CurrentWeather? = null
)

data class WeatherLocation(
    @SerializedName("name")
    val name: String? = null
)

data class CurrentWeather(
    @SerializedName("last_updated")
    val lastUpdated: String? = null,

    @SerializedName("temp_c")
    val temperatureC: Double? = null,

    @SerializedName("temp_f")
    val temperatureF: Double? = null,

    @SerializedName("condition")
    val condition: WeatherCondition? = null,

    @SerializedName("wind_kph")
    val windKph: Double? = null,

    @SerializedName("wind_mph")
    val windMph: Double? = null,

    @SerializedName("humidity")
    val humidity: Int? = null
)

data class WeatherCondition(
    @SerializedName("text")
    val text: String? = null,

    @SerializedName("icon")
    val icon: String? = null,

    @SerializedName("code")
    val code: Int? = null
)

data class WeatherApiErrorResponse(
    @SerializedName("error")
    val error: WeatherApiError? = null
)

data class WeatherApiError(
    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("message")
    val message: String? = null
)