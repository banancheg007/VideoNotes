package com.neliry.banancheg.videonotes.entities

class Page(id:String? = null,
           name:String? = null,
           var time: Int? = null,
           var creationTime: String? = null,
           var content: String? = null,
           var height: Float? = null,
           var width: Float? = null): BaseItem(id, name)
