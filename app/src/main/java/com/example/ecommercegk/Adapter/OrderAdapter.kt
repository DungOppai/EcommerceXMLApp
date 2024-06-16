package com.example.ecommercegk.Adapter

import android.annotation.SuppressLint
import android.content.Context
import com.example.ecommercegk.Model.OrderData
import com.example.ecommercegk.databinding.ViewholderOrderBinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommercegk.Model.ItemsModel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ValueEventListener

class OrderAdapter(
//    private val listItemSelected: ArrayList<String>,
    private val listItemInfo: ArrayList<OrderData>,
    context: Context,

    ) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderOrderBinding) : RecyclerView.ViewHolder(binding.root){
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ViewholderOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        if (position < listItemInfo.size) {
            val orderData = listItemInfo[position]

            holder.binding.dateTime.text = orderData.timestamp
            holder.binding.productName.text = orderData.items
            holder.binding.total.text = "-${orderData.totalPrice}$"
            holder.binding.totalQuantity.text= "Total: ${orderData.quantity}"
            holder.binding.address.text = orderData.address
            holder.binding.email.text = orderData.email
//        } else {
//            // Xử lý trường hợp position không hợp lệ
//            holder.binding.dateTime.text = ""
//            holder.binding.productName.text = ""
//            holder.binding.total.text = "orderData.totalPrice.toString()"
//        }

//        Glide.with(holder.itemView.context)
//            .load(item.picUrl[0])
//            .apply(RequestOptions().transform(CenterCrop()))
//            .into(holder.binding.pic)

    }
    override fun getItemCount(): Int {
        return listItemInfo.size
    }
}