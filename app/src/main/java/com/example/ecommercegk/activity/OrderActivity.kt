package com.example.ecommercegk.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommercegk.Adapter.CartAdapter
import com.example.ecommercegk.Adapter.OrderAdapter
import com.example.ecommercegk.Helper.ChangeNumberItemsListener
import com.example.ecommercegk.Model.ItemsModel
import com.example.ecommercegk.Model.OrderData
import com.example.ecommercegk.R
import com.example.ecommercegk.databinding.ActivityCartBinding
import com.example.ecommercegk.databinding.ActivityOrderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private val _itemCart = MutableLiveData<MutableList<OrderData>>()
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId = intent.getStringExtra("id")
        val OrderUserIds = mutableListOf<String>()
        // select orders user

//        firebaseRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (childSnapshot in snapshot.children) {
//                    if (userId?.let { childSnapshot.child(it).exists() } == true) {
//                        followingUserIds.add(childSnapshot.key!!)
//                    }
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                // Handle the error
//            }
//        })
        initOrder(userId.toString())
        setVariable()

    }
private fun initOrder(userId: String) {
    binding.emptyTxt.visibility = View.VISIBLE
    _itemCart.observe(this, Observer {listItemSelected ->
        binding.viewOrder.layoutManager = LinearLayoutManager(this@OrderActivity)
        binding.viewOrder.adapter = OrderAdapter(ArrayList(listItemSelected), context = this)
        binding.emptyTxt.visibility = if (listItemSelected.isEmpty()) View.VISIBLE else View.GONE
    })
    loadOrder(userId)
}
    private fun loadOrder(userId: String) {
        val lists = ArrayList<OrderData>()
        firebaseRef = FirebaseDatabase.getInstance().getReference("Orders")
        firebaseRef.child(userId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.value
                    list?.let {
                        lists.add(it as OrderData)
                    }
                }
                _itemCart.value = lists
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
//        firebaseRef = FirebaseDatabase.getInstance().getReference("Orders/$userId")
//        firebaseRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val lists = ArrayList<OrderData>()
//                for (childSnapshot in snapshot.children) {
//                    // Manually parse the data
//                    val map = childSnapshot.value as? Map<*, *>
//                    if (map != null) {
//                        val order = childSnapshot.getValue(OrderData::class.java)
//                        val items = map.values.filterIsInstance<String>()
//                        val totalPrice = items.lastOrNull()?.toDoubleOrNull() ?: 0.0
//                        lists.add(OrderData(userId,or,items.dropLast(1), totalPrice, childSnapshot.key ?: ""))
//                        lists.add(order!!)
//                    } else {
//                        // Handle the array case if necessary
//                    }
//                }
//                _itemCart.value = lists
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle the error
//            }
//        })
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }
    }
}