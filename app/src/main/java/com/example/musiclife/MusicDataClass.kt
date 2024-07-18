package com.example.musiclife

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import java.io.File
import kotlin.system.exitProcess


data class MusicDataClass(
    val id : String,
    val title: String,
    val album: String,
    val artist : String,
    val duration : Long = 0,
    val path : String,
    val artUri : String
)


class playList {
    lateinit var name : String
    lateinit var playList : ArrayList<MusicDataClass>
    lateinit var createdBy : String
    lateinit var createdOn : String
}

class MusicPlayList{
    var ref : ArrayList<playList> = ArrayList()
}

@SuppressLint("DefaultLocale")
fun formatDuration(duration: Long):String{
    val minutes = java.util.concurrent.TimeUnit.MINUTES.convert(duration,java.util.concurrent.TimeUnit.MILLISECONDS)
    val secondes = (java.util.concurrent.TimeUnit.SECONDS.convert(duration,java.util.concurrent.TimeUnit.MILLISECONDS) -
            minutes*java.util.concurrent.TimeUnit.SECONDS.convert(1,java.util.concurrent.TimeUnit.MINUTES))
    return String.format("%02d:%02d",minutes,secondes)
}

fun getImgArt(path: String): ByteArray? {
    val retriver = MediaMetadataRetriever()
    retriver.setDataSource(path)
    return retriver.embeddedPicture
}

 fun setSongPosition(increment: Boolean){
 if (!Player.repeat){
     if (increment){
         if (Player.musicListPA!!.size- 1 == Player.songPosition){
             Player.songPosition = 0
         }
         else{
             ++Player.songPosition
         }
     }else{
         if (0 == Player.songPosition){
             Player.songPosition = Player.musicListPA!!.size- 1
         }
         else{
             --Player.songPosition
         }
     }
 }
}
fun exitApplication(){
    if (Player.musicService != null){
        Player.musicService!!.audioManager.abandonAudioFocus(Player.musicService)
        Player.musicService!!.stopForeground(true)
        Player.musicService!!.mediaPlayer!!.release()
        Player.musicService = null}
    exitProcess(1)
}

fun favouriteChecker(id : String): Int{
    Player.isFavourite = false
    Favorite.favouriteSong.forEachIndexed { index, musicDataClass ->
        if (id == musicDataClass.id){
            Player.isFavourite = true
            return index
        }
    }
    return -1
}
fun checkPlayList(playlist: ArrayList<MusicDataClass>): ArrayList<MusicDataClass>{
    playlist.forEachIndexed { index, musicDataClass ->
        val file = File(musicDataClass.path)
        if (!file.exists())
            playlist.removeAt(index)
    }
    return playlist
}