package com.majid2851.a11yinspector

import com.majid2851.a11yinspector.scanner.Contrast
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContrastTest {

    @Test
    fun `luminance of white is one`() {
        assertEquals(1.0, Contrast.relativeLuminance(255, 255, 255), 0.001)
    }

    @Test
    fun `luminance of black is zero`() {
        assertEquals(0.0, Contrast.relativeLuminance(0, 0, 0), 0.001)
    }

    @Test
    fun `black on white is maximum ratio`() {
        val white = Contrast.relativeLuminance(255, 255, 255)
        val black = Contrast.relativeLuminance(0, 0, 0)
        assertEquals(21.0, Contrast.ratio(white, black), 0.1)
    }

    @Test
    fun `estimate returns null with too few samples`() {
        assertNull(Contrast.estimateRatioFromSamples(doubleArrayOf(0.0, 1.0)))
    }

    @Test
    fun `estimate detects high contrast from samples`() {
        val samples = DoubleArray(100) { if (it < 50) 0.0 else 1.0 }
        val ratio = Contrast.estimateRatioFromSamples(samples)!!
        assertTrue("expected high ratio but was $ratio", ratio > 15.0)
    }

    @Test
    fun `estimate detects low contrast from samples`() {
        val samples = DoubleArray(100) { if (it < 50) 0.35 else 0.45 }
        val ratio = Contrast.estimateRatioFromSamples(samples)!!
        assertTrue("expected low ratio but was $ratio", ratio < 2.0)
    }
}
