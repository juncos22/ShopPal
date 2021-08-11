package dev.demo.shoppal.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import dev.demo.shoppal.R

class GlideLoader(val context: Context) {
    fun loadUserPicture(image: Any, imageView: ImageView) {
        try {
            Glide.with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_profile_image_placeholder)
                .into(imageView)

        }catch (e: Exception) {
            Log.e("GlideLoader Error", e.message!!)
        }
    }
}