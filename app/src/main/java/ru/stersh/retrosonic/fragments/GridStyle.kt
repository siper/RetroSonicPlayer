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
package ru.stersh.retrosonic.fragments

import androidx.annotation.LayoutRes
import ru.stersh.retrosonic.R

enum class GridStyle constructor(
    @param:LayoutRes @field:LayoutRes
    val layoutResId: Int,
    val id: Int,
) {
    Grid(R.layout.item_grid, 0),
    Card(R.layout.item_card, 1),
    ColoredCard(R.layout.item_card_color, 2),
    Circular(R.layout.item_grid_circle, 3),
    Image(R.layout.image, 4),
    GradientImage(R.layout.item_image_gradient, 5),
}
