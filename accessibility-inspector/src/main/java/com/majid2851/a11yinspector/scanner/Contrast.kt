package com.majid2851.a11yinspector.scanner

import kotlin.math.pow

/**
 * WCAG color-contrast math. All functions are pure so they can be unit tested
 * without an Android runtime.
 */
object Contrast {

    /**
     * Relative luminance of an sRGB color per the WCAG 2.1 definition.
     *
     * @param r red channel in 0..255.
     * @param g green channel in 0..255.
     * @param b blue channel in 0..255.
     */
    fun relativeLuminance(r: Int, g: Int, b: Int): Double {
        val rl = linearize(r / 255.0)
        val gl = linearize(g / 255.0)
        val bl = linearize(b / 255.0)
        return 0.2126 * rl + 0.7152 * gl + 0.0722 * bl
    }

    private fun linearize(channel: Double): Double =
        if (channel <= 0.03928) channel / 12.92 else ((channel + 0.055) / 1.055).pow(2.4)

    /**
     * Contrast ratio between two relative luminance values, always >= 1.0.
     */
    fun ratio(luminanceA: Double, luminanceB: Double): Double {
        val lighter = maxOf(luminanceA, luminanceB)
        val darker = minOf(luminanceA, luminanceB)
        return (lighter + 0.05) / (darker + 0.05)
    }

    /**
     * Estimates the foreground/background contrast ratio from a set of sampled
     * luminances by comparing a low and high percentile. This is intentionally
     * approximate: it assumes text and background dominate the sampled region.
     *
     * @return the estimated ratio, or null when there are too few samples.
     */
    fun estimateRatioFromSamples(luminances: DoubleArray, lowPercentile: Double = 0.1): Double? {
        if (luminances.size < MIN_SAMPLES) return null
        val sorted = luminances.sortedArray()
        val low = percentile(sorted, lowPercentile)
        val high = percentile(sorted, 1.0 - lowPercentile)
        return ratio(low, high)
    }

    private fun percentile(sorted: DoubleArray, fraction: Double): Double {
        val clamped = fraction.coerceIn(0.0, 1.0)
        val index = (clamped * (sorted.size - 1)).toInt().coerceIn(0, sorted.size - 1)
        return sorted[index]
    }

    private const val MIN_SAMPLES = 16
}

/**
 * Supplies sampled luminance values for a rectangular region, used by the
 * contrast rule. Implementations back onto a bitmap captured from the screen.
 */
fun interface LuminanceSampler {
    /**
     * @param left pixel bounds of the region to sample.
     * @return relative luminance values for a subset of pixels in the region.
     */
    fun sample(left: Int, top: Int, right: Int, bottom: Int): DoubleArray
}
