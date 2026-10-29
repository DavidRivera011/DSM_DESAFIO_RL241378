package sv.edu.udb.exploramundodesafio.controller

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import sv.edu.udb.exploramundodesafio.R
import sv.edu.udb.exploramundodesafio.model.Country

class CountryDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_COUNTRY_JSON = "extra_country_json"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_detail)

        val backButton: Button = findViewById(R.id.backButton)
        val nameTextView: TextView = findViewById(R.id.detailCountryName)
        val flagImageView: ImageView = findViewById(R.id.detailCountryFlag)
        val officialNameTextView: TextView =
            findViewById(R.id.detailOfficialName)
        val capitalTextView: TextView = findViewById(R.id.detailCapital)
        val regionTextView: TextView = findViewById(R.id.detailRegion)
        val codesTextView: TextView = findViewById(R.id.detailCodes)

        backButton.setOnClickListener {
            finish()
        }

        val countryJson = intent.getStringExtra(EXTRA_COUNTRY_JSON)

        val country = countryJson?.let {
            runCatching {
                Gson().fromJson(it, Country::class.java)
            }.getOrNull()
        }

        if (country == null) {
            nameTextView.text = "No se pudo cargar el país"
            officialNameTextView.text =
                "La información recibida no es válida."
            return
        }

        nameTextView.text = country.displayName()

        officialNameTextView.text =
            "Nombre oficial: ${country.names?.official ?: "No disponible"}"

        capitalTextView.text =
            "Capital: ${country.displayCapital()}"

        regionTextView.text =
            "Región: ${country.region ?: "No disponible"}"

        codesTextView.text =
            "Códigos ISO: " +
                    "${country.codes?.alpha2 ?: "N/D"} / " +
                    "${country.codes?.alpha3 ?: "N/D"}"

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
}
