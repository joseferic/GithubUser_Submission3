package com.example.githubuser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Setting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // Menghilangkan bayangan dibawah Action Bar Tab
        supportActionBar?.elevation = 0F
        supportActionBar?.title = "Setting"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}