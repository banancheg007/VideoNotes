package com.neliry.banancheg.videonotes.repositories

import com.neliry.banancheg.videonotes.models.Theme

class ConspectusRepository : FirebaseDatabaseRepository<Theme>() {
    override fun getRootNode(): String {
        return "conspectuses"
    }


}