package com.example.finalprojectaibetter

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop

class RoundImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    fun loadImage(url: String) {
        Glide.with(context)
            .load(url)
            .transform(CircleCrop()) // applies the round form in the corners
            .into(this)
    }
}