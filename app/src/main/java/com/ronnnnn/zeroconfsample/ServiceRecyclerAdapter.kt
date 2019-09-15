package com.ronnnnn.zeroconfsample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ronnnnn.zeroconfsample.databinding.ItemServiceBinding

class ServiceRecyclerAdapter :
    ListAdapter<String, ServiceRecyclerAdapter.ViewHolder>(DIFF_CALLBACK) {

    var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return DataBindingUtil.inflate<ItemServiceBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_service, parent,
            false
        ).let { ViewHolder(it) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            serviceText.text = getItem(position)
            executePendingBindings()

            root.setOnClickListener { listener?.onItemClicked(position) }
        }
    }

    class ViewHolder(val binding: ItemServiceBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener {
        fun onItemClicked(position: Int)
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<String> =
            object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem == newItem
                }
            }
    }
}