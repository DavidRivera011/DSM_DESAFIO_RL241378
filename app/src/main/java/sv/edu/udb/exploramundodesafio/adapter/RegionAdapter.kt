package sv.edu.udb.exploramundodesafio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sv.edu.udb.exploramundodesafio.R
import sv.edu.udb.exploramundodesafio.model.Region

class RegionAdapter(
    private val regions: List<Region>,
    private val onRegionClick: (Region) -> Unit
) : RecyclerView.Adapter<RegionAdapter.RegionViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RegionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_region, parent, false)

        return RegionViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RegionViewHolder,
        position: Int
    ) {
        val region = regions[position]

        holder.nameTextView.text = region.name
        holder.subtitleTextView.text =
            "Ver países · ${region.apiName}"

        holder.itemView.setOnClickListener {
            onRegionClick(region)
        }
    }

    override fun getItemCount(): Int = regions.size

    class RegionViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView =
            itemView.findViewById(R.id.regionNameTextView)

        val subtitleTextView: TextView =
            itemView.findViewById(R.id.regionSubtitleTextView)
    }
}
