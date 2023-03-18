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
package ru.stersh.retrosonic.feature.settings.servers.presentation

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.databinding.ItemListServerBinding

internal class ServersAdapter(
    private val onMenuClickListener: (item: ServerUi, menuItem: MenuItem) -> Boolean,
) : RecyclerView.Adapter<ServersAdapter.ViewHolder>() {
    private val data: MutableList<ServerUi> = mutableListOf()
    private var isOneItem = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemListServerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data.size

    fun swapDataset(newData: List<ServerUi>) {
        data.clear()
        data.addAll(newData)
        isOneItem = newData.size == 1
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemListServerBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ServerUi) {
            binding.title.text = item.title
            binding.address.text = item.url
            binding.menu.setOnClickListener { view ->
                PopupMenu(binding.root.context, view).apply {
                    inflate(R.menu.menu_server)
                    if (item.isActive) {
                        menu.removeItem(R.id.action_set_active)
                    }
                    if (isOneItem) {
                        menu.removeItem(R.id.action_delete)
                    }
                    setOnMenuItemClickListener { menuItem ->
                        return@setOnMenuItemClickListener onMenuClickListener(
                            data[bindingAdapterPosition],
                            menuItem,
                        )
                    }
                    show()
                }
            }
        }
    }
}
