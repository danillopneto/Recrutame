package ufg.go.br.recrutame.Util

import android.content.res.Resources
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.Display
import android.os.Build
import android.view.WindowManager



class Utils {
    companion object {
        fun dpToPx(dp: Int): Int {
            return (dp * Resources.getSystem().getDisplayMetrics().density).toInt()
        }

        fun getDisplaySize(windowManager: WindowManager): Point {
            try {
                if (Build.VERSION.SDK_INT > 16) {
                    val display = windowManager.defaultDisplay
                    val displayMetrics = DisplayMetrics()
                    display.getMetrics(displayMetrics)
                    return Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
                } else {
                    return Point(0, 0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return Point(0, 0)
            }

        }
    }
}