package com.neliry.banancheg.videonotes.mvvm.repository


import com.neliry.banancheg.videonotes.mvvm.Theme


class ThemeRepository : FirebaseDatabaseRepository<Theme>() {
    override fun getRootNode(): String {
        return "themes"
    }


}
