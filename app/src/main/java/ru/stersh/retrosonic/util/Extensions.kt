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

import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import ru.stersh.retrosonic.R

val Fragment.defaultNavOptions: NavOptions by lazy {
    navOptions {
        launchSingleTop = false
        anim {
            enter = R.anim.retro_fragment_open_enter
            exit = R.anim.retro_fragment_open_exit
            popEnter = R.anim.retro_fragment_close_enter
            popExit = R.anim.retro_fragment_close_exit
        }
    }
}
