package com.example.musiclife

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musiclife.databinding.ActivityAboutBinding
import com.example.musiclife.databinding.ActivityFeedBackBinding
import com.example.musiclife.databinding.ActivitySettingBinding

class About : AppCompatActivity() {
    lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAboutBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = "About"
        binding.aboutText.text = aboutText()

    }
    private fun aboutText(): String{
        return "Developed By : Alam Nawaz Sha" +
                "If you Want to provied feedBack , I Will love to hear that"
    }
}