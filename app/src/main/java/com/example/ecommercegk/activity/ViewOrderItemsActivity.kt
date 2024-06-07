package com.example.ecommercegk.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommercegk.Model.ItemsModel
import com.example.ecommercegk.Model.OrderData
import com.example.ecommercegk.Model.ViewOrderItemsAdapter
import com.example.ecommercegk.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewOrderItemsActivity : AppCompatActivity() {
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var orderRecyclerview: RecyclerView
    private lateinit var orderArraylist: ArrayList<OrderData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.viewholder_order)
        orderRecyclerview = findViewById(R.id.product_name)
        orderRecyclerview.layoutManager = LinearLayoutManager(this)
        orderRecyclerview.setHasFixedSize(true)
        orderArraylist = arrayListOf<OrderData>()
//        getOrderItem()

    }

    private fun getOrderItem() {
        firebaseUser = firebaseAuth.currentUser!!
        val userId = firebaseUser.uid
        firebaseRef = FirebaseDatabase.getInstance().getReference("Orders")
        firebaseRef.child(userId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(ItemsModel::class.java)
//                orderArraylist.add(order!!)
                orderRecyclerview.adapter = ViewOrderItemsAdapter(orderArraylist)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}