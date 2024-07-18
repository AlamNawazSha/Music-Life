package com.example.musiclife

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musiclife.databinding.ActivityFavoriteBinding

class Favorite : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: FavouritAdapter
    companion object{
        var favouriteSong : ArrayList<MusicDataClass> = ArrayList()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
       binding = ActivityFavoriteBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        favouriteSong = checkPlayList(favouriteSong)
        binding.backbtn.setOnClickListener {
            finish()
        }

        binding.favoriteRv.setHasFixedSize(true)
        binding.favoriteRv.setItemViewCacheSize(15)
        binding.favoriteRv.layoutManager = GridLayoutManager(this,4)
        adapter = FavouritAdapter(this , favouriteSong )
        binding.favoriteRv.adapter = adapter

        if (favouriteSong.size < 1 )  binding.shuffleBtnFA.visibility = View.INVISIBLE
        binding.shuffleBtnFA.setOnClickListener {
            val intent = Intent(this, Player::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "FavouriteShuffle")
            startActivity(intent)
        }
    }
}