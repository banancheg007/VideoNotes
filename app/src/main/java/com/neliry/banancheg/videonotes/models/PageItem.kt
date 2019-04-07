package com.neliry.banancheg.videonotes.models

class PageItem(id:String?= null,
               var content: String? = null,
               var type: String? = null,
               var width: Int? = null,
               var height: Int? = null,
               var x: Int? = null,
               var y: Int? = null): BaseItem(id)