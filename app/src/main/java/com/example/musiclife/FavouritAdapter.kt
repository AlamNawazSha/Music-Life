package com.example.musiclife

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musiclife.databinding.FavouritViewBinding
import com.example.musiclife.databinding.MusicRvBinding

class FavouritAdapter(private val context: Context, private var musicList : ArrayList<MusicDataClass>):
    RecyclerView.Adapter<FavouritAdapter.MusiceHolder>() {
    class MusiceHolder(binding: FavouritViewBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.favoriteViewImg
        val name = binding.favoriteViewSongName
        val root = binding.root

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusiceHolder {
        return MusiceHolder(FavouritViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun onBindViewHolder(holder: MusiceHolder, position: Int) {
        holder.name.text = musicList[position].title
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.logo).centerCrop())
            .into(holder.image)
        holder.root.setOnClickListener{
            val intent = Intent(context,Player::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class","FavouriteAdapter")
            ContextCompat.startActivity(context,intent,null)
        }

    }

}
