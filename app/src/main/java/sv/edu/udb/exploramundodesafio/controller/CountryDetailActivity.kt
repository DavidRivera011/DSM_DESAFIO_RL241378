package sv.edu.udb.exploramundodesafio.controller

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Response
import sv.edu.udb.exploramundodesafio.R
import sv.edu.udb.exploramundodesafio.adapter.CityWeatherAdapter
import sv.edu.udb.exploramundodesafio.model.CityWeather
import sv.edu.udb.exploramundodesafio.model.Country
import sv.edu.udb.exploramundodesafio.model.GeoCity
import sv.edu.udb.exploramundodesafio.model.GeoNamesResponse
import sv.edu.udb.exploramundodesafio.model.WeatherResponse
import sv.edu.udb.exploramundodesafio.network.GeoNamesClient
import sv.edu.udb.exploramundodesafio.network.WeatherClient
import java.text.NumberFormat
import java.util.Locale
import android.widget.ScrollView
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.io.IOException
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class CountryDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_COUNTRY_JSON = "extra_country_json"
    }

    private lateinit var countryMap: MapView
    private lateinit var mapProgress: ProgressBar
    private lateinit var mapMessage: TextView
    private lateinit var citiesCount: TextView

    private lateinit var citiesRecyclerView: RecyclerView
    private lateinit var cityWeatherAdapter: CityWeatherAdapter
    private lateinit var detailScrollView: android.widget.ScrollView

    private val cityWeatherList = mutableListOf<CityWeather>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_country_detail)
        detailScrollView = findViewById(R.id.detailScrollView)

        // Inicializar elementos de la pantalla
        val backButton: Button = findViewById(R.id.backButton)
        val nameTextView: TextView = findViewById(R.id.detailCountryName)
        val flagImageView: ImageView = findViewById(R.id.detailCountryFlag)

        // Inicializar información del mapa
        countryMap = findViewById(R.id.countryMap)
        mapProgress = findViewById(R.id.mapProgress)
        mapMessage = findViewById(R.id.mapMessage)
        citiesCount = findViewById(R.id.citiesCount)

        // Inicializar lista de ciudades y clima
        citiesRecyclerView = findViewById(R.id.citiesRecyclerView)

        cityWeatherAdapter = CityWeatherAdapter(cityWeatherList) { cityWeather ->
            locateCityOnMap(cityWeather)
        }

        citiesRecyclerView.layoutManager = LinearLayoutManager(this)
        citiesRecyclerView.adapter = cityWeatherAdapter

        citiesRecyclerView.isNestedScrollingEnabled = false

        // Configurar mapa
        countryMap.setTileSource(TileSourceFactory.MAPNIK)
        countryMap.setMultiTouchControls(true)
        countryMap.controller.setZoom(3.0)

        // Evitar que el ScrollView intercepte los gestos del mapa
        countryMap.setOnTouchListener { view, event ->

            when (event.actionMasked) {

                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE -> {
                    view.parent?.requestDisallowInterceptTouchEvent(true)
                }

                MotionEvent.ACTION_UP -> {
                    view.parent?.requestDisallowInterceptTouchEvent(false)
                    view.performClick()
                }

                MotionEvent.ACTION_CANCEL -> {
                    view.parent?.requestDisallowInterceptTouchEvent(false)
                }
            }

            false
        }

        // Botón para regresar
        backButton.setOnClickListener {
            finish()
        }

        // Recuperar información del país seleccionado
        val countryJson = intent.getStringExtra(EXTRA_COUNTRY_JSON)

        val country = countryJson?.let {
            runCatching {
                Gson().fromJson(it, Country::class.java)
            }.getOrNull()
        }

        // Validar información recibida
        if (country == null) {
            nameTextView.text = "No se pudo cargar el país"
            mapMessage.text = "No se pudo cargar la información."
            return
        }

        // Mostrar información del país
        showCountryInformation(
            country,
            nameTextView,
            flagImageView
        )

        // Consultar ciudades mediante GeoNames
        loadCities(
            country.codes?.alpha2,
            country.coordinates?.let {
                listOfNotNull(it.lat, it.lng)
            }
        )
    }

    // Mostrar información general del país
    private fun showCountryInformation(
        country: Country,
        nameTextView: TextView,
        flagImageView: ImageView
    ) {

        nameTextView.text = country.displayName()

        findViewById<TextView>(R.id.detailOfficialName).text =
            "Nombre oficial: ${country.displayOfficialName()}"

        findViewById<TextView>(R.id.detailCapital).text =
            "Capital: ${country.displayCapital()}"

        findViewById<TextView>(R.id.detailRegion).text =
            "Región: ${country.region ?: "No disponible"}"

        findViewById<TextView>(R.id.detailSubregion).text =
            "Subregión: ${country.subregion ?: "No disponible"}"

        val population = country.population?.let {
            NumberFormat.getNumberInstance(Locale.getDefault())
                .format(it)
        } ?: "No disponible"

        findViewById<TextView>(R.id.detailPopulation).text =
            "Población: $population"

        findViewById<TextView>(R.id.detailCodes).text =
            "Códigos ISO: Alfa-2: ${country.codes?.alpha2 ?: "N/D"} · " +
                    "Alfa-3: ${country.codes?.alpha3 ?: "N/D"}"

        findViewById<TextView>(R.id.detailCurrencies).text =
            "Monedas: ${country.displayCurrencies()}"

        findViewById<TextView>(R.id.detailLanguages).text =
            "Idiomas: ${country.displayLanguages()}"

        findViewById<TextView>(R.id.detailCoordinates).text =
            "Coordenadas: ${country.displayCoordinates()}"

        val flagUrl = country.flagUrl()

        if (!flagUrl.isNullOrBlank()) {

            Glide.with(this)
                .load(flagUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(flagImageView)

        } else {

            flagImageView.setImageResource(
                android.R.drawable.ic_menu_report_image
            )
        }
    }

    // Consultar ciudades principales mediante GeoNames
    private fun loadCities(
        countryCode: String?,
        countryCoordinates: List<Double>?
    ) {

        if (countryCode.isNullOrBlank()) {

            mapProgress.visibility = View.GONE
            mapMessage.text = "No se encontró el código ISO del país."
            showCountryCenter(countryCoordinates)

            return
        }

        lifecycleScope.launch {

            mapProgress.visibility = View.VISIBLE
            mapMessage.visibility = View.VISIBLE
            mapMessage.text = "Consultando ciudades..."
            citiesCount.text = "Cargando ciudades principales..."

            try {

                val response = GeoNamesClient.service.getCities(
                    countryCode = countryCode,
                    username = GeoNamesClient.getUsername()
                )

                mapProgress.visibility = View.GONE

                if (!response.isSuccessful) {

                    mapMessage.text = getHttpErrorMessage(
                        "GeoNames",
                        response.code()
                    )

                    citiesCount.text = "No fue posible obtener las ciudades."

                    showCountryCenter(countryCoordinates)

                    return@launch
                }

                val geoNamesResponse = response.body()

                // Verificar errores devueltos dentro del JSON.
                val geoNamesError = geoNamesResponse?.status

                if (geoNamesError != null) {

                    val errorMessage = geoNamesError.message
                        ?: "GeoNames no pudo procesar la solicitud."

                    mapMessage.text = "Error de GeoNames: $errorMessage"

                    citiesCount.text = "No fue posible obtener las ciudades."

                    showCountryCenter(countryCoordinates)

                    return@launch
                }

                val cities = geoNamesResponse?.geonames.orEmpty()
                    .filter {
                        it.latitude != null && it.longitude != null
                    }

                if (cities.isEmpty()) {

                    mapMessage.text =
                        "No se encontraron ciudades para este país."

                    citiesCount.text = "Sin ciudades disponibles."

                    showCountryCenter(countryCoordinates)

                    return@launch
                }

                showCitiesOnMap(cities)

                mapMessage.text =
                    "Toca un marcador para ver la ciudad y su clima."

                citiesCount.text =
                    "${cities.size} ciudades principales encontradas."

            } catch (e: CancellationException) {

                throw e

            } catch (e: Throwable) {

                mapProgress.visibility = View.GONE

                mapMessage.text = getNetworkErrorMessage(e)
                citiesCount.text = "No se pudieron cargar las ciudades."

                showCountryCenter(countryCoordinates)
            }
        }
    }

    // Mostrar ciudades en el mapa y en las tarjetas
    private fun showCitiesOnMap(cities: List<GeoCity>) {

        countryMap.overlays.clear()

        // Crear tarjetas iniciales
        cityWeatherList.clear()

        val initialCities = cities.map { city ->
            CityWeather(city = city)
        }

        cityWeatherAdapter.setCities(initialCities)

        val geoPoints = mutableListOf<GeoPoint>()

        cities.forEach { city ->

            val latitude = city.latitude ?: return@forEach
            val longitude = city.longitude ?: return@forEach

            val point = GeoPoint(latitude, longitude)

            geoPoints.add(point)

            val marker = Marker(countryMap)

            marker.position = point

            marker.title =
                if (city.isCapital()) {
                    "★ ${city.displayName()} (Capital)"
                } else {
                    city.displayName()
                }

            marker.snippet =
                "Población: ${city.displayPopulation()}\n" +
                        "Departamento: ${city.department ?: "No disponible"}\n" +
                        "Consultando clima..."

            marker.setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )

            countryMap.overlays.add(marker)

            // Consultar clima individualmente
            loadWeatherForCity(city, marker)
        }

        // Centrar mapa en las ciudades
        if (geoPoints.isNotEmpty()) {

            val centerLatitude =
                geoPoints.map { it.latitude }.average()

            val centerLongitude =
                geoPoints.map { it.longitude }.average()

            countryMap.controller.setZoom(5.5)

            countryMap.controller.animateTo(
                GeoPoint(centerLatitude, centerLongitude)
            )
        }

        countryMap.invalidate()
    }

    private fun locateCityOnMap(cityWeather: CityWeather) {

        val latitude = cityWeather.city.latitude ?: return
        val longitude = cityWeather.city.longitude ?: return

        val cityPoint = GeoPoint(latitude, longitude)

        // Obtener el contenedor completo del mapa
        val mapContainer: View = findViewById(R.id.mapContainer)

        // Calcular la posición para dejar el mapa en el centro
        detailScrollView.post {

            val scrollPosition = mapContainer.top -
                    (detailScrollView.height - mapContainer.height) / 2

            // Desplazamiento suave hasta centrar el mapa
            detailScrollView.smoothScrollTo(
                0,
                scrollPosition.coerceAtLeast(0)
            )
        }

        // Centrar el mapa en la ciudad seleccionada
        countryMap.controller.setZoom(11.0)
        countryMap.controller.animateTo(cityPoint)

        countryMap.invalidate()
    }

    // Identificar errores HTTP de las APIs
    private fun getHttpErrorMessage(apiName: String, code: Int): String {
        return when (code) {
            400 -> "$apiName: solicitud incorrecta."
            401, 403 -> "$apiName: credenciales inválidas o acceso no autorizado."
            404 -> "$apiName: recurso no encontrado."
            408 -> "$apiName: tiempo de espera agotado."
            429 -> "$apiName: límite de solicitudes alcanzado."
            in 500..599 -> "$apiName: error en el servidor. Intenta más tarde."
            else -> "$apiName: error al consultar los datos (código $code)."
        }
    }

    // Identificar errores específicos de WeatherAPI
    private fun getWeatherApiErrorMessage(
        response: Response<WeatherResponse>
    ): String {

        val errorBody = response.errorBody()?.string()

        val apiError = runCatching {
            Gson().fromJson(
                errorBody,
                sv.edu.udb.exploramundodesafio.model.WeatherApiErrorResponse::class.java
            )?.error
        }.getOrNull()

        return when (apiError?.code) {

            1002, 2006 ->
                "WeatherAPI: la API Key no es válida."
            1006 ->
                "WeatherAPI: no se encontró la ubicación solicitada."
            2007 ->
                "WeatherAPI: se alcanzó el límite de solicitudes."
            2008 ->
                "WeatherAPI: el servicio está deshabilitado para esta cuenta."
            2009 ->
                "WeatherAPI: la cuenta no tiene permisos para esta consulta."
            else ->
                apiError?.message?.let {
                    "WeatherAPI: $it"
                } ?: getHttpErrorMessage("WeatherAPI", response.code())
        }
    }

    // Identificar problemas de conexión
    private fun getNetworkErrorMessage(error: Throwable): String {
        return when (error) {
            is SocketTimeoutException ->
                "Tiempo de espera agotado. Intenta nuevamente."

            is UnknownHostException ->
                "No hay conexión a Internet o no se pudo contactar al servidor."

            is IOException ->
                "Error de comunicación con el servidor."

            else ->
                "Ocurrió un error inesperado al consultar la información."
        }
    }



    // Consultar clima de una ciudad mediante WeatherAPI
    private fun loadWeatherForCity(
        city: GeoCity,
        marker: Marker
    ) {

        val latitude = city.latitude ?: return
        val longitude = city.longitude ?: return

        val coordinates = "$latitude,$longitude"

        lifecycleScope.launch {

            try {

                val response = WeatherClient.service.getCurrentWeather(
                    apiKey = WeatherClient.getApiKey(),
                    coordinates = coordinates
                )

                if (!response.isSuccessful) {

                    updateCityWeatherError(
                        city,
                        marker,
                        getWeatherApiErrorMessage(response)
                    )

                    return@launch
                }

                val weather = response.body()?.current

                if (weather == null) {

                    updateCityWeatherError(
                        city,
                        marker,
                        "No se recibió información del clima."
                    )

                    return@launch
                }

                val temperature = weather.temperatureC
                    ?.let {
                        "%.1f °C".format(Locale.US, it)
                    } ?: "No disponible"

                val condition = weather.condition?.text
                    ?: "No disponible"

                val humidity = weather.humidity
                    ?.let { "$it%" }
                    ?: "No disponible"

                val wind = weather.windKph
                    ?.let {
                        "%.1f km/h".format(Locale.US, it)
                    } ?: "No disponible"

                val iconUrl = weather.condition?.icon?.let {
                    "https:$it"
                }

                // Actualizar tarjeta de la ciudad.
                cityWeatherAdapter.updateWeather(
                    cityId = city.geonameId,
                    temperature = temperature,
                    condition = condition,
                    humidity = humidity,
                    wind = wind,
                    iconUrl = iconUrl
                )

                // Actualizar marcador del mapa.
                marker.snippet =
                    "Población: ${city.displayPopulation()}\n" +
                            "Departamento: ${city.department ?: "No disponible"}\n\n" +
                            "CLIMA ACTUAL\n" +
                            "Temperatura: $temperature\n" +
                            "Condición: $condition\n" +
                            "Humedad: $humidity\n" +
                            "Viento: $wind\n" +
                            "Actualizado: ${weather.lastUpdated ?: "No disponible"}"

                countryMap.invalidate()

            } catch (e: CancellationException) {

                throw e

            } catch (e: Throwable) {

                updateCityWeatherError(
                    city,
                    marker,
                    getNetworkErrorMessage(e)
                )
            }
        }
    }

    // Actualizar tarjeta y marcador cuando falla el clima
    private fun updateCityWeatherError(
        city: GeoCity,
        marker: Marker,
        errorMessage: String
    ) {

        cityWeatherAdapter.updateWeather(
            cityId = city.geonameId,
            temperature = "No disponible",
            condition = errorMessage,
            humidity = "No disponible",
            wind = "No disponible",
            iconUrl = null
        )

        marker.snippet =
            "Población: ${city.displayPopulation()}\n" +
                    "Departamento: ${city.department ?: "No disponible"}\n\n" +
                    errorMessage

        countryMap.invalidate()
    }

    // Centrar mapa en las coordenadas generales del país
    private fun showCountryCenter(coordinates: List<Double>?) {

        if (coordinates != null && coordinates.size >= 2) {

            countryMap.controller.setZoom(4.0)

            countryMap.controller.setCenter(
                GeoPoint(coordinates[0], coordinates[1])
            )

        } else {

            countryMap.controller.setZoom(2.0)

            countryMap.controller.setCenter(
                GeoPoint(0.0, 0.0)
            )
        }

        countryMap.invalidate()
    }

    override fun onResume() {
        super.onResume()

        if (::countryMap.isInitialized) {
            countryMap.onResume()
        }
    }

    override fun onPause() {

        if (::countryMap.isInitialized) {
            countryMap.onPause()
        }

        super.onPause()
    }


    override fun onDestroy() {

        if (::countryMap.isInitialized) {
            countryMap.onDetach()
        }

        super.onDestroy()
    }

}
