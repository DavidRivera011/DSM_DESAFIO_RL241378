package sv.edu.udb.exploramundodesafio.model

data class CityWeather(
    val city: GeoCity,
    var temperature: String = "Consultando...",
    var condition: String = "Consultando clima...",
    var humidity: String = "No disponible",
    var wind: String = "No disponible",
    var iconUrl: String? = null,
    var isLoading: Boolean = true
)