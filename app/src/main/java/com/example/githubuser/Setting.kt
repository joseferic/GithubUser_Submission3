package com.example.githubuser


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import com.example.githubuser.databinding.ActivitySettingBinding

class Setting : AppCompatActivity() {


    private var binding: ActivitySettingBinding? =null
    private lateinit var alarmReceiver: AlarmReceiver
    private val TAG = Setting::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        // Menghilangkan bayangan dibawah Action Bar Tab
        supportActionBar?.elevation = 0F
        supportActionBar?.title = "Setting"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        loadDataPreference()

        //alarm
        alarmReceiver = AlarmReceiver()

        //preferences



/*        binding!!.switchButton.setChecked(showExistingPreference())*/

        binding!!.switchButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // The switch is enabled/checked
                val repeatTime = "09:00"
                val repeatMessage = "Let's find popular user on Github!"
                alarmReceiver.setRepeatingAlarm(this, AlarmReceiver.TYPE_REPEATING,
                    repeatTime, repeatMessage)

                savePreference(binding!!.switchButton.isChecked)

                Log.d(TAG, "Reminder nya jadi")

            } else {
                // The switch is disabled
                Log.d(TAG, "Reminder nya gk jadi")
                alarmReceiver.cancelAlarm(this, AlarmReceiver.TYPE_REPEATING)
                savePreference(binding!!.switchButton.isChecked)
                // Set the app background color to light gray
            }
        }

    }


    private fun savePreference(statePreference: Boolean){
        val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply{
            putBoolean("BOOLEAN_KEY",statePreference )
        }.apply()
        if (statePreference == true){
            Toast.makeText(this, "Reminder saved", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this, "Reminder canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadDataPreference() {
        val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedBoolean = sharedPreferences.getBoolean("BOOLEAN_KEY", false)

        binding!!.switchButton.isChecked = savedBoolean
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}