package com.lyneon.cytoidinfo.tool

import java.awt.Font
import java.awt.Toolkit

fun Font.setPixelSize(pixelSize:Int): Font {
    val dpi = Toolkit.getDefaultToolkit().screenResolution
    return this.deriveFont(pixelSize.toFloat()/dpi.toFloat()/72.0f)
}