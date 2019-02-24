package com.neliry.banancheg.videonotes.viewmodels

import com.neliry.banancheg.videonotes.viewmodels.FirebaseViewModel
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.repositories.ThemeRepository

class ThemesViewModel: FirebaseViewModel<Theme>(){
    override var repository: FirebaseDatabaseRepository<Theme> =
        ThemeRepository()
}