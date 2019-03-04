package com.neliry.banancheg.videonotes.repositories

import com.neliry.banancheg.videonotes.models.Conspectus
import com.neliry.banancheg.videonotes.models.Theme

class ConspectusRepository : FirebaseDatabaseRepository<Conspectus>() {
    override fun getRootNode(): String {
        return "conspectuses"
    }


}