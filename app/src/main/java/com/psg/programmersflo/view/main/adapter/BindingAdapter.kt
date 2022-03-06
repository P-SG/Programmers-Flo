package com.psg.programmersflo.view.main.adapter

import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("playing")
fun bindPlaying(view: TextView, playing: Boolean) {
    if (playing) view.setTypeface(Typeface.SERIF,Typeface.BOLD) else view.setTypeface(Typeface.SERIF,Typeface.NORMAL)
}

@BindingAdapter("image")
fun bindImage(view: ImageView, uri: String?) { //imageView에 값을 넣기위한 Adapter Layout단에서 넣어주는 값이 uri로 들어옴
    Glide.with(view.context)
        .load(uri)
        .into(view)
}

