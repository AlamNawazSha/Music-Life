package com.example.musiclife

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musiclife.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import androidx.appcompat.widget.SearchView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        lateinit var MusicListMa: ArrayList<MusicDataClass>
        lateinit var musicListSearch : ArrayList<MusicDataClass>
        var search: Boolean = false
//        var themeIndex : Int = 0

//        var sortOrder : Int =0
//        val sortingList = arrayOf(MediaStore.Audio.Media.DATE_ADDED+"DESC",MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.SIZE+"DESC")

    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val themeEditor = getSharedPreferences("THEMES" , MODE_PRIVATE)
//      themeIndex =  themeEditor.getInt("themeIndex",0)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        rvAdapter()
        requestruntimePremetion()


        Favorite.favouriteSong = ArrayList()
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
       val jsonString = editor.getString("FavouriteSongs",null)
        val typeTpken = object : TypeToken<ArrayList<MusicDataClass>>(){}.type
        if (jsonString != null){
            val data:ArrayList<MusicDataClass> = GsonBuilder().create().fromJson(jsonString,typeTpken)
            Favorite.favouriteSong.addAll(data)
        }
        PlayListActivity.musicPlayList = MusicPlayList()
        val jsonStringPlayList = editor.getString("MusicPlayList",null)
        if (jsonStringPlayList != null){
            val dataPlayList:MusicPlayList = GsonBuilder().create().fromJson(jsonStringPlayList,MusicPlayList::class.java)
            PlayListActivity.musicPlayList= dataPlayList
        }



        binding.totalsong.text = "Total Song = " + musicAdapter.itemCount
        binding.favotitesbtn.setOnClickListener {
            startActivity(Intent(this, Favorite::class.java))
        }
        binding.playlistbtn.setOnClickListener {
            startActivity(Intent(this, PlayListActivity::class.java))
        }
        binding.shufflebtn.setOnClickListener {
            val intent = Intent(this, Player::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MainActivity")
            startActivity(intent)
        }
        binding.naveView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.feedback -> startActivity(Intent(this,FeedBack::class.java))
                R.id.setting ->  startActivity(Intent(this,Setting::class.java))
                R.id.about -> startActivity(Intent(this,About::class.java))
                R.id.exit -> {
                    val bilder = MaterialAlertDialogBuilder(this)
                    bilder.setTitle("Exit")
                        .setMessage("Do You Want Close App")
                        .setPositiveButton("Yes") { _, _ ->
                            exitApplication()
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
            true
        }


    }

    private fun requestruntimePremetion(){
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
        PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),21)

        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==21){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "PerMission Granted ", Toast.LENGTH_SHORT).show()
                rvAdapter()

            }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),21)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }
     private fun rvAdapter(){
         search = false
//         val sortEditer = getSharedPreferences("SORTING" , MODE_PRIVATE)
//         sortOrder = sortEditer.getInt("sortOrder" ,0)
         MusicListMa = getAllAudio()
         binding.musicRV.setHasFixedSize(true)
         binding.musicRV.setItemViewCacheSize(15)
         binding.musicRV.layoutManager = LinearLayoutManager(this)
         musicAdapter = MusicAdapter(this , MusicListMa)
         binding.musicRV.adapter = musicAdapter
     }

    @SuppressLint("Recycle", "Range", "SuspiciousIndentation")
    private fun getAllAudio():ArrayList<MusicDataClass>{
        val tempList = ArrayList<MusicDataClass>()
        val selector = MediaStore.Audio.Media.IS_MUSIC + " !=0"
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM_ID
            )
        val cursor= this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selector, null, MediaStore.Audio.Media.DATE_ADDED,null)

        if (cursor != null){
            if (cursor.moveToFirst())
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idc = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()

                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC =Uri.withAppendedPath(uri ,albumIdC).toString()
                    val music = MusicDataClass(id = idc, title = titleC, album = albumC , artist = artistC , duration = durationC, path = pathC,artUri = artUriC)
                    val file = File(music.path)
                    if (file.exists()) {
                        tempList.add(music)
                    }
                }while (cursor.moveToNext())
                cursor.close()

        }
        return tempList
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!Player.isPlaying && Player.musicService != null){
          exitApplication()
        }



    }

    override fun onResume() {
        super.onResume()
        //for storing Favourite Song data
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(Favorite.favouriteSong)
        editor.putString("FavouriteSongs",jsonString)
        val jsonStringPlayList = GsonBuilder().create().toJson(PlayListActivity.musicPlayList)
        editor.putString("MusicPlayList",jsonStringPlayList)
        editor.apply()

//        val sortEditer = getSharedPreferences("SORTING" , MODE_PRIVATE)
//      val sortValue = sortEditer.getInt("sortOrder" ,0)
//        if (sortOrder != sortValue){
//            sortOrder = sortValue
//            MusicListMa =getAllAudio()
//            musicAdapter.updateMusicLiIst(MusicListMa)
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu,menu)
       val searchView = menu?.findItem(R.id.search)!!.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            @SuppressLint("SuspiciousIndentation")
            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
                 if(newText != null){
                     val userInput = newText.lowercase()
                     for (song in MusicListMa)
                         if (song.title.lowercase().contains(userInput))
                             musicListSearch.add(song)
                             search = true
                             musicAdapter.updateMusicLiIst(searchList = musicListSearch)


                 }
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }



}