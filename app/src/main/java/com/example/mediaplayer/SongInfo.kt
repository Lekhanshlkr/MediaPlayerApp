package com.example.mediaplayer

class SongInfo {
    var title:String?=null
    var author:String?=null
    var songUrl:String?=null

    constructor(title:String,author:String,songUrl:String){
        this.title=title
        this.author=author
        this.songUrl=songUrl
    }

}