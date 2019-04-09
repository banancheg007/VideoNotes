package com.neliry.banancheg.videonotes.models

class Conspectus(id:String? = null,
                 name:String? = null,
                 var previewUrl: String? = null,
                 var time:  Long? = null,
                 var videoUrl: String? = null): BaseItem(id,name)
