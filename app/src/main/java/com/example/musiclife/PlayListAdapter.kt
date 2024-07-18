package com.example.musiclife

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musiclife.databinding.PlaylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayListAdapter(private val context: Context, private var playListList : ArrayList<playList>):
    RecyclerView.Adapter<PlayListAdapter.MusiceHolder>() {
    class MusiceHolder(binding: PlaylistViewBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistImage
        val name = binding.playlistName
        val delet = binding.playlistDeleteBtn
        val root = binding.root

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusiceHolder {
        return MusiceHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return playListList.size
    }

    override fun onBindViewHolder(holder: MusiceHolder, position: Int) {
        holder.name.text = playListList[position].name
        holder.name.isSelected = true
        holder.delet.setOnClickListener {
            val bilder = MaterialAlertDialogBuilder(context)
            bilder.setTitle(playListList[position].name)
                .setMessage("Do You Want Delete PlayList")
                .setPositiveButton("Yes") { dialog, _ ->
                    PlayListActivity.musicPlayList.ref.removeAt(position)
                    refrashPlayList()
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
        holder.root.setOnClickListener{
            val intent = Intent(context,PlayListDetails::class.java)
            intent.putExtra("index" ,position)
            ContextCompat.startActivity(context,intent,null)
        }
        if (PlayListActivity.musicPlayList.ref[position].playList.size > 0){
            Glide.with(context)
                .load(PlayListActivity.musicPlayList.ref[position].playList[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.logo).centerCrop())
                .into(holder.image)
        }
        }
    @SuppressLint("NotifyDataSetChanged")
    fun refrashPlayList(){
        playListList = ArrayList()
        playListList.addAll(PlayListActivity.musicPlayList.ref)
        notifyDataSetChanged()
    }

    }

