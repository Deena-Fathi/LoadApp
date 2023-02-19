package com.udacity
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

//        if(intent?.extras != null){
//            fileNameValue.text = intent.getStringExtra("fileName")
//            statusValue.text = intent.getStringExtra("status")
//        }
//
//        confirm_button.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or  Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(intent)
//        }

        if (intent.hasExtra("success"))
            if (intent.getBooleanExtra("success", false)) {
                statusValue.text = getString(R.string.success)
            } else {
                statusValue.text = getString(R.string.fail)
            }
        if (intent.hasExtra("title")) {
            fileNameValue.text = intent.getStringExtra("title")
        }


        confirm_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }


}
