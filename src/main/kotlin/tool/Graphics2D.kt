package com.lyneon.cytoidinfo.tool

import java.awt.Graphics2D
import java.awt.RenderingHints

fun Graphics2D.enableAntiAlias(): Graphics2D {
    this.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    this.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
    this.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    return this
}