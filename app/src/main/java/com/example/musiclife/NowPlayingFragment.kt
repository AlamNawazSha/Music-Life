package com.example.musiclife

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musiclife.databinding.FragmentNowPlayingBinding


class NowPlayingFragment : Fragment(){
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.playPauseNP.setOnClickListener {
            if (Player.isPlaying) pauseMusic()
            else playMusic()
        }
        binding.nextNp.setOnClickListener {
            setSongPosition(increment = true)
            Player.musicService!!.createMediaPlayer()
            Glide.with(this)
                .load(Player.musicListPA!![Player.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.logo).centerCrop())
                .into(NowPlayingFragment.binding.songImgNp)
            binding.songNameNP.text  = Player.musicListPA!![Player.songPosition].title
            Player.musicService!!.showNotification(R.drawable.baseline_pause_24)
            playMusic()
        }
        binding.root.setOnClickListener{
            val intent = Intent(requireContext(),Player::class.java)
            intent.putExtra("index",Player.songPosition)
            intent.putExtra("class","NowPlaying")
            ContextCompat.startActivity(requireContext(),intent,null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (Player.musicService != null)
            binding.root.visibility = View.VISIBLE
        binding.songNameNP.isSelected = true
        try {


            Glide.with(this)
                .load(Player.musicListPA!![Player.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.logo).centerCrop())
                .into(binding.songImgNp)
        }catch (e : Exception){
            return
        }
        binding.songNameNP.text = Player.musicListPA!![Player.songPosition].title

        if (Player.isPlaying) binding.playPauseNP.setIconResource(R.drawable.baseline_pause_24)
        else binding.playPauseNP.setIconResource(R.drawable.baseline_play_arrow_24)

    }

    private fun playMusic(){
        Player.musicService!!.mediaPlayer!!.start()
        binding.playPauseNP.setIconResource(R.drawable.baseline_pause_24)
        Player.musicService!!.showNotification(R.drawable.baseline_pause_24)
        Player.isPlaying = true
    }
    private fun pauseMusic(){
        Player.musicService!!.mediaPlayer!!.pause()
        binding.playPauseNP.setIconResource(R.drawable.baseline_play_arrow_24)
        Player.musicService!!.showNotification(R.drawable.baseline_play_arrow_24)
        Player.isPlaying = false
    }
}