package sv.edu.udb.exploramundodesafio.controller

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.Response
import sv.edu.udb.exploramundodesafio.R
import sv.edu.udb.exploramundodesafio.adapter.CountryAdapter
import sv.edu.udb.exploramundodesafio.model.CountriesResponse
import sv.edu.udb.exploramundodesafio.network.RestCountriesClient
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class CountriesActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_REGION_NAME = "region_name"
        const val EXTRA_REGION_API = "region_api"
        private const val TAG = "ExploraMundo"
    }

    private lateinit var titleTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var messageTextView: TextView
    private lateinit var countriesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countries)

        titleTextView = findViewById(R.id.countriesTitleTextView)
        progressBar = findViewById(R.id.countriesProgressBar)
        messageTextView = findViewById(R.id.countriesMessageTextView)
        countriesRecyclerView = findViewById(R.id.countriesRecyclerView)

        val regionName =
            intent.getStringExtra(EXTRA_REGION_NAME) ?: "Región"

        val regionApi =
            intent.getStringExtra(EXTRA_REGION_API) ?: "Americas"

        titleTextView.text = "Países de $regionName"

        countriesRecyclerView.layoutManager = LinearLayoutManager(this)

        loadCountries(regionApi)
    }

    private fun loadCountries(regionApi: String) {

        lifecycleScope.launch {

            progressBar.visibility = View.VISIBLE
            messageTextView.visibility = View.GONE
            countriesRecyclerView.visibility = View.GONE

            try {

                val response = RestCountriesClient.restCountriesService
                    .getCountriesByRegion(regionApi)

                if (response.isSuccessful) {

                    val countries = response.body()
                        ?.data
                        ?.objects
                        .orEmpty()
                        .sortedBy { it.displayName() }

                    if (countries.isEmpty()) {

                        showMessage(
                            "No hay países disponibles para esta región."
                        )

                    } else {

                        countriesRecyclerView.adapter =
                            CountryAdapter(countries)

                        countriesRecyclerView.visibility = View.VISIBLE
                    }

                } else {

                    val errorDetails =
                        response.errorBody()?.string().orEmpty()

                    Log.e(
                        TAG,
                        "Error HTTP ${response.code()}: $errorDetails"
                    )

                    showMessage(
                        when (response.code()) {

                            400 ->
                                "La solicitud enviada a REST Countries no es válida."

                            401 ->
                                "La API Key no es válida, ha expirado " +
                                        "o no se está enviando correctamente."

                            403 ->
                                "No tienes autorización para consultar esta información."

                            404 ->
                                "No se encontró información para esta región."

                            408 ->
                                "La solicitud tardó demasiado tiempo. Intenta nuevamente."

                            429 ->
                                "Se alcanzó el límite de peticiones. Intenta más tarde."

                            in 500..599 ->
                                "El servidor de REST Countries presenta problemas. " +
                                        "Intenta nuevamente más tarde."

                            else ->
                                "No se pudieron cargar los países. " +
                                        "Error HTTP ${response.code()}."
                        }
                    )
                }

            } catch (e: CancellationException) {

                // No debemos impedir la cancelación de la corrutina.
                throw e

            } catch (e: SocketTimeoutException) {

                Log.e(TAG, "Tiempo de espera agotado", e)
                showMessage("Se agotó el tiempo de espera. Intenta nuevamente.")

            } catch (e: UnknownHostException) {

                Log.e(TAG, "No se pudo contactar al servidor", e)
                showMessage(
                    "No hay conexión a Internet o no se pudo contactar al servidor."
                )

            } catch (e: IOException) {

                Log.e(TAG, "Error de comunicación con REST Countries", e)
                showMessage("Ocurrió un problema de comunicación con el servidor.")

            } catch (e: Exception) {

                Log.e(TAG, "Error inesperado al cargar países", e)
                showMessage("Ocurrió un error inesperado al cargar los países.")

            } finally {

                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showMessage(message: String) {
        messageTextView.text = message
        messageTextView.visibility = View.VISIBLE
        countriesRecyclerView.visibility = View.GONE
    }
}
