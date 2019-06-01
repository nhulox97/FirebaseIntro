package com.nhulox.dosv.firebaseintro.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nhulox.dosv.firebaseintro.R
import com.nhulox.dosv.firebaseintro.models.User
import java.io.IOException
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var fireauth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var btnChoose: Button
    private lateinit var btnUpload: Button
    private lateinit var imageView: ImageView

    private var filePath: Uri? = null

    private val PICK_IMAGE_REQUEST = 71

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialize FireBase components
        fireauth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference



        //Initialize layout components
        btnChoose = findViewById(R.id.btnChoose)
        btnUpload = findViewById(R.id.btnUpload)
        imageView = findViewById(R.id.imgView)

        btnChoose.setOnClickListener { chooseImage() }
        btnUpload.setOnClickListener { uploadImage() }

        //registerUser("Sergio", "sergiobernal909@gmail.com", "123456")
        //loginUser("sergiobernal909@gmail.com", "123456")
    }

    private fun chooseImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null){
            filePath = data.data
            try{
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imageView.setImageBitmap(bitmap)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(){
        if (filePath != null){
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            val reference = storageReference.child("test/ ${UUID.randomUUID()}")
            reference.putFile(filePath!!)
                .addOnSuccessListener { _ ->
                    progressDialog.dismiss()
                    Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { task ->
                    val progress = 100.0*task.bytesTransferred/task.totalByteCount
                    progressDialog.setMessage("Uploaded $progress%")
                }

        }

    }

    private fun registerUser(displayName:String, email: String, password: String){
        fireauth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val currentUserUid = fireauth.currentUser!!.uid
                    val user = User(currentUserUid, email, displayName).toMap()

                    firestore.collection("users").document("$currentUserUid").set(user)
                        .addOnSuccessListener{ _ ->
                            Toast.makeText(this, "Everything is ok!", Toast.LENGTH_LONG).show()
                        }
                }else
                    Toast.makeText(this, "Something's wrong :(", Toast.LENGTH_LONG).show()
            }
    }

    private fun loginUser(email: String, password: String){

    }

}
