package com.neliry.banancheg.videonotes

class ThemeRepository():FirebaseRepository(){
    override fun getRootNode(): String {
        return "themes"
    }

}