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
package ru.stersh.retrosonic.feature.mylibrary.presentation

import ru.stersh.retrosonic.databinding.FragmentHomeBinding

class MyLibraryBinding(
    homeBinding: FragmentHomeBinding,
) {
    val root = homeBinding.root
    val container = homeBinding.container
    val contentContainer = homeBinding.contentContainer
    val appBarLayout = homeBinding.appBarLayout
    val toolbar = homeBinding.toolbar
    val userImage = homeBinding.imageLayout.userImage
    val recyclerView = homeBinding.homeContent.recyclerView
    val titleWelcome = homeBinding.imageLayout.titleWelcome
    val appNameText = homeBinding.appNameText
}
