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
package ru.stersh.retrosonic.adapter.backup

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.databinding.ItemListBackupBinding
import java.io.File

class BackupAdapter(
    val activity: FragmentActivity,
    var dataSet: MutableList<File>,
    val backupClickedListener: BackupClickedListener,
) : RecyclerView.Adapter<BackupAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemListBackupBinding.inflate(LayoutInflater.from(activity), parent, false),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.title.text = dataSet[position].nameWithoutExtension
    }

    override fun getItemCount(): Int = dataSet.size

    @SuppressLint("NotifyDataSetChanged")
    fun swapDataset(dataSet: List<File>) {
        this.dataSet = ArrayList(dataSet)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemListBackupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.menu.setOnClickListener { view ->
                val popupMenu = PopupMenu(activity, view)
                popupMenu.inflate(R.menu.menu_backup)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    return@setOnMenuItemClickListener backupClickedListener.onBackupMenuClicked(
                        dataSet[bindingAdapterPosition],
                        menuItem,
                    )
                }
                popupMenu.show()
            }
            itemView.setOnClickListener {
                backupClickedListener.onBackupClicked(dataSet[bindingAdapterPosition])
            }
        }
    }

    interface BackupClickedListener {
        fun onBackupClicked(file: File)

        fun onBackupMenuClicked(file: File, menuItem: MenuItem): Boolean
    }
}
