/*
 * Copyright (c) 2020 Hemanth Savarla.
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
package code.name.monkey.retromusic.adapter.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.feature.main.presentation.MainActivity
import code.name.monkey.retromusic.fragments.AlbumCoverStyle
import code.name.monkey.retromusic.glide.GlideApp
import code.name.monkey.retromusic.glide.RetroMusicColoredTarget
import code.name.monkey.retromusic.misc.CustomFragmentStatePagerAdapter
import code.name.monkey.retromusic.util.PreferenceUtil
import code.name.monkey.retromusic.util.color.MediaNotificationProcessor
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED

class AlbumCoverPagerAdapter(
    fragmentManager: FragmentManager,
    private val dataSet: List<String>
) : CustomFragmentStatePagerAdapter(fragmentManager) {

    private var currentColorReceiver: AlbumCoverFragment.ColorReceiver? = null
    private var currentColorReceiverPosition = -1

    override fun getItem(position: Int): Fragment {
        return AlbumCoverFragment.newInstance(dataSet[position])
    }

    override fun getCount(): Int {
        return dataSet.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val o = super.instantiateItem(container, position)
        if (currentColorReceiver != null && currentColorReceiverPosition == position) {
            receiveColor(currentColorReceiver!!, currentColorReceiverPosition)
        }
        return o
    }

    /**
     * Only the latest passed [AlbumCoverFragment.ColorReceiver] is guaranteed to receive a
     * response
     */
    fun receiveColor(colorReceiver: AlbumCoverFragment.ColorReceiver, position: Int) {

        if (getFragment(position) is AlbumCoverFragment) {
            val fragment = getFragment(position) as AlbumCoverFragment
            currentColorReceiver = null
            currentColorReceiverPosition = -1
            fragment.receiveColor(colorReceiver, position)
        } else {
            currentColorReceiver = colorReceiver
            currentColorReceiverPosition = position
        }
    }

    class AlbumCoverFragment : Fragment() {
        private var isColorReady: Boolean = false
        private lateinit var color: MediaNotificationProcessor
        private var coverArtUrl: String? = null
        private var colorReceiver: ColorReceiver? = null
        private var request: Int = 0
        private val mainActivity get() = activity as MainActivity

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            if (arguments != null) {
                coverArtUrl = requireArguments().getString(COVER_ART_URL_ARG)
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(getLayoutWithPlayerTheme(), container, false)
            view.setOnClickListener {
                if (mainActivity.getBottomSheetBehavior().state == STATE_EXPANDED) {
                    showLyricsDialog()
                }
            }
            return view
        }

        private fun showLyricsDialog() {
//            lifecycleScope.launch(Dispatchers.IO) {
//                val data: String? = MusicUtil.getLyrics(song)
//                withContext(Dispatchers.Main) {
//                    MaterialAlertDialogBuilder(
//                        requireContext(),
//                        com.google.android.material.R.style.ThemeOverlay_MaterialComponents_Dialog_Alert
//                    ).apply {
//                        setTitle(song.title)
//                        setMessage(if (data.isNullOrEmpty()) "No lyrics found" else data)
//                        setNegativeButton(R.string.synced_lyrics) { _, _ ->
//                            goToLyrics(requireActivity())
//                        }
//                        show()
//                    }
//                }
//            }
        }

        private fun getLayoutWithPlayerTheme(): Int {
            return if (PreferenceUtil.isCarouselEffect) {
                R.layout.fragment_album_carousel_cover
            } else {
                when (PreferenceUtil.albumCoverStyle) {
                    AlbumCoverStyle.Normal -> R.layout.fragment_album_cover
                    AlbumCoverStyle.Flat -> R.layout.fragment_album_flat_cover
                    AlbumCoverStyle.Circle -> R.layout.fragment_album_circle_cover
                    AlbumCoverStyle.Card -> R.layout.fragment_album_card_cover
                    AlbumCoverStyle.Full -> R.layout.fragment_album_full_cover
                    AlbumCoverStyle.FullCard -> R.layout.fragment_album_full_card_cover
                }
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            loadAlbumCover(albumCover = view.findViewById(R.id.player_image))
        }

        override fun onDestroyView() {
            super.onDestroyView()
            colorReceiver = null
        }

        private fun loadAlbumCover(albumCover: ImageView) {
            GlideApp
                .with(this)
                .asBitmapPalette()
                .load(coverArtUrl)
                .dontAnimate()
                .into(object : RetroMusicColoredTarget(albumCover) {
                    override fun onColorReady(colors: MediaNotificationProcessor) {
                        setColor(colors)
                    }
                })
        }

        private fun setColor(color: MediaNotificationProcessor) {
            this.color = color
            isColorReady = true
            if (colorReceiver != null) {
                colorReceiver!!.onColorReady(color, request)
                colorReceiver = null
            }
        }

        internal fun receiveColor(colorReceiver: ColorReceiver, request: Int) {
            if (isColorReady) {
                colorReceiver.onColorReady(color, request)
            } else {
                this.colorReceiver = colorReceiver
                this.request = request
            }
        }

        interface ColorReceiver {
            fun onColorReady(color: MediaNotificationProcessor, request: Int)
        }

        companion object {

            private const val COVER_ART_URL_ARG = "coverArtUrl"

            fun newInstance(coverArtUrl: String): AlbumCoverFragment {
                val frag = AlbumCoverFragment()
                frag.arguments = bundleOf(COVER_ART_URL_ARG to coverArtUrl)
                return frag
            }
        }
    }

    companion object {
        val TAG: String = AlbumCoverPagerAdapter::class.java.simpleName
    }
}
