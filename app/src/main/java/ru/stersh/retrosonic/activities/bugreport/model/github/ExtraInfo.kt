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

class ExtraInfo {
    private val extraInfo: MutableMap<String, String> = LinkedHashMap()
    fun put(key: String, value: String) {
        extraInfo[key] = value
    }

    fun put(key: String, value: Boolean) {
        extraInfo[key] = value.toString()
    }

    fun put(key: String, value: Double) {
        extraInfo[key] = value.toString()
    }

    fun put(key: String, value: Float) {
        extraInfo[key] = value.toString()
    }

    fun put(key: String, value: Long) {
        extraInfo[key] = value.toString()
    }

    fun put(key: String, value: Int) {
        extraInfo[key] = value.toString()
    }

    fun put(key: String, value: Any) {
        extraInfo[key] = value.toString()
    }

    fun remove(key: String) {
        extraInfo.remove(key)
    }

    fun toMarkdown(): String {
        if (extraInfo.isEmpty()) {
            return ""
        }
        val output = StringBuilder()
        output.append(
            """
    Extra info:
    ---
    <table>

            """.trimIndent(),
        )
        for (key in extraInfo.keys) {
            output
                .append("<tr><td>")
                .append(key)
                .append("</td><td>")
                .append(extraInfo[key])
                .append("</td></tr>\n")
        }
        output.append("</table>\n")
        return output.toString()
    }
}
