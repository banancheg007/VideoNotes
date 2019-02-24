package com.neliry.banancheg.videonotes.repositories

import com.google.firebase.database.*
import java.lang.Exception
import java.lang.reflect.ParameterizedType


abstract class FirebaseDatabaseRepository<Model> {
    var list = ArrayList<Model>()
    var databaseReference: DatabaseReference
    private var listener: ValueEventListener? = null

    protected lateinit var firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>

    protected abstract fun getRootNode(): String

    init {
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child("1OlV0BFqhzNzSMVI0vmoZlTHwAJ2").child(getRootNode())
    }

    fun getEntityClass(): Class<Model> {
        val superclass = javaClass.genericSuperclass as ParameterizedType
        return superclass.actualTypeArguments[0] as Class<Model>
    }

    interface FirebaseDatabaseRepositoryCallback<T> {
        fun onSuccess(result: List<T>)

        fun onError(e: Exception)
    }

    fun addListener(firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>) {
        this.firebaseCallback = firebaseCallback
        listener = object :ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
                firebaseCallback.onError(databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear()
                for (child in dataSnapshot.children) {
                    val data = child.getValue(getEntityClass());
                    list.add(data!!)
                }
                firebaseCallback.onSuccess(list)
            }

        }
        databaseReference.addValueEventListener(listener!!)
    }

    fun removeListener() {
        databaseReference.removeEventListener(listener!!)
    }
}
