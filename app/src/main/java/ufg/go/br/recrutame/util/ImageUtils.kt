package ufg.go.br.recrutame.util

import android.widget.ImageView
import com.squareup.picasso.Picasso


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