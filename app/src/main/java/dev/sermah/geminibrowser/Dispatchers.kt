package dev.sermah.geminibrowser

import kotlinx.coroutines.Dispatchers

object AppDispatchers {
    val IO = Dispatchers.IO
    val Main = Dispatchers.Main
    val Unconfined = Dispatchers.Unconfined
    val Default = Dispatchers.Default
}