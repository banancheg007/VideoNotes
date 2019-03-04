package com.neliry.banancheg.videonotes.repositories

import com.google.firebase.database.FirebaseDatabase
import com.neliry.banancheg.videonotes.models.Conspectus
import com.neliry.banancheg.videonotes.models.Theme

class ConspectusRepository : FirebaseDatabaseRepository<Conspectus>() {
    override fun getRootNode(): String {
        return "conspectuses"
    }

    init {
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child("1OlV0BFqhzNzSMVI0vmoZlTHwAJ2").child(getRootNode()).child("-LYwPvNpYTMeeYUNkFdh")
    }


}