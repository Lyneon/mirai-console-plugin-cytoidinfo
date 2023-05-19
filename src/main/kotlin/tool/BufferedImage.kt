package com.lyneon.cytoidinfo.tool

import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO

fun BufferedImage.roundImage(): BufferedImage {
    if (this.width != this.height) {
        throw Exception("Only square image is supported")
    } else {
        val bi = BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB)
        val g = bi.createGraphics().enableAntiAlias()
        val roundShape = Ellipse2D.Double((0).toDouble(), (0).toDouble(), this.width.toDouble(), this.height.toDouble())
        g.clip = roundShape
        g.drawImage(this, 0, 0, this.width, this.height, null)
        g.dispose()
        
        return bi
    }
}

fun BufferedImage.toInputStream(): InputStream {
    val baos = ByteArrayOutputStream()
    ImageIO.write(this, "jpg", baos)
    val resultStream = ByteArrayInputStream(baos.toByteArray())
    this.flush()
    baos.close()
    return resultStream
}