package com.empire.sente.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.palette.graphics.Palette


val Context.dataStore: DataStore<Preferences> by preferencesDataStore("APP_PREFERENCES")

fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) =
    Toast.makeText(this, text, duration).show()


fun String.removeSpaces() = this.filter { !it.isWhitespace() }


fun Activity.requestAudioPermission() = ActivityCompat.requestPermissions(
    this,
    Array(1) { Manifest.permission.RECORD_AUDIO },
    RECORD_REQUEST
)

fun Activity.hasAudioPermission(): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

fun View.fadeIn(duration: Long) {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    ViewCompat.animate(this).alpha(1f).setDuration(duration)
        .setListener(object : ViewPropertyAnimatorListener {
            override fun onAnimationStart(view: View) {
            }

            override fun onAnimationEnd(view: View) {
            }

            override fun onAnimationCancel(view: View) {}
        }).start()
}

fun View.fadeOut(duration: Long, delay: Long = 0) {
    this.alpha = 1f
    ViewCompat.animate(this).alpha(0f).setStartDelay(delay).setDuration(duration)
        .setListener(object :
            ViewPropertyAnimatorListener {
            override fun onAnimationStart(view: View) {
                @Suppress("DEPRECATION")
                view.isDrawingCacheEnabled = true
            }

            override fun onAnimationEnd(view: View) {
                view.visibility = View.INVISIBLE
                view.alpha = 0f
                @Suppress("DEPRECATION")
                view.isDrawingCacheEnabled = false
            }

            override fun onAnimationCancel(view: View) {}
        })
}

fun createPalette(uri: String?, view: View, vararg textViews: TextView) = uri?.let {
    if (it.isEmpty()) return@let
    val bmp = BitmapFactory.decodeFile(it)
    val palette: Palette = Palette.from(bmp).generate()
    val dominant = palette.dominantSwatch
    val lightVibrant = palette.lightVibrantSwatch
    val vibrant = palette.vibrantSwatch
    val muted = palette.mutedSwatch
    val lightMuted = palette.lightMutedSwatch

    val swatch: Palette.Swatch? = lightMuted ?: lightVibrant ?: muted ?: vibrant ?: dominant
    var i = 0
    for (tv in textViews)
        if (i == 0) swatch?.titleTextColor?.let { it1 ->
            tv.setTextColor(it1)
            i++

        } else swatch?.let { it1 ->
            tv.setTextColor(it1.bodyTextColor)
            i++
        }

    swatch?.rgb?.let { it1 -> view.setBackgroundColor(it1) }
}



