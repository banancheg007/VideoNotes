package com.neliry.banancheg.videonotes.mvvm.repository


import com.neliry.banancheg.videonotes.mvvm.Theme
import com.neliry.banancheg.videonotes.mvvm.mapper.ThemeMapper

class ThemeRepository : FirebaseDatabaseRepository<Theme>(ThemeMapper()) {
    override fun getRootNode(): String {
        return "themes"
    }


}
