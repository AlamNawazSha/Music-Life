package com.example.musiclife

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musiclife.databinding.ActivityPlayListDetailsBinding
import com.example.musiclife.databinding.ActivitySelectionBinding

class Selection : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter: MusicAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectionRv.setItemViewCacheSize(10)
        binding.selectionRv.setHasFixedSize(true)
        binding.selectionRv.layoutManager = LinearLayoutManager(this)
        adapter = MusicAdapter(this ,MainActivity.MusicListMa, selectionActivity = true)
        binding.selectionRv.adapter = adapter
        binding.backBtnSA.setOnClickListener {
            finish()
        }
      binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            @SuppressLint("SuspiciousIndentation")
            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.musicListSearch = ArrayList()
                if(newText != null){
                    val userInput = newText.lowercase()
                    for (song in MainActivity.MusicListMa)
                        if (song.title.lowercase().contains(userInput))
                            MainActivity.musicListSearch.add(song)
                    MainActivity.search = true
                    adapter.updateMusicLiIst(searchList = MainActivity.musicListSearch)


                }
                return true
            }

        })
    }
}