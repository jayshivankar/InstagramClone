package com.example.instagramclone.Models.utils

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.FirebaseStorage.*
import java.util.UUID

fun upload(uri: Uri,folderName:String,callback:(String?)->Unit) {
    var imageUrl:String?=null
    getInstance().getReference(folderName).child(UUID.randomUUID().toString())
        .putFile(uri)
        .addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                imageUrl = it.toString()
                callback(imageUrl)
            }
        }


}