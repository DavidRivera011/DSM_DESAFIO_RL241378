package sv.edu.udb.exploramundodesafio.controller

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sv.edu.udb.exploramundodesafio.R
import sv.edu.udb.exploramundodesafio.adapter.CountryAdapter
import sv.edu.udb.exploramundodesafio.model.CountriesResponse
import sv.edu.udb.exploramundodesafio.network.RetrofitClient

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
        progressBar.visibility = View.VISIBLE
        messageTextView.visibility = View.GONE
        countriesRecyclerView.visibility = View.GONE

        RetrofitClient.restCountriesService
            .getCountriesByRegion(regionApi)
            .enqueue(object : Callback<CountriesResponse> {

                override fun onResponse(
                    call: Call<CountriesResponse>,
                    response: Response<CountriesResponse>
                ) {
                    progressBar.visibility = View.GONE

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
                                401 -> "La API key no es válida, " +
                                        "ha expirado o no se está enviando."
                                403 -> "La API key no tiene permiso " +
                                        "para realizar esta petición."
                                429 -> "Se alcanzó el límite de peticiones " +
                                        "de la API. Intenta más tarde."
                                else -> "No se pudieron cargar los países. " +
                                        "Error HTTP ${response.code()}."
                            }
                        )
                    }
                }

                override fun onFailure(
                    call: Call<CountriesResponse>,
                    t: Throwable
                ) {
                    progressBar.visibility = View.GONE

                    Log.e(TAG, "Error de conexión con REST Countries", t)

                    showMessage(
                        "Error de conexión: " +
                                (t.localizedMessage ?: "Error desconocido")
                    )
                }
            })
    }

    private fun showMessage(message: String) {
        messageTextView.text = message
        messageTextView.visibility = View.VISIBLE
        countriesRecyclerView.visibility = View.GONE
    }
}
