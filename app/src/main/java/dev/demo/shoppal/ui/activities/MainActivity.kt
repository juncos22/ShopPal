package dev.demo.shoppal.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.demo.shoppal.R
import dev.demo.shoppal.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharePreferences = getSharedPreferences(Constants.SHOPPAL_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharePreferences.getString(Constants.LOGGED_IN_USERNAME, "")
        tv_main.text = "Hello $username"
    }
}