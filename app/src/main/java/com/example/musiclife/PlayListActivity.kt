package com.example.musiclife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musiclife.databinding.ActivityPlayListBinding
import com.example.musiclife.databinding.AddPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Locale

class PlayListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayListBinding
    private lateinit var adapter: PlayListAdapter

    companion object{
        var musicPlayList : MusicPlayList = MusicPlayList()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlayListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.backbtn.setOnClickListener {
            finish()
        }


        binding.PlayListRv.setHasFixedSize(true)
        binding.PlayListRv.setItemViewCacheSize(15)
        binding.PlayListRv.layoutManager = GridLayoutManager(this, 2)
        adapter = PlayListAdapter(this , playListList = musicPlayList.ref)
        binding.PlayListRv.adapter = adapter

        binding.addPlayListBtn.setOnClickListener {
            customAlertDialog()
        }
    }

    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this).inflate(R.layout.add_playlist, binding.root,false)
        val binder = AddPlaylistBinding.bind(customDialog)
        val bilder = MaterialAlertDialogBuilder(this)
        bilder.setView(customDialog)
            .setTitle("Play List Details")
            .setPositiveButton("ADD") { dialog, _ ->
               val playlistName = binder.playlistName.text
               val createdBy = binder.playlistYourName.text
                if (playlistName != null && createdBy != null ){
                    if (playlistName.isNotEmpty() && createdBy.isNotEmpty()){
                        addPlayList(playlistName.toString(),createdBy.toString())
                    }
                }
                dialog.dismiss()
            }.show()
    }

    private fun addPlayList(name : String, createdBy : String){
        var playListExists  = false
        for (i in musicPlayList.ref){
            if (name.equals(i.name)){
                playListExists = true
                break
            }
        }
        if (playListExists) Toast.makeText(this,"PlayList Exits", Toast.LENGTH_SHORT).show()
        else{
            val tempPlayList = playList()
            tempPlayList.name = name
            tempPlayList.playList = ArrayList()
            tempPlayList.createdBy = createdBy
            val calendar = java.util.Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MM yyyy", Locale.ENGLISH)
            tempPlayList.createdOn = sdf.format(calendar)
            musicPlayList.ref.add(tempPlayList)
            adapter.refrashPlayList()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}