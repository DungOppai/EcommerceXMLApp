package com.example.ecommercegk.Model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommercegk.R

class ViewOrderItemsAdapter(
    private val orderList: ArrayList<OrderData>
) : RecyclerView.Adapter<ViewOrderItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewOrderItemsAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_view_order_items,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewOrderItemsAdapter.ViewHolder, position: Int) {
        val currentItem = orderList[position]
//
//        holder.itemOrder.text = currentItem.items.toString()
//        holder.quantityOrder.text = currentItem.quantity.toString()
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemOrder : TextView = itemView.findViewById(R.id.nameOrder)
        val quantityOrder : TextView = itemView.findViewById(R.id.quantity)
    }

}