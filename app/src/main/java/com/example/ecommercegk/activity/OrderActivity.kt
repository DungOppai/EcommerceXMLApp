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
import com.example.ecommercegk.Model.OrderData
import com.example.ecommercegk.R
import com.example.ecommercegk.databinding.ActivityOrderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private val _itemCart = MutableLiveData<MutableList<String>>()
    private var _itemOrder = MutableLiveData<MutableList<OrderData>>()
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var orderArray: ArrayList<OrderData>

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
        orderArray = arrayListOf<OrderData>()
        // select orders user
        initOrder(userId.toString())
//        loadOrder(userId.toString())
        setVariable()
    }

private fun initOrder(userId:String) {
    binding.emptyTxt.visibility = View.VISIBLE
    _itemOrder.observe(this, Observer {listItemInfo ->
        binding.viewOrder.layoutManager = LinearLayoutManager(this@OrderActivity)
        binding.viewOrder.adapter = OrderAdapter(ArrayList(listItemInfo), context = this)
        binding.emptyTxt.visibility = if (listItemInfo.isEmpty()) View.VISIBLE else View.GONE
    })
//    _itemOrder.observe(this, Observer { listItemInfo ->
//        binding.viewOrder.layoutManager = LinearLayoutManager(this@OrderActivity)
//        binding.viewOrder.adapter = OrderAdapter(
////                ArrayList(listItemSelected),
//            ArrayList(listItemInfo),
//            context = this@
//        )
//        binding.emptyTxt.visibility = if (listItemSelected.isEmpty()) View.VISIBLE else View.GONE
//    })
//    _itemCart.observe(this, Observer { listItemSelected ->
//
//    })

    loadOrder(userId)
}
    private fun loadOrder(userId: String) {
        val lists = ArrayList<OrderData>()
        val orderId = mutableListOf<String>()
        firebaseRef = FirebaseDatabase.getInstance().getReference("Orders")
                    firebaseRef.child(userId).addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (childSnapshot in snapshot.children) {
                                if(snapshot.exists()){
//                                    for (userSnapshot in snapshot.children) {
                                        val order =childSnapshot.getValue(OrderData::class.java)
                                        orderArray.add(order!!)
                                        Log.d("Order info", "$orderArray")
//                                    }
                                    _itemOrder.value= orderArray
//                                    binding.viewOrder.adapter = OrderAdapter(orderArray)
//                                    binding.emptyTxt.visibility = if (orderArray.isEmpty()) View.VISIBLE else View.GONE
                                }
//                                initOrder()


//                                val id =childSnapshot.key!!
//                                orderId.add(id)
////                                Log.d("ORDER","orderID: $orderId")
//                                Log.d("USER","userID: $userId, $id")
//                               getOrderDetail(userId,id)
//                            _itemCart.value = orderId
                            }
                        }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun getOrderDetail(userId: String, orderId: String){
        firebaseRef = FirebaseDatabase.getInstance().getReference("Orders")
        firebaseRef.child(userId).child(orderId).get().addOnSuccessListener {
            val lists = ArrayList<OrderData>()
            try {
                val orderData = it.getValue(OrderData::class.java)
                if(orderData != null){
                    lists.add(orderData)
                }
//                orderData?.let {
//                    lists.add(it)
//                    Log.d("Order Info", "Items: ${it.items}, TotalPrice: ${it.totalPrice}, Quantity: ${it.quantity}, Timestamp: ${it.timestamp}")
//                }
            } catch (e: DatabaseException) {
                Log.e("Firebase Error", "Error converting data", e)
//                Log.e("Firebase Error", "Raw data: ${orderSnapshot.value}")
            }
                _itemOrder.value = lists
        }.addOnFailureListener { error ->
            Log.e("Firebase ErrorGet", "Error getting data", error)
        }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }
    }
}