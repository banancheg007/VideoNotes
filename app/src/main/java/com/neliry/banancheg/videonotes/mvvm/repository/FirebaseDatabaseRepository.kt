package com.neliry.banancheg.videonotes.mvvm.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.neliry.banancheg.videonotes.mvvm.mapper.FirebaseMapper

abstract class FirebaseDatabaseRepository<Model>(private val mapper: FirebaseMapper<*, *>) {

    protected var databaseReference: DatabaseReference
    protected var firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>
    private var listener: BaseValueEventListener<*, *>? = null


    protected abstract val rootNode: String

    init {
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child("1OlV0BFqhzNzSMVI0vmoZlTHwAJ2").child(rootNode)
    }

    fun addListener(firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>) {
        this.firebaseCallback = firebaseCallback
        listener = BaseValueEventListener(mapper, firebaseCallback)
        databaseReference.addValueEventListener(listener!!)
    }

    fun removeListener() {
        databaseReference.removeEventListener(listener!!)
    }

    interface FirebaseDatabaseRepositoryCallback<T> {
        fun onSuccess(result: List<T>)

        fun onError(e: Exception)
    }
}
