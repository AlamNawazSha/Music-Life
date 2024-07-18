package com.example.musiclife

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.system.exitProcess

class NotificationReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.PREVIOUS ->{

                prevNextSong(increment = false, context = context!!)

            }
            ApplicationClass.PLAY ->
                if (Player.isPlaying) pauseMusic()
                else playMusic()
            ApplicationClass.NEXT ->{

                prevNextSong(increment = true, context = context!!)

            }

            ApplicationClass.EXIT ->{
               exitApplication()
            }


        }
    }

    private fun playMusic(){
        Player.isPlaying = true
        Player.musicService!!.mediaPlayer!!.start()
        Player.musicService!!.showNotification(R.drawable.baseline_pause_24)
        Player.binding.PusSongPA.setIconResource(R.drawable.baseline_pause_24)
        NowPlayingFragment.binding.playPauseNP.setIconResource(R.drawable.baseline_pause_24)
    }
    private fun pauseMusic(){
        Player.isPlaying = false
        Player.musicService!!.mediaPlayer!!.pause()
        Player.musicService!!.showNotification(R.drawable.baseline_play_arrow_24)
        Player.binding.PusSongPA.setIconResource(R.drawable.baseline_play_arrow_24)
        NowPlayingFragment.binding.playPauseNP.setIconResource(R.drawable.baseline_play_arrow_24)

    }

    private fun prevNextSong(increment : Boolean , context: Context){
        setSongPosition(increment = increment)
         Player.musicService!!.createMediaPlayer()
        Glide.with(context)
            .load(Player.musicListPA!![Player.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.logo).centerCrop())
            .into(Player.binding.SongImagPA)
        Player.binding.SongNamePA.text = Player.musicListPA!![Player.songPosition].title
        Glide.with(context)
            .load(Player.musicListPA!![Player.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.logo).centerCrop())
            .into(NowPlayingFragment.binding.songImgNp)
        NowPlayingFragment.binding.songNameNP.text  = Player.musicListPA!![Player.songPosition].title
        playMusic()
        Player.fIndex = favouriteChecker(Player.musicListPA!![Player.songPosition].id)
        if (Player.isFavourite) Player.binding.favoriteBtnPA.setImageResource(R.drawable.baseline_favorite_24)
        else Player.binding.favoriteBtnPA.setImageResource(R.drawable.baseline_favorite_border_24)

    }
}
