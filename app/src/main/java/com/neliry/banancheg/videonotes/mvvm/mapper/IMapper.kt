package com.neliry.banancheg.videonotes.mvvm.mapper

interface IMapper<From, To> {

    fun map(from: From): To
}
