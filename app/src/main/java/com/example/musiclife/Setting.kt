package com.example.musiclife

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musiclife.databinding.ActivityAboutBinding
import com.example.musiclife.databinding.ActivitySettingBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.internal.StaticLayoutBuilderConfigurer

class Setting : AppCompatActivity() {
    lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title="Setting"
//        when(MainActivity.themeIndex){
//            0 -> binding.coolPinkTheme.setBackgroundColor(Color.YELLOW)
//            1 -> binding.coolBlueTheme.setBackgroundColor(Color.YELLOW)
//            2 -> binding.coolPurpleTheme.setBackgroundColor(Color.YELLOW)
//            3 -> binding.coolGreenTheme.setBackgroundColor(Color.YELLOW)
//            4 -> binding.coolBlackTheme.setBackgroundColor(Color.YELLOW)
//        }
//        binding.coolPinkTheme.setOnClickListener {saveTheme(0)}
//        binding.coolBlueTheme.setOnClickListener {saveTheme(1)}
//        binding.coolPurpleTheme.setOnClickListener {saveTheme(2)}
//        binding.coolGreenTheme.setOnClickListener {saveTheme(3)}
//        binding.coolBlackTheme.setOnClickListener {saveTheme(4)}
        binding.versionName.text = setVersion()
//        binding.sortSong.setOnClickListener {
//            var menuList = arrayOf("Recently Added","Song Title","File Size")
//            var currentSort = MainActivity.sortOrder
//            val bilder = MaterialAlertDialogBuilder(this)
//            bilder.setTitle("SortIng")
//                .setPositiveButton("OK") { _, _ ->
//                    val editor = getSharedPreferences("SORTING" , MODE_PRIVATE).edit()
//                    editor.putInt("sortOrder",currentSort)
//                    editor.apply()
//                }
//                .setSingleChoiceItems(menuList,currentSort){_,which ->
//                    currentSort = which
//                }
//
//            val customDialog = bilder.create()
//            customDialog.show()
//            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
//        }
    }
//    private fun saveTheme(index : Int){
//        if (MainActivity.themeIndex !=index){
//            val editor = getSharedPreferences("THEMES" , MODE_PRIVATE).edit()
//            editor.putInt("themeIndex",index)
//            editor.apply()
//            val bilder = MaterialAlertDialogBuilder(this)
//            bilder.setTitle("Apply Theme")
//                .setMessage("Do You Want Apple Theme?...")
//                .setPositiveButton("Yes") { _, _ ->
//                    exitApplication()
//                }
//                .setNegativeButton("No") { dialog, _ ->
//                    dialog.dismiss()
//                }
//            val customDialog = bilder.create()
//            customDialog.show()
//            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
//            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
//        }
//    }
    private fun setVersion():String{
        return "Version Name : 1.0"
    }
}