package com.example.ecommercegk.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ecommercegk.Helper.ManagementCart
import com.example.ecommercegk.Model.ItemsModel
import com.example.ecommercegk.R
import com.example.ecommercegk.databinding.ActivityCartBinding
import com.example.ecommercegk.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var managementCart: ManagementCart
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseRef = FirebaseDatabase.getInstance().getReference("Users")

        managementCart = ManagementCart(this)
        setVariable()
        firebaseAuth = FirebaseAuth.getInstance()
        binding.logoutBtn.setOnClickListener {
            logout()
        }

        //Load UserInfo
        val userId = intent.getStringExtra("id")
        if (userId != null) {
            firebaseRef.child(userId).get().addOnSuccessListener {
                if (it.exists()) {
                    val name = it.child("userName").value
                    binding.userName.text = name.toString()
                    val email = it.child("email").value
                    binding.userEmail.text = email.toString()
                }
            }.addOnFailureListener {
                Log.d("TAG", "Error: ${it.message}")
            }
        }

    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }

    }

    private fun logout() {
        managementCart.clearCart()
        firebaseAuth.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        // Optionally, you can display a toast or message to inform the user that they have been logged out.
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}
