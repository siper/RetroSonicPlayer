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
package ru.stersh.retrosonic.feature.home.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import ru.stersh.retrosonic.databinding.UserImageLayoutBinding

internal class UserLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1,
    defStyleRes: Int = -1,
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var userImageBinding: UserImageLayoutBinding? = null

    init {
        userImageBinding = UserImageLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    }

    val userImage: ImageView
        get() = userImageBinding!!.userImage

    val titleWelcome: TextView
        get() = userImageBinding!!.titleWelcome
}
