package com.example.ecommercegk.Adapter

import com.example.ecommercegk.Model.OrderData
import com.example.ecommercegk.databinding.ViewholderOrderBinding

import android.content.Context

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommercegk.Model.ItemsModel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class OrderAdapter(
    private val listItemSelected: ArrayList<OrderData>,
    context: Context,

) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    class ViewHolder(val binding: ViewholderOrderBinding) : RecyclerView.ViewHolder(binding.root){}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapter.ViewHolder {
        val binding =
            ViewholderOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItemSelected[position]
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!

//        holder.binding.dateTime.text = item.timestamp
//        holder.binding.dateTime.text = listItemSelected[position].toString()
//        holder.binding.total.text = listItemSelected[position].toString()

//        Glide.with(holder.itemView.context)
//            .load(item.picUrl[0])
//            .apply(RequestOptions().transform(CenterCrop()))
//            .into(holder.binding.pic)

    }
    override fun getItemCount(): Int = listItemSelected.size
}