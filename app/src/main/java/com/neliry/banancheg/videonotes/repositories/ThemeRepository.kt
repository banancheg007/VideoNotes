package com.neliry.banancheg.videonotes.repositories


import com.neliry.banancheg.videonotes.models.Theme


class ThemeRepository : FirebaseDatabaseRepository<Theme>() {
    override fun getRootNode(): String {
        return "themes"
    }


}
