@file:Suppress("DEPRECATION")

package com.example.musiclife

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musiclife.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Player : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    @SuppressLint("StaticFieldLeak")
    companion object{
        var musicListPA : ArrayList<MusicDataClass>? = null
        var songPosition: Int = 0
        var isPlaying : Boolean = false
        var musicService : MusicService? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var repeat:Boolean = false
        var min15 : Boolean = false
        var min30 : Boolean = false
        var min60 : Boolean = false
        var nowPlayingId : String = ""
        var isFavourite : Boolean = false
        var fIndex : Int = -1

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)




        binding.backbtn.setOnClickListener {
            finish()
        }
        initializeLayout()
        binding.PusSongPA.setOnClickListener{
            if (isPlaying)pauseMusic()
            else playMusic()
            }
        binding.previousBtnPA.setOnClickListener {
            prevNextSong(increment = false)
        }
        binding.FreantBtnPA.setOnClickListener {
            prevNextSong(increment = true)
        }

        binding.seekBarSongPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })

        binding.repeatBtnPA.setOnClickListener {
        if (!repeat){
            repeat = true
            binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.background))
        }else{
            repeat = false
            binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.black))
        }
        }
        binding.equalizerBtnPA.setOnClickListener {
           try {
               val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
               eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
               eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME,baseContext.packageName)
               eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE,AudioEffect.CONTENT_TYPE_MUSIC)
               startActivityForResult(eqIntent,10)
           }catch (e : Exception){
               Toast.makeText(this,"Equalizer Feature not Supported",Toast.LENGTH_SHORT).show()
           }
        }
        binding.timeBtnPA.setOnClickListener {
            val timer =  min15 || min30 || min60
            if (!timer) showBottomSheetDialog()
            else{
                val bilder = MaterialAlertDialogBuilder(this)
                bilder.setTitle("Stop Timer ")
                    .setMessage("Do You Want To Stop Timer??")
                    .setPositiveButton("Yes"){ _,_ ->
                        min15=false
                        min30=false
                        min60=false
                        binding.timeBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.black))
                    }
                    .setNegativeButton("No"){dialog,_ ->
                        dialog.dismiss()
                    }
                val customDialog = bilder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            }
            }

        binding.shareBtnPA.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse (musicListPA!![songPosition].path))
            startActivity(Intent.createChooser(shareIntent,"Share the Music File"))
        }
        binding.favoriteBtnPA.setOnClickListener {

            when{
               isFavourite -> {
                   isFavourite = false
                   binding.favoriteBtnPA.setImageResource(R.drawable.baseline_favorite_border_24)
                   Favorite.favouriteSong.removeAt(Player.fIndex)
               }
             !isFavourite->{
                    isFavourite = true
                    binding.favoriteBtnPA.setImageResource(R.drawable.baseline_favorite_24)
                    Favorite.favouriteSong.add(musicListPA!![songPosition])
                }


            }


//                if (isFavourite) {
//                    binding.favoriteBtnPA.setImageResource(R.drawable.baseline_favorite_border_24)
//                    isFavourite = false
//                    Favorite.favouriteSong.removeAt(fIndex)
//                    if (!isFavourite) {
//                        isFavourite = true
//                        binding.favoriteBtnPA.setImageResource(R.drawable.baseline_favorite_24)
//                        Favorite.favouriteSong.add(musicListPA!![songPosition])
//                    }
//                }


        }



        }

    private fun initializeLayout(){
        songPosition = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")){
            "MusicAdapter" ->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA!!.addAll(MainActivity.MusicListMa)
                setLayout()


            }
            "MainActivity"  ->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA!!.addAll(MainActivity.MusicListMa)
                musicListPA!!.shuffle()
                setLayout()

            }
            "MusicAdapterSearch"->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA= ArrayList()
                musicListPA!!.addAll(MainActivity.musicListSearch)
                setLayout()
            }

            "NowPlaying"->{
              setLayout()
                binding.FirstTimePA.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.SecondTimePA.text =  formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBarSongPA.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekBarSongPA.max = musicService!!.mediaPlayer!!.duration
                if (isPlaying) binding.PusSongPA.setIconResource(R.drawable.baseline_pause_24)
                else binding.PusSongPA.setIconResource(R.drawable.baseline_play_arrow_24)
            }

            "FavouriteAdapter"->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA= ArrayList()
                musicListPA!!.addAll(Favorite.favouriteSong)
                setLayout()
            }
            "FavouriteShuffle"->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA!!.addAll(Favorite.favouriteSong)
                musicListPA!!.shuffle()
                setLayout()
            }
            "PlayListAdapter"->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA!!.addAll(PlayListActivity.musicPlayList.ref[PlayListDetails.currentPlayListPos].playList)
                setLayout()
            }
            "PlaylistDetailsShuffle"->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA!!.addAll(PlayListActivity.musicPlayList.ref[PlayListDetails.currentPlayListPos].playList)
                musicListPA!!.shuffle()
                setLayout()
            }
        }
    }
    private fun setLayout(){
            fIndex = favouriteChecker(musicListPA!![songPosition].id)
            Glide.with(this)
                .load(musicListPA!![songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.logo).centerCrop())
                .into(binding.SongImagPA)
            binding.SongNamePA.text = musicListPA!![songPosition].title
        if (repeat)binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.background))
        if (min15 || min30 || min60 ) binding.timeBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.background))
        if (isFavourite) binding.favoriteBtnPA.setImageResource(R.drawable.baseline_favorite_24)
        else binding.favoriteBtnPA.setImageResource(R.drawable.baseline_favorite_border_24)



    }

    @SuppressLint("SuspiciousIndentation")
    private fun createMediaPlayer(){

try {


    if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
        musicService!!.mediaPlayer!!.reset()
        musicService!!.mediaPlayer!!.setDataSource(musicListPA!![songPosition].path)
        musicService!!.mediaPlayer!!.prepare()
        musicService!!.mediaPlayer!!.start()
        isPlaying = true
        binding.PusSongPA.setIconResource(R.drawable.baseline_pause_24)
        musicService!!.showNotification(R.drawable.baseline_pause_24)
    binding.FirstTimePA.text= formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
    binding.SecondTimePA.text= formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
    binding.seekBarSongPA.progress = 0
    binding.seekBarSongPA.max = musicService!!.mediaPlayer!!.duration
    musicService!!.mediaPlayer!!.setOnCompletionListener(this)
    nowPlayingId = musicListPA!![songPosition].id

}
catch (e : Exception){
    return Toast.makeText(this, "error$e",Toast.LENGTH_SHORT).show()
}

    }
    private  fun playMusic(){
        binding.PusSongPA.setIconResource(R.drawable.baseline_pause_24)
        musicService!!.showNotification(R.drawable.baseline_pause_24)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }
    private  fun pauseMusic(){
        binding.PusSongPA.setIconResource(R.drawable.baseline_play_arrow_24)
        musicService!!.showNotification(R.drawable.baseline_play_arrow_24)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun prevNextSong(increment : Boolean){
        if (increment){
            setSongPosition(increment = true)
            setLayout()
            createMediaPlayer()
        }else{
            setSongPosition(increment = false)
            setLayout()
            createMediaPlayer()
        }
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(true)
        createMediaPlayer()
        try {
            setLayout()
        }
        catch (e : Exception){
            return
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 || resultCode == RESULT_OK){
            return
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun showBottomSheetDialog(){
        val dialog = BottomSheetDialog(this)
            dialog.setContentView(R.layout.bottom_sheet_dialog)
            dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener{
                Toast.makeText(baseContext,"Music Will Stop After 15 Minutes", Toast.LENGTH_SHORT).show()
                binding.timeBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.background))
            min15 = true
            Thread{Thread.sleep(15 * 60000)
            if (min15) exitApplication()}.start()
                dialog.dismiss()
            }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener{
            Toast.makeText(baseContext,"Music Will Stop After 30 Minutes", Toast.LENGTH_SHORT).show()
            binding.timeBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.background))
            min30 = true
            Thread{Thread.sleep(30 * 60000)
                if (min30) exitApplication()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener{
            Toast.makeText(baseContext,"Music Will Stop After 60 Minutes", Toast.LENGTH_SHORT).show()
            binding.timeBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.background))
            min60 = true
            Thread{Thread.sleep(60 * 60000)
                if (min60) exitApplication()}.start()
            dialog.dismiss()
        }
    }

}