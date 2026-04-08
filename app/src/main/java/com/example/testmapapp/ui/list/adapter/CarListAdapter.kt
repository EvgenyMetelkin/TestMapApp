package com.example.testmapapp.ui.list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.testmapapp.R
import com.example.testmapapp.domain.CarGroup
import com.example.testmapapp.domain.CarInformation

class CarListAdapter(
    private val onCarClick: (CarInformation) -> Unit = {}
) : ListAdapter<CarGroup, RecyclerView.ViewHolder>(DiffCallback) {

    companion object {
        private const val VIEW_TYPE_GROUP = 0
        private const val VIEW_TYPE_CAR = 1

        private val DiffCallback = object : DiffUtil.ItemCallback<CarGroup>() {
            override fun areItemsTheSame(oldItem: CarGroup, newItem: CarGroup): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: CarGroup, newItem: CarGroup): Boolean =
                oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is CarGroup.Group -> VIEW_TYPE_GROUP
        is CarInformation -> VIEW_TYPE_CAR
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_GROUP -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_car_group, parent, false
                )
                GroupViewHolder(view)
            }

            else -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_car_info, parent, false
                )
                CarViewHolder(view, onCarClick)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is CarGroup.Group -> (holder as GroupViewHolder).bind(item)
            is CarInformation -> (holder as CarViewHolder).bind(item)
        }
    }

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.groupTitle)
        fun bind(group: CarGroup.Group) {
            titleView.text = group.title
        }
    }

    class CarViewHolder(
        itemView: View,
        private val onClick: (CarInformation) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val carTitleView: TextView = itemView.findViewById(R.id.carTitle)
        private var currentCar: CarInformation? = null

        init {
            itemView.setOnClickListener {
                currentCar?.let { car -> onClick(car) }
            }
        }

        fun bind(car: CarInformation) {
            currentCar = car
            carTitleView.text = car.title
        }
    }
}