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
package ru.stersh.retrosonic.activities.bugreport.model.github

import android.text.TextUtils

class GithubLogin {
    val apiToken: String?
    val password: String?
    val username: String?

    constructor(username: String?, password: String?) {
        this.username = username
        this.password = password
        apiToken = null
    }

    constructor(apiToken: String?) {
        username = null
        password = null
        this.apiToken = apiToken
    }

    fun shouldUseApiToken(): Boolean {
        return TextUtils.isEmpty(username) || TextUtils.isEmpty(password)
    }
}
