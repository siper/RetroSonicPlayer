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

import android.view.View
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.stersh.retrosonic.databinding.ItemDiscoverAlbumBinding
import ru.stersh.retrosonic.databinding.ItemSectionBinding
import ru.stersh.retrosonic.glide.GlideApp

internal fun albumDelegate(itemClickedListener : (View, AlbumUi) -> Unit) = adapterDelegateViewBinding<AlbumUi, MyLibraryItemUi, ItemDiscoverAlbumBinding>(
    { layoutInflater, root -> ItemDiscoverAlbumBinding.inflate(layoutInflater, root, false) }
) {
    binding.root.setOnClickListener {
        itemClickedListener(binding.image, item)
    }
    bind {
        binding.title.text = item.title
        GlideApp
            .with(context)
            .load(item.coverUrl)
            .playlistOptions()
            .into(binding.image)
    }
}

internal fun sectionDelegate() = adapterDelegateViewBinding<SectionUi, MyLibraryItemUi, ItemSectionBinding>(
    { layoutInflater, root -> ItemSectionBinding.inflate(layoutInflater, root, false) }
) {
    bind {
        binding.title.setText(item.titleId)
    }
}
