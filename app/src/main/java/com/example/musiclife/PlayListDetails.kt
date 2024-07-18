package com.example.musiclife

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musiclife.databinding.ActivityPlayListDetailsBinding
import com.example.musiclife.databinding.AddPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlayListDetails : AppCompatActivity() {
    lateinit var binding : ActivityPlayListDetailsBinding
    lateinit var adapter: MusicAdapter

    companion object{
        var currentPlayListPos : Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayListDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentPlayListPos = intent.extras?.get("index") as Int
        PlayListActivity.musicPlayList.ref[currentPlayListPos].playList = checkPlayList(playlist = PlayListActivity.musicPlayList.ref[currentPlayListPos].playList)
        binding.PlayListDetailsRv.setItemViewCacheSize(10)
        binding.PlayListDetailsRv.setHasFixedSize(true)
        binding.PlayListDetailsRv.layoutManager = LinearLayoutManager(this)
        adapter = MusicAdapter(this , PlayListActivity.musicPlayList.ref[currentPlayListPos].playList, playListDetails = true)
        binding.PlayListDetailsRv.adapter = adapter
        binding.backBtnPD.setOnClickListener { finish() }
        if (Favorite.favouriteSong.size < 1 )  binding.shufflePlayListBtnPD.visibility = View.INVISIBLE
        binding.shufflePlayListBtnPD.setOnClickListener {
            val intent = Intent(this, Player::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlaylistDetailsShuffle")
            startActivity(intent)
        }
        binding.addPlayListBtnPD.setOnClickListener{
            startActivity(Intent(this,Selection::class.java))
        }
        binding.deletePlayListPD.setOnClickListener {
            val bilder = MaterialAlertDialogBuilder(this)
            bilder.setTitle("Remove")
                .setMessage("Do You Want Remove All Songs")
                .setPositiveButton("Yes") { dialog, _ ->
                    PlayListActivity.musicPlayList.ref[currentPlayListPos].playList.clear()
                    adapter.refrashPlayList()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = bilder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }

        }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.PlayListNameHPD.text = PlayListActivity.musicPlayList.ref[currentPlayListPos].name
        binding.PlayListNamePD.text = " Total ${adapter.itemCount} Songs.\n\n" +
                " Created On:\n${PlayListActivity.musicPlayList.ref[currentPlayListPos].createdOn}\n\n" +
                " ${PlayListActivity.musicPlayList.ref[currentPlayListPos].createdBy}"
        if (adapter.itemCount > 0){
            Glide.with(this)
                .load(PlayListActivity.musicPlayList.ref[currentPlayListPos].playList[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.logo).centerCrop())
                .into(binding.PlayListImgPD)
            binding.shufflePlayListBtnPD.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonStringPlayList = GsonBuilder().create().toJson(PlayListActivity.musicPlayList)
        editor.putString("MusicPlayList",jsonStringPlayList)
        editor.apply()
    }

    }
