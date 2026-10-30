package sv.edu.udb.exploramundodesafio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sv.edu.udb.exploramundodesafio.R
import sv.edu.udb.exploramundodesafio.model.GeoCity

class CityAdapter(
    private val cities: List<GeoCity>
) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    class CityViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.cityName)
        val population: TextView =
            itemView.findViewById(R.id.cityPopulation)
        val department: TextView =
            itemView.findViewById(R.id.cityDepartment)
        val capital: TextView =
            itemView.findViewById(R.id.cityCapital)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_city, parent, false)

        return CityViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CityViewHolder,
        position: Int
    ) {
        val city = cities[position]

        holder.name.text = city.displayName()

        holder.population.text =
            "Población: ${city.displayPopulation()}"

        holder.department.text =
            "Departamento: ${city.department ?: "No disponible"}"

        if (city.isCapital()) {
            holder.capital.visibility = View.VISIBLE
            holder.capital.text = "★ CAPITAL"
        } else {
            holder.capital.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = cities.size
}
