package com.example.musiclife

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musiclife.databinding.ActivityFeedBackBinding
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class FeedBack : AppCompatActivity() {
    lateinit var binding : ActivityFeedBackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "FeedBack"

        binding.senFeedback.setOnClickListener {
            val feedback = binding.message.text.toString()+"\n" + binding.email.text.toString()
            val subject = binding.topi.text.toString()
            val userName = "alamalam83802@gmail.com"
            val pass = "alamalam83802@815001"
            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (feedback.isNotEmpty() && subject.isNotEmpty() && (cm.activeNetworkInfo?.isConnectedOrConnecting == true)) {
                Thread {
                    try {
                        val properties = Properties()
                        properties["mail.smtp.auth"] = "true"
                        properties["mail.smtp.starttls.enable"] = "smpt.gmail.com"
                        properties["mail.smtp.port"] = "587"

                        val session = Session.getInstance(properties, object : Authenticator() {
                            override fun getPasswordAuthentication(): PasswordAuthentication {
                                return PasswordAuthentication(userName, pass)
                            }
                        })
                        val mail = MimeMessage(session)
                        mail.subject = subject
                        mail.setText(feedback)
                        mail.setFrom(InternetAddress(userName))
                        mail.setRecipients(
                            Message.RecipientType.TO,
                            InternetAddress.parse(userName)
                        )
                        Transport.send(mail)
                    } catch (e: Exception) {
                        return@Thread
                    }
                }.start()
                Toast.makeText(this, "Thanks For Feedback", Toast.LENGTH_LONG).show()
                finish()
            }
            else {
                Toast.makeText(this,"Went something Wrong",Toast.LENGTH_LONG).show()
            }
        }

    }
}