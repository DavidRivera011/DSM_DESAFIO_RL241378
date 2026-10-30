package sv.edu.udb.exploramundodesafio.model

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonObject

data class Country(
    @SerializedName("names")
    val names: CountryNames? = null,

    @SerializedName("flag")
    val flag: CountryFlag? = null,

    @SerializedName("capitals")
    val capitals: List<CountryCapital>? = null,

    @SerializedName("region")
    val region: String? = null,

    @SerializedName("subregion")
    val subregion: String? = null,

    @SerializedName("population")
    val population: Long? = null,

    @SerializedName("codes")
    val codes: CountryCodes? = null,

    @SerializedName("currencies")
    val currencies: List<CountryCurrency>? = null,

    @SerializedName("languages")
    val languages: List<JsonObject>? = null,

    @SerializedName("coordinates")
    val coordinates: CountryCoordinates? = null
) {
    fun displayName(): String =
        names?.common ?: "País sin nombre"

    fun displayOfficialName(): String =
        names?.official ?: "No disponible"

    fun displayCapital(): String =
        capitals?.firstOrNull()?.name ?: "No disponible"

    fun flagUrl(): String? =
        flag?.urlPng?.takeIf { it.isNotBlank() }
            ?: flag?.urlSvg?.takeIf { it.isNotBlank() }

    fun displayCurrencies(): String =
        currencies
            ?.mapNotNull { currency ->
                currency.name?.let { name ->
                    currency.symbol?.let { "$name ($it)" } ?: name
                }
            }
            ?.joinToString(", ")
            ?.ifBlank { null }
            ?: "No disponible"

    fun displayLanguages(): String =
        languages
            ?.mapNotNull { language ->
                listOf("name", "language", "common")
                    .firstNotNullOfOrNull { key ->
                        language.get(key)
                            ?.takeIf { it.isJsonPrimitive }
                            ?.asString
                    }
            }
            ?.distinct()
            ?.joinToString(", ")
            ?.ifBlank { null }
            ?: "No disponible"

    fun displayCoordinates(): String {
        val lat = coordinates?.lat
        val lng = coordinates?.lng

        return if (lat != null && lng != null) {
            "$lat, $lng"
        } else {
            "No disponibles"
        }
    }
}

data class CountryNames(
    @SerializedName("common")
    val common: String? = null,

    @SerializedName("official")
    val official: String? = null
)

data class CountryFlag(
    @SerializedName("url_png")
    val urlPng: String? = null,

    @SerializedName("url_svg")
    val urlSvg: String? = null,

    @SerializedName("emoji")
    val emoji: String? = null,

    @SerializedName("description")
    val description: String? = null
)

data class CountryCapital(
    @SerializedName("name")
    val name: String? = null
)

data class CountryCodes(
    @SerializedName("alpha_2")
    val alpha2: String? = null,

    @SerializedName("alpha_3")
    val alpha3: String? = null
)

data class CountryCurrency(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("symbol")
    val symbol: String? = null
)

data class CountryCoordinates(
    @SerializedName("lat")
    val lat: Double? = null,

    @SerializedName("lng")
    val lng: Double? = null
)
