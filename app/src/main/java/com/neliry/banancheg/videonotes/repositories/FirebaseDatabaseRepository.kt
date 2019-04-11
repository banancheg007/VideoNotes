package com.neliry.banancheg.videonotes.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.neliry.banancheg.videonotes.models.*
import java.lang.Exception
import java.lang.reflect.ParameterizedType


open class FirebaseDatabaseRepository<Model> {
    var list = ArrayList<Model>()
    private lateinit var databaseReference: DatabaseReference
    private var listener: ValueEventListener? = null
    private lateinit var qvery: Query
    private lateinit var firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: FirebaseUser = mAuth.currentUser!!


    fun setDatabaseReference(reference: String){
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.uid).child(reference)
        qvery = databaseReference
    }
    fun setDatabaseReference(reference: String, childReference:String){
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.uid).child(reference).child(childReference)
        qvery = databaseReference
    }

    fun setDatabaseReferenceOrder(reference: String){
        qvery = databaseReference.orderByChild(reference)
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
        qvery.addValueEventListener(listener!!)
    }

    fun removeListener() {
        Log.d("myTag",listener.toString())
        databaseReference.removeEventListener(listener!!)
        qvery.removeEventListener(listener!!)
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
        //databaseReference.child(key).setValue(baseItem)
        FirebaseDatabase.getInstance().getReference("users").child(user.uid).child("conspectuses").child(parentId).child(key).setValue(baseItem)
        FirebaseDatabase.getInstance().getReference("users").child(user.uid).child("all_conspectuses").child(key).setValue(baseItem)
    }

    fun saveNewItem(baseItem: Page, parentId: String){
        val myReference = FirebaseDatabase.getInstance().getReference("users").child(user.uid).child("pages").child(parentId)
        var key = myReference.push().key!!
        baseItem.id = key
        myReference.child(key).setValue(baseItem)
    }

    fun saveNewItem(baseItem: PageItem, parentId: String){
        val myReference = FirebaseDatabase.getInstance().getReference("users").child(user.uid).child("views").child(parentId)
        var key = myReference.push().key!!
        baseItem.id = key
        myReference.child(key).setValue(baseItem)
    }

    fun removePageItems(parentId: String){
        var myRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid).child("views").child(parentId)
        myRef.removeValue()
    }
    fun removePage(itemId: String ,parentId: String){
        var myRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid).child("pages").child(parentId).child(itemId)
        myRef.removeValue()
    }

    fun renamePage(name: String ,itemId: String ,parentId: String){
        var myRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid).child("pages").child(parentId).child(itemId).child("name")
        myRef.setValue(name)
    }

    fun changePageContent(it: String, height: Int, width: Int, itemId: String ,parentId: String){
        var myRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid).child("pages").child(parentId).child(itemId)
        myRef.child("content").setValue(it)
        myRef.child("height").setValue(height)
        myRef.child("width").setValue(width)
    }
}
