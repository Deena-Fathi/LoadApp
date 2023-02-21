package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

/**
 * DetailActivity class provides functionality for displaying details about a file,
 * such as its title and status, and allows the user to confirm and return to the MainActivity
 */
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

        //Checks if the intent that started this activity contains the keys "success" and "title".
        //If the "success" key is present and its value is true, the statusValue text view is
        // set to the string resource with the ID R.string.success. If the "success" key is present
        // and its value is false or absent, the statusValue text view is set to the string resource
        // with the ID R.string.fail. If the "title" key is present, the fileNameValue text view is
        // set to the string value associated with that key.
        if (intent.hasExtra("success"))
            if (intent.getBooleanExtra("success", false)) {
                statusValue.text = getString(R.string.success)
            } else {
                statusValue.text = getString(R.string.fail)
            }
        if (intent.hasExtra("title")) {
            fileNameValue.text = intent.getStringExtra("title")
        }

        //Setting an OnClickListener on the confirm_button view. When the button is clicked,
        // it creates a new Intent that starts the MainActivity and sets the flags
        // Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK. These flags ensure
        // that any existing instances of the MainActivity are removed from the back stack before
        // starting a new instance of the activity. The startActivity method is then called to
        // start the MainActivity.
        confirm_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

}
