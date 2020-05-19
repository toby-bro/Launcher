package com.finnmglas.launcher

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_firststartup.*

// Taken from https://stackoverflow.com/questions/47293269
fun View.blink(
    times: Int = Animation.INFINITE,
    duration: Long = 1000L,
    offset: Long = 20L,
    minAlpha: Float = 0.2f,
    maxAlpha: Float = 1.0f,
    repeatMode: Int = Animation.REVERSE
) {
    startAnimation(AlphaAnimation(minAlpha, maxAlpha).also {
        it.duration = duration
        it.startOffset = offset
        it.repeatMode = repeatMode
        it.repeatCount = times
    })
}

class FirstStartupActivity : AppCompatActivity(){

    /** Variables for this activity */

    private var menuNumber = 0
    private var defaultApps = mutableListOf<String>()

    /** Activity Lifecycle functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Flags
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_firststartup)

        hintText.blink() // animate
        loadMenu(this)

        val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        defaultApps = resetSettings(sharedPref, this) // UP, DOWN, RIGHT, LEFT, VOLUME_UP, VOLUME_DOWN
    }

    /** Touch- and Key-related functions to navigate */

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            menuNumber++
            loadMenu(this)
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_BACK){
            menuNumber--

            // prevent negative indices
            if (menuNumber < 0) menuNumber = 0

            loadMenu(this)
        }
        return true
    }

    fun clickAnywhere(view: View){
        menuNumber++
        loadMenu(this)
    }

    /** Touch- and Key-related functions to navigate */

    private fun loadMenu(context :Context) { // Context needed for packageManager

        val intro = resources.getStringArray(R.array.intro)

        if (menuNumber < intro.size){
            val entry = intro[menuNumber].split("|").toTypedArray() //heading|infoText|hintText|size

            heading.text = entry[0]
            if (entry[4] == "1")infoText.text = String.format(entry[1],
                defaultApps[0], defaultApps[1], defaultApps[2], defaultApps[3], defaultApps[4], defaultApps[5])
            else infoText.text = entry[1]
            hintText.text = entry[2]
            infoText.setTextSize(TypedValue.COMPLEX_UNIT_SP, entry[3].toFloat())

        } else { // End intro
            val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putBoolean("startedBefore", true) // never run this again
            editor.putLong("firstStartup", System.currentTimeMillis() / 1000L) // record first startup timestamp
            editor.apply()

            finish()
        }
    }
}
