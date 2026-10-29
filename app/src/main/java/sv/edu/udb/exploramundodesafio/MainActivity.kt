package sv.edu.udb.exploramundodesafio

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sv.edu.udb.exploramundodesafio.adapter.RegionAdapter
import sv.edu.udb.exploramundodesafio.model.Region
import android.content.Intent
import sv.edu.udb.exploramundodesafio.controller.CountriesActivity


class MainActivity : AppCompatActivity() {

    private lateinit var regionsRecyclerView: RecyclerView
    private lateinit var regionAdapter: RegionAdapter

    private val regions = listOf(
        Region("África", "Africa"),
        Region("América", "Americas"),
        Region("Antártida", "Antarctic"),
        Region("Asia", "Asia"),
        Region("Europa", "Europe"),
        Region("Oceanía", "Oceania")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        regionsRecyclerView =
            findViewById(R.id.regionsRecyclerView)

        regionAdapter = RegionAdapter(regions) { region ->
            val intent = Intent(this, CountriesActivity::class.java).apply {
                putExtra(
                    CountriesActivity.EXTRA_REGION_NAME,
                    region.name
                )
                putExtra(
                    CountriesActivity.EXTRA_REGION_API,
                    region.apiName
                )
            }

            startActivity(intent)
        }

        regionsRecyclerView.layoutManager =
            LinearLayoutManager(this)

        regionsRecyclerView.adapter = regionAdapter
    }
}