package com.example.ecommercegk.Helper

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.ecommercegk.Model.ItemsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class ManagementCart(val context: Context) {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser



    private val tinyDB = TinyDB(context)

    fun insertFood(item: ItemsModel) {
        var listFood = getListCart()
        val existAlready = listFood.any { it.title == item.title }
        val index = listFood.indexOfFirst { it.title == item.title }

        if (existAlready) {
            listFood[index].numberInCart = item.numberInCart
            Toast.makeText(context,"This already in your cart",Toast.LENGTH_SHORT).show()
        } else {

            listFood.add(item)
        }
        tinyDB.putListObject("CartList", listFood)
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show()
    }

    fun getListCart(): ArrayList<ItemsModel> {
        return tinyDB.getListObject("CartList") ?: arrayListOf()
    }

    fun minusItem(
        firebaseUser: FirebaseUser,
        listFood: ArrayList<ItemsModel>,
        position: Int,
        listener: ChangeNumberItemsListener
    ) {
        var indexItem = position
        if (listFood[position].numberInCart == 1) {
            listFood.removeAt(position)
//            deleteCart(indexItem)

        } else {

            listFood[position].numberInCart--
            Log.d("TAG","NumberInCart: ${listFood[position].numberInCart}")
            getCart(listFood[position].numberInCart,firebaseUser, indexItem)


        }
        tinyDB.putListObject("CartList", listFood)
        listener.onChanged()
    }

    fun plusItem(listFood: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener, firebaseUser: FirebaseUser) {
        var indexItem = position
        listFood[position].numberInCart++
        tinyDB.putListObject("CartList", listFood)
        listener.onChanged()
        getCart(listFood[position].numberInCart,firebaseUser, indexItem )
    }

    fun getTotalFee(): Double {
        val listFood = getListCart()
        var fee = 0.0
        for (item in listFood) {
            fee += item.price * item.numberInCart
        }
        return fee
    }

    private fun updateQuantity(cartId: String, numberInCart: Int, index: Int){
        firebaseRef = FirebaseDatabase.getInstance().getReference("Carts")
        val cart = mapOf<String,String>(
            "numberInCart" to numberInCart.toString()
        )
        firebaseRef.child(cartId).child(index.toString()).updateChildren(cart).addOnSuccessListener{
            Log.d("TAG","update success cartID: $cartId quantity: $cart")
        }
    }
    private fun getCart(numberInCart: Int,firebaseUser: FirebaseUser,index: Int) {
        firebaseRef = FirebaseDatabase.getInstance().getReference("Users")
        firebaseAuth = FirebaseAuth.getInstance()
        this.firebaseUser = firebaseAuth.currentUser!!
        val userId = firebaseUser.uid
        firebaseRef.child(userId).get().addOnSuccessListener {
            val cartId = it.child("cartId").value.toString()
            Log.d("TAG","get cartId success!!")
            updateQuantity(cartId,numberInCart, index)

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }
    private fun deleteCart(index: Int){
        firebaseRef = FirebaseDatabase.getInstance().getReference("Users")
        val userId = firebaseUser.uid
        firebaseRef.child(userId).get().addOnSuccessListener {
            val cartId = it.child("cartId").value.toString()
            Log.d("TAG","get cartId in deleteCart success!!")
            firebaseRef = FirebaseDatabase.getInstance().getReference("Carts").child(cartId).child(index.toString())
            val deleteItem = firebaseRef.removeValue()
            deleteItem
                .addOnSuccessListener{
                    Log.d("TAG","delete item in cart $userId success!!")
                }
                .addOnFailureListener{
                    Log.e("firebase", "Error delete item in cart", it)
                }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }


    }



}