package sv.edu.udb.exploramundodesafio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import sv.edu.udb.exploramundodesafio.R
import sv.edu.udb.exploramundodesafio.model.Country
import android.content.Intent
import com.google.gson.Gson
import sv.edu.udb.exploramundodesafio.controller.CountryDetailActivity

class CountryAdapter(
    private val countries: List<Country>
) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CountryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_country, parent, false)

        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CountryViewHolder,
        position: Int
    ) {
        val country = countries[position]

        holder.nameTextView.text = country.displayName()
        holder.capitalTextView.text =
            "Capital: ${country.displayCapital()}"

        Glide.with(holder.itemView.context)
            .load(country.flagUrl())
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(holder.flagImageView)

        holder.detailsButton.setOnClickListener {
            val context = holder.itemView.context

            val intent = Intent(context, CountryDetailActivity::class.java)
            intent.putExtra(
                CountryDetailActivity.EXTRA_COUNTRY_JSON,
                Gson().toJson(country)
            )

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = countries.size

    class CountryViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val flagImageView: ImageView =
            itemView.findViewById(R.id.countryFlagImageView)

        val nameTextView: TextView =
            itemView.findViewById(R.id.countryNameTextView)

        val capitalTextView: TextView =
            itemView.findViewById(R.id.countryCapitalTextView)

        val detailsButton: Button =
            itemView.findViewById(R.id.countryDetailsButton)
    }
}
