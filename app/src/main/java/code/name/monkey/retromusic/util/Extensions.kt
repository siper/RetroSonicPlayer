package code.name.monkey.retromusic.util

import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import code.name.monkey.retromusic.R

val Fragment.defaultNavOptions: NavOptions by lazy {
    navOptions {
        launchSingleTop = false
        anim {
            enter = R.anim.retro_fragment_open_enter
            exit = R.anim.retro_fragment_open_exit
            popEnter = R.anim.retro_fragment_close_enter
            popExit = R.anim.retro_fragment_close_exit
        }
    }
}