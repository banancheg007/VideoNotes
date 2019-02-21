package com.neliry.banancheg.videonotes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

abstract class FirebaseRepository(){
    //private lateinit var auth: FirebaseAuth
    //val currentUser = auth.currentUser

    lateinit var databaseReference: DatabaseReference


    protected abstract fun getRootNode(): String

    init{
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child("1OlV0BFqhzNzSMVI0vmoZlTHwAJ2").child(getRootNode())
    }
}
