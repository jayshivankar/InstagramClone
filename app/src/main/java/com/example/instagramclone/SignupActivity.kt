package com.example.instagramclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.instagramclone.Models.User
import com.example.instagramclone.Models.utils.USER_NODE
import com.example.instagramclone.Models.utils.USER_PROFILE_FOLDER
import com.example.instagramclone.Models.utils.upload
import com.example.instagramclone.databinding.ActivitySignupBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore


fun Firebase.getFirestoreInstance(): FirebaseFirestore {
    return FirebaseFirestore.getInstance()
}



private val Spannable.text: Any
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
            upload(uri, USER_PROFILE_FOLDER){
                if(it==null){

                }
                else{
                    user.image=it
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        user = User()

        binding.signUpBtn.setOnClickListener{
            if((binding.name.editableText?.text.toString() == "") or
                (binding.email.editableText?.text.toString() == "") or
                (binding.password.editableText?.text.toString() == "")
            )
                {
                Toast.makeText(this@SignupActivity,"PLease fill all the information",Toast.LENGTH_SHORT).show()
            }else{
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.email.editableText?.text.toString(),
                    binding.password.editableText?.text.toString()
                ).addOnCompleteListener{
                    result ->

                    if(result.isSuccessful){
                        user.name = binding.name.editableText?.text.toString()
                        user.email = binding.email.editableText?.text.toString()
                        user.password = binding.password.editableText?.text.toString()
                        Firebase.firestore.collection(USER_NODE)
                            .document(Firebase.auth.currentUser!!.uid).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this@SignupActivity,"LOGIN SUCCESSFUL",Toast.LENGTH_SHORT).show()
                            }

                    }
                    else{
                        Toast.makeText(this@SignupActivity,result.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
        binding.addImage.setOnClickListener {
            launcher.launch("image/*")
        }
    }
}