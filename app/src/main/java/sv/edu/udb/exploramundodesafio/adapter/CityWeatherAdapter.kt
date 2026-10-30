package sv.edu.udb.exploramundodesafio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import sv.edu.udb.exploramundodesafio.R
import sv.edu.udb.exploramundodesafio.model.CityWeather

class CityWeatherAdapter(
    private val cities: MutableList<CityWeather>,
    private val onLocateCity: (CityWeather) -> Unit
) : RecyclerView.Adapter<CityWeatherAdapter.CityWeatherViewHolder>() {

    class CityWeatherViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val cityName: TextView =
            itemView.findViewById(R.id.cityWeatherName)

        val weatherIcon: ImageView =
            itemView.findViewById(R.id.cityWeatherIcon)

        val temperature: TextView =
            itemView.findViewById(R.id.cityWeatherTemperature)

        val condition: TextView =
            itemView.findViewById(R.id.cityWeatherCondition)

        val humidity: TextView =
            itemView.findViewById(R.id.cityWeatherHumidity)

        val wind: TextView =
            itemView.findViewById(R.id.cityWeatherWind)

        val locateButton: Button =
            itemView.findViewById(R.id.btnLocateCity)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityWeatherViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_city_weather, parent, false)

        return CityWeatherViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CityWeatherViewHolder,
        position: Int
    ) {

        val cityWeather = cities[position]

        val capitalLabel =
            if (cityWeather.city.isCapital()) " ★ Capital" else ""

        holder.cityName.text =
            "${cityWeather.city.displayName()}$capitalLabel"

        holder.temperature.text = cityWeather.temperature
        holder.condition.text = cityWeather.condition
        holder.humidity.text = "Humedad: ${cityWeather.humidity}"
        holder.wind.text = "Viento: ${cityWeather.wind}"

        if (!cityWeather.iconUrl.isNullOrBlank()) {

            Glide.with(holder.itemView.context)
                .load(cityWeather.iconUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.weatherIcon)

        } else {

            Glide.with(holder.itemView.context)
                .clear(holder.weatherIcon)

            holder.weatherIcon.setImageResource(
                android.R.drawable.ic_menu_gallery
            )
        }

        holder.locateButton.setOnClickListener {
            onLocateCity(cityWeather)
        }
    }

    override fun getItemCount(): Int = cities.size

    fun updateWeather(
        cityId: Long?,
        temperature: String,
        condition: String,
        humidity: String,
        wind: String,
        iconUrl: String?
    ) {

        if (cityId == null) return

        val index = cities.indexOfFirst {
            it.city.geonameId == cityId
        }

        if (index != -1) {

            cities[index].temperature = temperature
            cities[index].condition = condition
            cities[index].humidity = humidity
            cities[index].wind = wind
            cities[index].iconUrl = iconUrl
            cities[index].isLoading = false

            notifyItemChanged(index)
        }
    }

    fun setCities(newCities: List<CityWeather>) {

        cities.clear()
        cities.addAll(newCities)

        notifyDataSetChanged()
    }
}
