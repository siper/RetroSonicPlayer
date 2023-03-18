/*
 * Copyright (c) 2020 Retro Sonic contributors.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package ru.stersh.retrosonic.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import ru.stersh.retrosonic.BuildConfig
import ru.stersh.retrosonic.extensions.showToast

fun Activity.maybeShowAnnoyingToasts() {
    if (BuildConfig.APPLICATION_ID != "ru.stersh.retrosonic" &&
        BuildConfig.APPLICATION_ID != "ru.stersh.retrosonic.debug" &&
        BuildConfig.APPLICATION_ID != "ru.stersh.retrosonic.normal"
    ) {
        if (BuildConfig.DEBUG) {
            // Log these things to console, if the plagiarizer even cares to check it
            Log.d("Retro Sonic", "What are you doing with your life?")
            Log.d("Retro Sonic", "Stop copying apps and make use of your brain.")
            Log.d("Retro Sonic", "Stop doing this or you will end up straight to hell.")
            Log.d("Retro Sonic", "To the boiler room of hell. All the way down.")
        } else {
            showToast("Warning! This is a copy of Retro Sonic Player", Toast.LENGTH_LONG)
            showToast("Instead of using this copy by a dumb person who didn't even bother to remove this code.", Toast.LENGTH_LONG)
            showToast("Support us by downloading the original version from Play Store.", Toast.LENGTH_LONG)
            val packageName = "ru.stersh.retrosonic"
            try {
                startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri()))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$packageName".toUri()))
            }
        }
    }
}
