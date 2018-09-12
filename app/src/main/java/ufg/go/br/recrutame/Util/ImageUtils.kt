package ufg.go.br.recrutame.Util

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.graphics.Bitmap
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator


class ImageUtils {

    companion object{
        fun displayRoundImageFromUrl(url: String, imageView: ImageView) {
            Picasso.get()
                    .load(url)
                    .fit()
                    .centerCrop()
                    .transform(PicassoCircleTransformation())
                    .into(imageView)
        }
    }
}