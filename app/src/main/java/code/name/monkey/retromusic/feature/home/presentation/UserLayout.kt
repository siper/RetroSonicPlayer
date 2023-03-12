package code.name.monkey.retromusic.feature.home.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import code.name.monkey.retromusic.databinding.UserImageLayoutBinding

internal class UserLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1,
    defStyleRes: Int = -1
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var userImageBinding: UserImageLayoutBinding? = null

    init {
        userImageBinding = UserImageLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    }

    val userImage: ImageView
        get() = userImageBinding!!.userImage

    val titleWelcome : TextView
        get() = userImageBinding!!.titleWelcome
}