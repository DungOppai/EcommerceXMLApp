package com.example.ecommercegk.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText

import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommercegk.Adapter.CartAdapter
import com.example.ecommercegk.Helper.ChangeNumberItemsListener
import com.example.ecommercegk.Helper.ManagementCart
import com.example.ecommercegk.Model.ItemsModel
import com.example.ecommercegk.R
import com.example.ecommercegk.databinding.ActivityCartBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartActivity : BaseActivity() {
    private val _itemCart = MutableLiveData<MutableList<ItemsModel>>()
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var binding: ActivityCartBinding
    private lateinit var managementCart: ManagementCart
    private var tax: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        managementCart = ManagementCart(this)


        firebaseRef = FirebaseDatabase.getInstance().getReference("Users")
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        val userId = firebaseUser.uid

        firebaseRef.child(userId).get().addOnSuccessListener {
            val cartId = it.child("cartId").value.toString()
            Log.d("TAG","get cartId in cartActivity $cartId success!!")
            setVariable()
            initCart(cartId)
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
        calculateCart()
        // checkout
        binding.button.setOnClickListener {
            showBottomSheet(userId)

        }

    }


    fun showBottomSheet(userId: String){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet,null)

        val btnClose = view.findViewById<ImageView>(R.id.backBtnCart)
        val totalOrder = view.findViewById<TextView>(R.id.total)
        val contact = view.findViewById<TextView>(R.id.contact)
        val ship = view.findViewById<TextView>(R.id.text_input)
        val btnChangeContact = view.findViewById<TextView>(R.id.changeBtn1)
        val btnChangeAddress = view.findViewById<TextView>(R.id.changeBtn2)
        //caculator
        val percentTax = 0.02
        val delivery = 10.0
        tax = Math.round((managementCart.getTotalFee() * percentTax) * 100) / 100.0
        val total = Math.round((managementCart.getTotalFee() + tax + delivery) * 100) / 100
        totalOrder.text = "$$total"
        //close bottom_sheet
        btnClose.setOnClickListener{
            dialog.dismiss()
        }
        btnChangeAddress.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("id",userId)
            startActivity(intent)

        }
        btnChangeContact.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("id",userId)
            startActivity(intent)
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()

        // get contact user
        firebaseRef = FirebaseDatabase.getInstance().getReference("Users")
        firebaseRef.child(userId).get().addOnSuccessListener {
            if (it.exists()) {
                val email = it.child("email").value
                contact.text = email.toString()
                val address = it.child("address").value
                ship.text = address.toString()
            }
            Log.d("TAG","get contact in bottom_sheet $contact success!!")

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

    }
    private fun initCartList() {
        binding.viewCart.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.viewCart.adapter =
            CartAdapter(managementCart.getListCart(), context = this ,object : ChangeNumberItemsListener {
                override fun onChanged() {
                    calculateCart()
                }
            })

        with(binding) {
            emptyTxt.visibility =
                if (managementCart.getListCart().isEmpty()) View.VISIBLE else View.GONE
            scrollView2.visibility =
                if (managementCart.getListCart().isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun calculateCart() {
        val percentTax = 0.02
        val delivery = 10.0
        tax = Math.round((managementCart.getTotalFee() * percentTax) * 100) / 100.0
        val total = Math.round((managementCart.getTotalFee() + tax + delivery) * 100) / 100
        val itemTotal = Math.round(managementCart.getTotalFee() * 100) / 100

        with(binding) {
            totalFeeTxt.text = "$$itemTotal"
            taxTxt.text = "$$tax"
            deliveryTxt.text = "$$delivery"
            totalTxt.text = "$$total"
        }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }

    }

    private fun initCart(cartId: String) {
        binding.emptyTxt.visibility = View.VISIBLE
        _itemCart.observe(this, Observer {listItemSelected ->
            binding.viewCart.layoutManager = LinearLayoutManager(this@CartActivity)
            binding.viewCart.adapter = CartAdapter(ArrayList(listItemSelected), context = this ,object : ChangeNumberItemsListener {
                override fun onChanged() {
                    calculateCart()
                }
            })

            binding.emptyTxt.visibility = if (listItemSelected.isEmpty()) View.VISIBLE else View.GONE
        })
        loadCart(cartId)

    }
    fun loadCart(cartId:String) {
        firebaseRef = FirebaseDatabase.getInstance().getReference("Carts/${cartId}")
        val ref = firebaseRef
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = ArrayList<ItemsModel>()

                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(ItemsModel::class.java)
                    list?.let {
                        lists.add(it)
                        managementCart.insertFoodArray(it)
                    }
                }
                _itemCart.value = lists
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


}