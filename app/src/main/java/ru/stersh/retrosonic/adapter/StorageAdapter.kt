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
package ru.stersh.retrosonic.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.stersh.retrosonic.R
import java.io.File

class StorageAdapter(
    val storageList: List<Storage>,
    val storageClickListener: StorageClickListener,
) :
    RecyclerView.Adapter<StorageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_storage,
                parent,
                false,
            ),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(storageList[position])
    }

    override fun getItemCount(): Int {
        return storageList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)

        fun bindData(storage: Storage) {
            title.text = storage.title
        }

        init {
            itemView.setOnClickListener { storageClickListener.onStorageClicked(storageList[bindingAdapterPosition]) }
        }
    }
}

interface StorageClickListener {
    fun onStorageClicked(storage: Storage)
}

class Storage {
    lateinit var title: String
    lateinit var file: File
}
