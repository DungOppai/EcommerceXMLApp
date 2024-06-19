package com.example.ecommercegk.activity

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.transition.Transition
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.ecommercegk.Helper.ManagementCart
import com.example.ecommercegk.Model.ItemsModel
import com.example.ecommercegk.Model.UserData
import com.example.ecommercegk.R
import com.example.ecommercegk.ViewModel.MainViewModel
import com.example.ecommercegk.databinding.ActivityCartBinding
import com.example.ecommercegk.databinding.ActivityProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var managementCart: ManagementCart
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var editProfilePopup: Dialog
    private lateinit var storageReference: StorageReference
    private lateinit var imageUri: Uri
    private lateinit var userImage: ImageView
    private lateinit var changeImg: FloatingActionButton
    private val _userName = MutableLiveData<MutableList<UserData>>()
    private val viewModel = MainViewModel()
    private var uploadedBitmap: Bitmap? = null

    val username: LiveData<MutableList<UserData>> = _userName

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


        val userId = intent.getStringExtra("id")
        if (userId != null) {
            viewModel.loadUser(userId)
            viewModel.username.observe(this, Observer { userDataList ->

                userDataList?.let {

                    binding.userName.text = it[0].userName ?: "No user name"
                    binding.userEmail.text = it[0].email ?: "No email"
                    binding.userAddress.text = it[0].address ?: "No address"

                    Glide.with(this)
                        .asBitmap()
                        .load(it[0].uImage)
                        .into(object : com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                                val cornerRadius = 64f // Set the desired corner radius
                                val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, resource)
                                roundedBitmapDrawable.cornerRadius = cornerRadius
                                binding.userImage.setImageDrawable(roundedBitmapDrawable)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })
                }
            })
        }

        userImage = findViewById(R.id.userImage)
        changeImg = findViewById(R.id.floatingActionButton)

        changeImg.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(
                    1080,
                    1080
                )
                .start()
        }
        //uploadProfileImage()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data

            uploadImageToFirebase(fileUri)
        }
        userImage.setImageURI(data?.data)
    }


    private fun uploadImageToFirebase(fileUri: Uri?) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {

            Toast.makeText(this, "Please sign in to upload an image", Toast.LENGTH_SHORT).show()
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference.child("images/${System.currentTimeMillis()}.jpg")

        currentUser.getIdToken(true)
            .addOnSuccessListener { task ->
                uploadFileToStorage(fileUri, storageRef)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "You do not have permission to upload files: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadFileToStorage(fileUri: Uri?, storageRef: StorageReference) {
        val uploadTask = storageRef.putFile(fileUri!!)

        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            println("Upload is $progress% done")
        }.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                saveImageUrlToRealtime(imageUrl)
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error uploading image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageUrlToRealtime(imageUrl: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(currentUser?.uid!!)

        userRef.child("uImage").setValue(imageUrl)
            .addOnSuccessListener {
                println("Image URL saved to Realtime Database successfully")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving image URL to Realtime Database: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEditProfilePopup() {
        val nameEditText = editProfilePopup.findViewById<TextInputEditText>(R.id.changeName)
        val emailEditText = editProfilePopup.findViewById<TextInputEditText>(R.id.changeEmail)
        val addressEditText = editProfilePopup.findViewById<TextInputEditText>(R.id.changeAddress)

        nameEditText.setText(binding.userName.text.toString())
        emailEditText.setText(binding.userEmail.text.toString())
        addressEditText.setText(binding.userAddress.text.toString())

        val cancelButton = editProfilePopup.findViewById<Button>(R.id.backBtn)
        val saveButton = editProfilePopup.findViewById<Button>(R.id.confirmBtn)

        cancelButton.setOnClickListener {
            editProfilePopup.dismiss()
        }

        saveButton.setOnClickListener {

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
                    Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
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

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}
