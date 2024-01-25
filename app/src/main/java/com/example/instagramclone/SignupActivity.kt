package com.example.instagramclone

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.instagramclone.Models.User
import com.example.instagramclone.Models.utils.USER_NODE
import com.example.instagramclone.Models.utils.USER_PROFILE_FOLDER
import com.example.instagramclone.Models.utils.upload
import com.example.instagramclone.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


fun Firebase.getFirestoreInstance(): FirebaseFirestore {
    return FirebaseFirestore.getInstance()
}



val Spannable.text: Any
    get() {
        return toString()
    }

class SignupActivity : AppCompatActivity() {
    val binding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }
    lateinit var user: User
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()){
        uri->
        uri?.let {
            upload(uri, USER_PROFILE_FOLDER) {
                if (it == null) {

                } else {
                    user.image = it
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val text = "<font color=#FF000000>Already have an account</font> <font color=#1E88E5>Login?</font>"
        binding.Login.setText(Html.fromHtml(text))

        user = User()
        if (intent.hasExtra("MODE")){
            if (intent.getIntExtra("MODE",-1)==1){

                binding.signUpBtn.text="Update Profile"
                Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                        user = it.toObject<User>()!!
                        if (!user.image.isNullOrEmpty())
                        {
                            Picasso.get()
                                .load(user.image)
                                .into(binding.profileImage, object : Callback {
                                    override fun onSuccess() {
                                        

                                    }

                                    override fun onError(e: Exception?) {
                                        // Handle error loading image
                                        e?.printStackTrace()
                                    }
                                })

                        }
                        binding.name.setText(user.name)
                        binding.email.setText(user.email)
                        binding.password.setText(user.password)




                    }
            }
        }

        binding.signUpBtn.setOnClickListener{

            if (intent.hasExtra("MODE")){
                if(intent.getIntExtra("MODE",-1)==1){
                    Firebase.firestore.collection(USER_NODE)
                        .document(Firebase.auth.currentUser!!.uid).set(user)
                        .addOnSuccessListener {
                            startActivity(Intent(this@SignupActivity,HomeActivity::class.java))
                            finish()
                        }
                }
            }

            else {
                if ((binding.name.editableText?.text.toString() == "") or
                    (binding.email.editableText?.text.toString() == "") or
                    (binding.password.editableText?.text.toString() == "")
                ) {
                    Toast.makeText(
                        this@SignupActivity,
                        "PLease fill all the information",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        binding.email.editableText?.text.toString(),
                        binding.password.editableText?.text.toString()
                    ).addOnCompleteListener { result ->

                        if (result.isSuccessful) {
                            user.name = binding.name.editableText?.text.toString()
                            user.email = binding.email.editableText?.text.toString()
                            user.password = binding.password.editableText?.text.toString()
                            Firebase.firestore.collection(USER_NODE)
                                .document(Firebase.auth.currentUser!!.uid).set(user)
                                .addOnSuccessListener {
                                    startActivity(
                                        Intent(
                                            this@SignupActivity,
                                            HomeActivity::class.java
                                        )
                                    )
                                    finish()
                                }

                        } else {
                            Toast.makeText(
                                this@SignupActivity,
                                result.exception?.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
            }
        }
        binding.addImage.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.Login.setOnClickListener {
            startActivity(Intent(this@SignupActivity,LoginActivity::class.java))
            finish()
        }
    }
}