package com.lyneon.cytoidinfo

import com.lyneon.cytoidinfo.logic.JsonParser
import com.lyneon.cytoidinfo.logic.NetRequest
import com.lyneon.cytoidinfo.model.PlayerProfile
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.message.data.ForwardMessageBuilder
import net.mamoe.mirai.message.data.PlainText

class MainCommand : CompositeCommand(
    CytoidInfo, "cytoid", "ctd"
) {
    @SubCommand
    suspend fun profile(
        context: CommandSender,
        playerName: String,
        queryType: String = "default",
        outputType: String = "text"
    ) {
        val contact = context.subject ?: return
        val bot = context.bot ?: return
        contact.sendMessage("开始查询")
        val playerProfile: PlayerProfile = try {
            if (queryType != "default" && queryType != "detail") {
                contact.sendMessage("参数不正确")
                return
            }
            NetRequest.getPlayerProfile(playerName)
        } catch (e: Exception) {
            contact.sendMessage("请求失败：${e.message}")
            return
        }
        val result = try {
            when {
                queryType == "default" && outputType == "text" -> JsonParser.parsePlayerProfileToText(
                    playerProfile,
                    false
                )
//                queryType == "default" && outputType == "image" -> ImageHandler.profileToImage(playerProfile, false)
                queryType == "detail" && outputType == "text" -> JsonParser.parsePlayerProfileToText(
                    playerProfile,
                    true
                )
                
                queryType == "detail" && outputType == "image" -> {}
                else -> throw Exception("Unknown exception")
            }
        } catch (e: Exception) {
            contact.sendMessage("解析失败：${e.message}")
            return
        }
        when (result) {
            is String -> when (queryType) {
                "default" -> contact.sendMessage(result)
                "detail" -> contact.sendMessage(
                    ForwardMessageBuilder(contact)
                        .add(bot, PlainText(result))
                        .build()
                )
                
                else -> throw Exception("Unknown exception")
            }
            //is BufferedImage -> group?.sendImage(InputStreamReader())
            else -> throw Exception("Unknown exception")
        }
    }
    
    @SubCommand
    suspend fun b30(
        context: CommandSender,
        playerName: String,
        count: Int = 30
    ) {
        val contact = context.subject ?: return
        val bot = context.bot ?: return
        contact.sendMessage("开始查询")
        val result = JsonParser.parseB30RecordsToText(NetRequest.getB30Records(playerName, count), playerName)
        val message = ForwardMessageBuilder(contact)
            .add(bot, PlainText(result))
            .build()
        contact.sendMessage(message)
    }
}