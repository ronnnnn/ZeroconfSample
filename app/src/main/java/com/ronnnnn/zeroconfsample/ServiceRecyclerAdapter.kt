package com.ronnnnn.zeroconfsample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ronnnnn.zeroconfsample.databinding.ItemServiceBinding

class ServiceRecyclerAdapter : RecyclerView.Adapter<ServiceRecyclerAdapter.ViewHolder>() {

    var itemList: List<String> = emptyList()
    var listener: OnItemClickListener? = null

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return DataBindingUtil.inflate<ItemServiceBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_service, parent,
            false
        ).let { ViewHolder(it) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(
        private val binding: ItemServiceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            with(binding) {
                serviceText.text = itemList[position]
                executePendingBindings()

                root.setOnClickListener { listener?.onItemClicked(position) }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int)
    }
}