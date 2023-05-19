package com.lyneon.cytoidinfo

import com.lyneon.cytoidinfo.logic.DataParser
import com.lyneon.cytoidinfo.logic.ImageHandler
import com.lyneon.cytoidinfo.logic.NetRequest
import com.lyneon.cytoidinfo.model.PlayerProfile
import com.lyneon.cytoidinfo.tool.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.message.data.ForwardMessageBuilder
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.MiraiLogger

class MainCommand : CompositeCommand(
    CytoidInfo, "cytoid", "ctd"
) {
    private val logger = MiraiLogger.Factory.INSTANCE.create(this::class.java)
    
    @SubCommand
    suspend fun profile(
        context: CommandSender,
        playerName: String,
        queryType: String = "default",
//        outputType: String = "text"
    ) {
        val contact = context.subject ?: return
        val bot = context.bot ?: return
        contact.sendMessage("开始查询")
        coroutineScope {
            val playerProfile: PlayerProfile = try {
                if (queryType != "default" && queryType != "detail") {
                    contact.sendMessage("参数不正确\nqueryType只能为default或detail")
                    return@coroutineScope
                } else NetRequest.getPlayerProfile(playerName)
            } catch (e: Exception) {
                contact.sendMessage("请求失败：${e.message}")
                logger.error(e)
                return@coroutineScope
            }
            val result = try {
                when (queryType) {
                    "default" -> DataParser.parsePlayerProfileToText(playerProfile, false)
                    "detail" -> DataParser.parsePlayerProfileToText(playerProfile, true)
                    else -> throw Exception("Unknown exception")
                }
            } catch (e: Exception) {
                contact.sendMessage("解析失败：${e.message}")
                logger.error(e)
                return@coroutineScope
            }
            when (queryType) {
                "default" -> contact.sendMessage(result)
                "detail" -> contact.sendMessage(
                    ForwardMessageBuilder(contact)
                        .add(bot, PlainText(result))
                        .build()
                )
                
                else -> throw Exception("Unknown exception")
            }
        }
    }
    
    @SubCommand
    suspend fun b30(
        context: CommandSender,
        playerName: String,
        outputType: String = "text",
        count: Int = 30
    ) {
        val contact = context.subject ?: return
        val bot = context.bot ?: return
        contact.sendMessage("开始查询")
        coroutineScope {
            try {
                when (outputType) {
                    "text" -> {
                        val result =
                            DataParser.parseB30RecordsToText(NetRequest.getB30Records(playerName, count), playerName)
                        val message = ForwardMessageBuilder(contact)
                            .add(bot, PlainText(result))
                            .build()
                        contact.sendMessage(message)
                    }
                    
                    "image" -> {
                        val profile = NetRequest.getPlayerProfile(playerName)
                        val image = ImageHandler.getB30RecordsImage(profile)
                        val imageStream = image.toInputStream()
                        contact.sendImage(imageStream)
                        image.flush()
                        withContext(Dispatchers.IO) {
                            imageStream.close()
                        }
                    }
                    
                    else -> throw Exception("unknown exception")
                }
            } catch (e: Exception) {
                contact.sendMessage("查询失败：${e.message}")
            }
        }
    }
}