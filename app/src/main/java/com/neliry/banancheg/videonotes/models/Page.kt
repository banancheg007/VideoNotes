package com.neliry.banancheg.videonotes.models

class Page(id:String? = null,
           name:String? = null,
           var time: Int? = null,
           var creationTime: Int? = null): BaseItem(id, name)
