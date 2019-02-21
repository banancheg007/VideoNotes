package com.neliry.banancheg.videonotes.mvvm.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.neliry.banancheg.videonotes.mvvm.mapper.FirebaseMapper

class BaseValueEventListener<Model, Entity>(
    private val mapper: FirebaseMapper<Entity, Model>,
    private val callback: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Model>
) : ValueEventListener {

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        val data = mapper.mapList(dataSnapshot)
        callback.onSuccess(data)
    }

    override fun onCancelled(databaseError: DatabaseError) {
        callback.onError(databaseError.toException())
    }
}
