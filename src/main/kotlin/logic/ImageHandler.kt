package com.lyneon.cytoidinfo.logic

import com.lyneon.cytoidinfo.CytoidInfo
import com.lyneon.cytoidinfo.model.B30Records
import com.lyneon.cytoidinfo.model.Character
import com.lyneon.cytoidinfo.model.PlayerProfile
import com.lyneon.cytoidinfo.tool.enableAntiAlias
import com.lyneon.cytoidinfo.tool.fix
import com.lyneon.cytoidinfo.tool.roundImage
import com.lyneon.cytoidinfo.tool.setPixelSize
import kotlinx.coroutines.*
import java.awt.Color
import java.awt.Font
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO

object ImageHandler {
    fun getProfileImage(profile: PlayerProfile): BufferedImage {
        System.setProperty("java.awt.headless", "true")
        val bi = BufferedImage(2000, 1000, BufferedImage.TYPE_INT_RGB)
        val g = bi.createGraphics().enableAntiAlias()
        
        g.drawImage(
            when (profile.character.name) {
                Character.hancho -> ImageIO.read(CytoidInfo.getResourceAsStream("pictures/Hancho_bg.jpg"))
                Character.marySue -> ImageIO.read(CytoidInfo.getResourceAsStream("pictures/MarySue_bg.png"))
                else -> ImageIO.read(CytoidInfo.getResourceAsStream("pictures/MarySue_bg.png"))
            }, 0, 0, 2000, 1000, null
        )
        g.color = Color(0, 0, 0, 128)
        g.fillRect(0, 0, 2000, 1000)
        
        g.drawImage(
            ImageIO.read(URL(profile.user.avatar.medium)).roundImage(),
            50, 50, 200, 200, null
        )
        
        g.font = Font(Font.SANS_SERIF, Font.TRUETYPE_FONT, 1).setPixelSize(100)
        g.color = Color.WHITE
        g.drawString(profile.user.uid, 300, 150)
        g.font = g.font.setPixelSize(40)
        g.drawString("等级 ${profile.exp.currentLevel}", 300, 250)
        
        g.drawImage(
            when (profile.character.name) {
                Character.hancho -> ImageIO.read(CytoidInfo.getResourceAsStream("pictures/Hancho.png"))
                Character.marySue -> ImageIO.read(CytoidInfo.getResourceAsStream("pictures/MarySue.png"))
                else -> ImageIO.read(CytoidInfo.getResourceAsStream("pictures/Sayaka.png"))
            }, 1000, 0, 1000, 1000, null
        )
        
        g.drawImage(getActivitiesImage(profile), 50, 300, 950, 650, null)
        
        g.dispose()
        return bi
    }
    
    suspend fun getB30RecordsImage(profile: PlayerProfile): BufferedImage {
        val records = NetRequest.getB30Records(profile.user.uid, 30)
        val bi = BufferedImage(6160, 3210, BufferedImage.TYPE_INT_RGB)
        val g = bi.createGraphics().enableAntiAlias()
        coroutineScope {
            val bg = withContext(Dispatchers.IO) {
                ImageIO.read(CytoidInfo.getResourceAsStream("pictures/bg_blur.jpg"))
            }
            g.drawImage(bg, 0, 0, null)

//            绘制头像
            val avatar = withContext(Dispatchers.IO) {
                ImageIO.read(URL(profile.user.avatar.medium))
            }.roundImage()
            g.drawImage(avatar, 50, 50, 500, 500, null)
            avatar.flush()

//            绘制昵称
//            g.font = Font(Font.SANS_SERIF, Font.PLAIN, 200)
            g.font = withContext(Dispatchers.IO) {
                Font.createFont(
                    Font.TRUETYPE_FONT, CytoidInfo.getResourceAsStream("fonts/MPLUSRounded1c-Regular.ttf")
                )
            }.deriveFont(200f)
            g.drawString(profile.user.uid, 600, 300)
            g.color = Color.WHITE
            g.font = g.font.deriveFont(75f)
            g.drawString("Lv.${profile.exp.currentLevel}  Rating ${profile.rating.toDouble().fix(2)}", 600, 400)

//            获取记录图像
            val deferreds = ArrayList<Deferred<BufferedImage>>()
            for (record in records.data.profile.bestRecords) {
                deferreds.add(getRecordImage(record))
            }
            val recordImages = deferreds.awaitAll().toMutableList()
            
            
            g.color = Color(0x5171DE)
            g.fillRoundRect(50, 600, 6060, 2510, 10, 10)
//            绘制具体记录图像
            var imageIndex = 0
            var startX = 100
            var startY = 650
            //5*6循环绘制网格
            val rows = 1..6
            val columns = 1..5
            for (row in rows) {
                for (column in columns) {
                    g.clip = RoundRectangle2D.Double(startX.toDouble(), startY.toDouble(), 1120.0, 360.0, 10.0, 10.0)
                    g.drawImage(recordImages[imageIndex], startX, startY, 1152, 360, null)
                    startX += 1202
                    imageIndex++
                }
                startX = 100
                startY += 410
            }
        }
        g.dispose()
        return bi
    }
    
    private fun getActivitiesImage(profile: PlayerProfile): BufferedImage {
        val bi = BufferedImage(1900 / 2, 650, BufferedImage.TYPE_INT_ARGB)
        val g = bi.createGraphics().enableAntiAlias()
        
        g.color = Color(0x5171DE)
        g.fillRoundRect(0, 0, 1900 / 2, 650, 5, 5)
        
        g.color = Color.WHITE
        g.font = Font(Font.SANS_SERIF, Font.TRUETYPE_FONT, 10)
        
        g.dispose()
        return bi
    }
    
    private suspend fun getRecordImage(record: B30Records.Data.Profile.Record): Deferred<BufferedImage> =
        coroutineScope {
            async {
                val bi = BufferedImage(1152, 360, BufferedImage.TYPE_INT_ARGB)
                val g = bi.createGraphics().enableAntiAlias()
    
                g.color = Color(0, 0, 0, 128)
                g.fillRect(0, 0, 1152, 360)
    
                //绘制曲绘
                withContext(Dispatchers.IO) {
                    try {
                        g.drawImage(
                            ImageIO.read(URL(record.chart.level.bundle.backgroundImage.thumbnail)),
                            0,
                            0,
                            576,
                            360,
                            null
                        )
                    } catch (e: IOException) {
                        g.drawImage(
                            ImageIO.read(CytoidInfo.getResourceAsStream("pictures/sayakacry.png")),
                            0,
                            0,
                            360,
                            360,
                            null
                        )
                        g.font = withContext(Dispatchers.IO) {
                            Font.createFont(
                                Font.TRUETYPE_FONT,
                                CytoidInfo.getResourceAsStream("fonts/MPLUSRounded1c-Regular.ttf")
                            )
                        }.deriveFont(50f)
                        g.color = Color.WHITE
                        g.drawString("?妹有!11", 370, 200)
                    }
                }
    
                val difficultyImage = withContext(Dispatchers.IO) {
                    ImageIO.read(
                        CytoidInfo.getResourceAsStream("pictures/difficulty/${record.chart.difficulty}.png")
                            ?: CytoidInfo.getResourceAsStream("pictures/difficulty/0.png")
                    )
                }
                g.drawImage(difficultyImage, 606, 30, null)
    
                g.color = Color.WHITE
                g.font = g.font.deriveFont(50f)
                g.drawString(record.chart.level.title, 686, 80)
                g.drawString("${record.score} Acc:${(record.accuracy * 100).fix(2)}%", 606, 160)
                g.font = g.font.deriveFont(30f)
                g.drawString("Rating:${record.rating.fix(2)}", 606, 190)
                g.drawString(
                    "Perfect:${record.details.perfect} Great:${record.details.great} Good:${record.details.good}",
                    606,
                    220
                )
                g.drawString("Bad:${record.details.bad} Miss:${record.details.miss}", 606, 250)
                g.drawString(
                    "Max Combo:${record.details.maxCombo} ${if (record.score == 1000000) "All Perfect" else if (record.details.maxCombo == record.chart.notesCount) "Full Combo" else ""}",
                    606,
                    280
                )
                g.drawString("Mods:${record.mods}", 606, 340)
                
                g.dispose()
                bi
            }
        }
}