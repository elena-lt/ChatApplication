package com.example.chatapplication.utils

import android.graphics.Bitmap
import androidx.databinding.BindingAdapter
import com.example.chatapplication.R
import com.google.android.material.imageview.ShapeableImageView

@BindingAdapter(value = ["setImageBitmap"])
fun ShapeableImageView.bindImageBitmap(bitmap: Bitmap?) {
    if (bitmap != null) {
        this.setImageBitmap(bitmap)
    }else{
        this.setImageResource(R.drawable.user_default_img)
    }
}