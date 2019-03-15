package com.neliry.banancheg.videonotes.repositories

import com.google.firebase.database.FirebaseDatabase
import com.neliry.banancheg.videonotes.models.Conspectus

class ConspectusRepository : FirebaseDatabaseRepository<Conspectus>() {
    override fun getRootNode(): String {
        return "conspectuses"
    }

    /*init {
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child("1OlV0BFqhzNzSMVI0vmoZlTHwAJ2").child(getRootNode()).child("-LYwPvNpYTMeeYUNkFdh")
    }*/
    fun setConspectusesByThemes(themeId:String){
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child("1OlV0BFqhzNzSMVI0vmoZlTHwAJ2").child(getRootNode()).child(themeId)
    }

    fun setAllConspectuses(){
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child("1OlV0BFqhzNzSMVI0vmoZlTHwAJ2").child(getRootNode()).child("all")
    }


}