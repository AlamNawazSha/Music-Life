package com.example.musiclife

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.example.musiclife.databinding.MusicRvBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MusicAdapter(private val context: Context,private var musicList : ArrayList<MusicDataClass>,private val playListDetails: Boolean = false,
    private val selectionActivity: Boolean = false):RecyclerView.Adapter<MusicAdapter.MusiceHolder>() {
    class MusiceHolder(binding: MusicRvBinding):RecyclerView.ViewHolder(binding.root) {
        val  tile = binding.rvsongname
        val  albums = binding.rvsonalbam
        val  image = binding.songrvimg
        val  duration = binding.rvduration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusiceHolder {
        return MusiceHolder(MusicRvBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun onBindViewHolder(holder: MusiceHolder, position: Int) {
        holder.tile.text = musicList[position].title
        holder.albums.text = musicList[position].album
        holder.duration.text = formatDuration(musicList[position].duration)
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.logo).centerCrop())
            .into(holder.image)

        when {
            playListDetails -> {
                holder.root.setOnClickListener{
                    sendIntent(ref = "PlayListAdapter", pos = position)
                }
            }
            selectionActivity ->{
                holder.root.setOnClickListener{
                    if (addSong(musicList[position])){
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.background))
                    }
                    else holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                }
            }
            else -> {
                holder.root.setOnClickListener {
                    when {
                        MainActivity.search -> sendIntent(ref = "MusicAdapterSearch", pos = position)
                        musicList[position].id == Player.nowPlayingId -> sendIntent(ref = "NowPlaying", pos = Player.songPosition)
                        else -> sendIntent("MusicAdapter", position) } }

            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun  updateMusicLiIst(searchList : ArrayList<MusicDataClass>){
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }
    private fun sendIntent(ref : String, pos : Int){
        val intent = Intent(context,Player::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)
        ContextCompat.startActivity(context,intent,null)
    }
    private fun addSong(song : MusicDataClass): Boolean{
        PlayListActivity.musicPlayList.ref[PlayListDetails.currentPlayListPos].playList.forEachIndexed{index, musicDataClass ->
            if (song.id == musicDataClass.id){
                PlayListActivity.musicPlayList.ref[PlayListDetails.currentPlayListPos].playList.removeAt(index)
                return false
            }
        }
        PlayListActivity.musicPlayList.ref[PlayListDetails.currentPlayListPos].playList.add(song)
        return true
    }
    @SuppressLint("NotifyDataSetChanged")
    fun refrashPlayList(){
        musicList = ArrayList()
        musicList = PlayListActivity.musicPlayList.ref[PlayListDetails.currentPlayListPos].playList
        notifyDataSetChanged()
    }
}

