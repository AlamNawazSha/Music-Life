package com.example.musiclife


import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MusicService : Service(),OnAudioFocusChangeListener {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager
    private lateinit var mediaSession: MediaSessionCompat
    var contentIntent : PendingIntent? = null
    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "MyMusic")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(playPauseBtn: Int) {

//            val intent = Intent(baseContext,MainActivity::class.java)
////            intent.putExtra("index",Player.songPosition)
////            intent.putExtra("class","NowPlaying")
//            contentIntent = PendingIntent.getActivity(this,0,intent,0)


        val preIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(ApplicationClass.PREVIOUS)
        val prevPandingIntent =
            PendingIntent.getBroadcast(baseContext, 0, preIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPandingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPandingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val exitIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPandingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val imgArt = getImgArt(Player.musicListPA!![Player.songPosition].path)
        val img = if (imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.logo)
        }

        val notificationCompat = NotificationCompat.Builder(this, ApplicationClass.CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(Player.musicListPA!![Player.songPosition].title)
            .setContentText(Player.musicListPA!![Player.songPosition].artist)
            .setSmallIcon(R.drawable.logo)
            .setLargeIcon(img)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.baseline_navigate_before_24, "Previous", prevPandingIntent)
            .addAction(playPauseBtn, "Play", playPandingIntent)
            .addAction(R.drawable.baseline_navigate_next_24, "next", nextPandingIntent)
            .addAction(R.drawable.baseline_exit_to_app_24, "Exit", exitPandingIntent)
            .build()
        startForeground(13, notificationCompat)

        Player.musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        Player.musicService!!.audioManager.requestAudioFocus(Player.musicService, AudioManager.STREAM_MUSIC , AudioManager.AUDIOFOCUS_GAIN)
    }

    @SuppressLint("SuspiciousIndentation")
    fun createMediaPlayer() {

        try {


            if (Player.musicService!!.mediaPlayer == null) Player.musicService!!.mediaPlayer = MediaPlayer()
                Player.musicService!!.mediaPlayer!!.reset()
                Player.musicService!!.mediaPlayer!!.setDataSource(Player.musicListPA!![Player.songPosition].path)
                Player.musicService!!.mediaPlayer!!.prepare()
                Player.binding.PusSongPA.setIconResource(R.drawable.baseline_pause_24)
                Player.musicService!!.showNotification(R.drawable.baseline_pause_24)
            Player.binding.FirstTimePA.text= formatDuration(mediaPlayer!!.currentPosition.toLong())
            Player.binding.SecondTimePA.text= formatDuration(mediaPlayer!!.duration.toLong())
            Player.binding.seekBarSongPA.progress = 0
            Player.binding.seekBarSongPA.max = mediaPlayer!!.duration
            Player.nowPlayingId = Player.musicListPA!![Player.songPosition].id


        } catch (e: Exception) {
            return Toast.makeText(this, "error$e", Toast.LENGTH_SHORT).show()
        }

    }
    fun seekBarSetup(){
        runnable = Runnable{
            Player.binding.FirstTimePA.text= formatDuration(mediaPlayer!!.currentPosition.toLong())
            Player.binding.seekBarSongPA.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)

    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0){
            Player.binding.PusSongPA.setIconResource(R.drawable.baseline_play_arrow_24)
            NowPlayingFragment.binding.playPauseNP.setIconResource(R.drawable.baseline_play_arrow_24)
            showNotification(R.drawable.baseline_play_arrow_24)
            Player.isPlaying = false
            mediaPlayer!!.pause()
        }
        else{
            Player.binding.PusSongPA.setIconResource(R.drawable.baseline_pause_24)
            NowPlayingFragment.binding.playPauseNP.setIconResource(R.drawable.baseline_pause_24)
            showNotification(R.drawable.baseline_pause_24)
            Player.isPlaying = true
            mediaPlayer!!.start()
        }
    }
}