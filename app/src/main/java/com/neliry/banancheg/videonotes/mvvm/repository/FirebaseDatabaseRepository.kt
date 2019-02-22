package com.neliry.banancheg.videonotes.mvvm.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


abstract class FirebaseDatabaseRepository<Model>() {

    var databaseReference: DatabaseReference



    protected abstract fun getRootNode(): String

    init {
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child("1OlV0BFqhzNzSMVI0vmoZlTHwAJ2").child(getRootNode())
    }
}
