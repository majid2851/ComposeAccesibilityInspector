package com.majid2851.a11yinspector.scanner

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import androidx.core.view.drawToBitmap

/**
 * A [LuminanceSampler] backed by a software bitmap captured from a [View].
 *
 * Capturing is only appropriate for debug builds; it renders the whole Compose
 * view into an ARGB bitmap once per scan and then samples pixels on demand.
 */
class BitmapLuminanceSampler private constructor(
    private val bitmap: Bitmap,
) : LuminanceSampler {

    override fun sample(left: Int, top: Int, right: Int, bottom: Int): DoubleArray {
        val l = left.coerceIn(0, bitmap.width - 1)
        val t = top.coerceIn(0, bitmap.height - 1)
        val r = right.coerceIn(l + 1, bitmap.width)
        val b = bottom.coerceIn(t + 1, bitmap.height)

        val regionWidth = r - l
        val regionHeight = b - t
        if (regionWidth <= 0 || regionHeight <= 0) return DoubleArray(0)

        // Cap total samples so large regions stay cheap.
        val stepX = maxOf(1, regionWidth / MAX_SAMPLES_PER_AXIS)
        val stepY = maxOf(1, regionHeight / MAX_SAMPLES_PER_AXIS)

        val values = ArrayList<Double>()
        var y = t
        while (y < b) {
            var x = l
            while (x < r) {
                val pixel = bitmap.getPixel(x, y)
                if (Color.alpha(pixel) > 0) {
                    values += Contrast.relativeLuminance(
                        Color.red(pixel),
                        Color.green(pixel),
                        Color.blue(pixel),
                    )
                }
                x += stepX
            }
            y += stepY
        }
        return values.toDoubleArray()
    }

    companion object {
        private const val MAX_SAMPLES_PER_AXIS = 24

        /**
         * Captures [view] into a bitmap sampler, returning null if the view has
         * no size or capture fails.
         */
        fun capture(view: View): BitmapLuminanceSampler? {
            if (view.width <= 0 || view.height <= 0) return null
            return runCatching {
                BitmapLuminanceSampler(view.drawToBitmap(Bitmap.Config.ARGB_8888))
            }.getOrNull()
        }
    }
}
