package com.example.ecommercegk.activity

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ecommercegk.Helper.ManagementCart
import com.example.ecommercegk.Model.ItemsModel
import com.example.ecommercegk.R
import com.example.ecommercegk.databinding.ActivityCartBinding
import com.example.ecommercegk.databinding.ActivityProfileBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var managementCart: ManagementCart
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var editProfilePopup: Dialog

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


        editProfilePopup = Dialog(this)
        editProfilePopup.setContentView(R.layout.change_profile_popup)

        binding.editProfileBtn.setOnClickListener {
            showEditProfilePopup()
        }


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
                    val address = it.child("address").value
                    binding.userAddress.text = address.toString()
                }
            }.addOnFailureListener {
                Log.d("TAG", "Error: ${it.message}")
            }
        }

    }


    private fun showEditProfilePopup() {
        val nameEditText = editProfilePopup.findViewById<TextInputEditText>(R.id.changeName)
        val emailEditText = editProfilePopup.findViewById<TextInputEditText>(R.id.changeEmail)
        val addressEditText = editProfilePopup.findViewById<TextInputEditText>(R.id.changeAddress)

        // Populate the EditText fields with the current user data
        nameEditText.setText(binding.userName.text.toString())
        emailEditText.setText(binding.userEmail.text.toString())
        addressEditText.setText(binding.userAddress.text.toString())

        val cancelButton = editProfilePopup.findViewById<Button>(R.id.backBtn)
        val saveButton = editProfilePopup.findViewById<Button>(R.id.confirmBtn)

        cancelButton.setOnClickListener {
            editProfilePopup.dismiss()
        }

        saveButton.setOnClickListener {
            // Save the updated profile information to the database
            updateUserProfile(
                nameEditText.text.toString(),
                emailEditText.text.toString(),
                addressEditText.text.toString()
            )
            editProfilePopup.dismiss()
        }

        editProfilePopup.show()
    }

    private fun updateUserProfile(name: String, email: String, address: String) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val userMap = mapOf(
                "userName" to name,
                "email" to email,
                "address" to address
            )
            firebaseRef.child(userId).updateChildren(userMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
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
