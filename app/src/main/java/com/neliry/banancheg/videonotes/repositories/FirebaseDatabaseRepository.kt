package com.neliry.banancheg.videonotes.repositories

import android.util.Log
import com.google.firebase.database.*
import com.neliry.banancheg.videonotes.models.*
import java.lang.Exception
import java.lang.reflect.ParameterizedType


open class FirebaseDatabaseRepository<Model> {
    var list = ArrayList<Model>()
    private lateinit var databaseReference: DatabaseReference
    private var listener: ValueEventListener? = null

    private lateinit var firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>



    fun setDatabaseReference(reference: String){
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child("1OlV0BFqhzNzSMVI0vmoZlTHwAJ2").child(reference)
    }
    fun setDatabaseReference(reference: String, childReference:String){
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child("1OlV0BFqhzNzSMVI0vmoZlTHwAJ2").child(reference).child(childReference)
    }

    @Suppress("UNCHECKED_CAST")
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
                    val data = child.getValue(getEntityClass())
                    list.add(data!!)
                }
                firebaseCallback.onSuccess(list)
            }

        }
        databaseReference.addValueEventListener(listener!!)
    }

    fun removeListener() {
        Log.d("myTag",listener.toString())
        databaseReference.removeEventListener(listener!!)
        //Log.d("myTag",listener.toString())
    }

    fun saveNewItem(baseItem: BaseItem){
        var key = databaseReference.push().key!!
        baseItem.id = key
        databaseReference.child(key).setValue(baseItem)
    }

    fun saveNewItem(baseItem: BaseItem, parentId: String){
        var key = databaseReference.push().key!!
        baseItem.id = key
        databaseReference.child(key).setValue(baseItem)
        FirebaseDatabase.getInstance().getReference("users").child("1OlV0BFqhzNzSMVI0vmoZlTHwAJ2").child("conspectuses").child(parentId).child(key).setValue(baseItem)
        FirebaseDatabase.getInstance().getReference("users").child("1OlV0BFqhzNzSMVI0vmoZlTHwAJ2").child("all_conspectuses").child(key).setValue(baseItem)
    }
}
