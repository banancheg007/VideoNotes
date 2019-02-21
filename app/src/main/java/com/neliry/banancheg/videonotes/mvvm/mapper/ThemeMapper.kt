package com.neliry.banancheg.videonotes.mvvm.mapper

import com.google.firebase.database.DataSnapshot
import com.neliry.banancheg.videonotes.mvvm.Theme
import com.neliry.banancheg.videonotes.mvvm.ThemeEntity
import java.lang.reflect.ParameterizedType
import java.util.ArrayList

class ThemeMapper : FirebaseMapper<ThemeEntity, Theme>() {
    override fun map(themeEntity: ThemeEntity): Theme {
        val theme = Theme()
        theme.id = themeEntity.id
        theme.name = themeEntity.name
        return theme
    }
}
