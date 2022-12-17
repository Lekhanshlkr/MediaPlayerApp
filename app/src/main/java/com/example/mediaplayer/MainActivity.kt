package com.example.mediaplayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.song_ticket.view.*

class MainActivity : AppCompatActivity() {

    var listofSongs = ArrayList<SongInfo>()
    var adapter:SongAdapter?=null
    var medPl:MediaPlayer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
//        loadURLOnline()
//        adapter = SongAdapter(listofSongs)
//        lsListSongs.adapter = adapter

        var myTracking = MySongTrack()
        myTracking.start()
    }

    fun loadURLOnline(){
        listofSongs.add(SongInfo("Ram Ram","MC Square","https://open.spotify.com/track/49TNlAQZXmFBteKZBF9rj0?si=890929fb8a094ec9"))
        listofSongs.add(SongInfo("Badmos Chora","MC Square","https://open.spotify.com/track/0U8fm89VKJwDYa0mzh6foW?si=0dc7a724570c42c0"))
        listofSongs.add(SongInfo("I Guess","KR"+"$"+"NA","https://open.spotify.com/track/0OxG3hlJNNzXcSrNVXEu8f?si=3ca8b663cfc14b6e"))
        listofSongs.add(SongInfo("Hal e Dil","Harshit Saxena","https://open.spotify.com/track/6GQK4G5o60E8YA18DGpAzv?si=568739525e324113"))

    }

    var STORAGEACCESS = 123
    fun checkPermission(){
        if(Build.VERSION.SDK_INT>=29 ){
            if(ActivityCompat.
                checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),STORAGEACCESS)
                return;
            }
        }
        loadSong()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){

            STORAGEACCESS->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    loadSong()
                }
                else{
                    Toast.makeText(this,"We cannot access your storage", Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("Range")
    fun loadSong(){
        val allSongsURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val cursor = contentResolver.query(allSongsURI,null,selection,null,null)
        if(cursor!=null){
            if(cursor!!.moveToFirst()){
                do{
                    val songUrl = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val songArtist = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val songName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    listofSongs.add(SongInfo(songName,songArtist,songUrl))
                }while(cursor!!.moveToNext())
            }
            cursor!!.close()
            adapter = SongAdapter(listofSongs)
            lsListSongs.adapter = adapter
        }
    }

    inner class SongAdapter:BaseAdapter{

        var myListSong = ArrayList<SongInfo>()

        constructor(myListSong:ArrayList<SongInfo>):super(){
            this.myListSong=myListSong
        }

        override fun getCount(): Int {
            return myListSong.size
        }

        override fun getItem(p0: Int): Any {
            return myListSong[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(index: Int, view: View?, p2: ViewGroup?): View {
            var myView = layoutInflater.inflate(R.layout.song_ticket,null)
            val song = this.myListSong[index]
            myView.tvSongName.text = song.title
            myView.tvSinger.text = song.author

            myView.buPlay.setOnClickListener {
                medPl = MediaPlayer()
                if(myView.buPlay.text.equals("Stop")){
                    medPl!!.stop()
                    myView.buPlay.text = "Play"
                }
                else{
                    try{
                        medPl!!.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        medPl!!.setDataSource(song.songUrl)
                        medPl!!.prepare()
                        medPl!!.start()
                        sbProgress.max = medPl!!.duration
                        myView.buPlay.text = "Stop"

                    } catch (_:Exception){ }
                }

            }

            return myView
        }

    }

    inner class MySongTrack:Thread(){

        override fun run() {
            while(true){
                try{
                    Thread.sleep(1000)
                } catch (_:Exception){ }
                runOnUiThread {
                    if(medPl!=null){
                        sbProgress.progress = medPl!!.currentPosition
                    }
                }
            }
        }
    }
}