package ru.stersh.apisonic

import okio.IOException

data class ApiSonicError(val code: Int, override val message: String): IOException()