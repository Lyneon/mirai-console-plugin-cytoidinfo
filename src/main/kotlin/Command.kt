package com.lyneon.cytoidinfo

import com.lyneon.cytoidinfo.logic.ImageHandler
import com.lyneon.cytoidinfo.logic.JsonParser
import com.lyneon.cytoidinfo.logic.NetRequest
import com.lyneon.cytoidinfo.model.PlayerProfile
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.contact.Group
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
        val group = if (context.getGroupOrNull() == null) return else {
            context.getGroupOrNull() as Group
        }
        val bot = if (context.bot == null) return else {
            context.bot as Bot
        }
        group.sendMessage("开始查询")
        val playerProfile: PlayerProfile = try {
            if (queryType != "default" && queryType != "detail") {
                group.sendMessage("参数不正确")
                return
            }
            NetRequest.getPlayerProfile(playerName)
        } catch (e: Exception) {
            group.sendMessage("请求失败：${e.message}")
            return
        }
        val result = try {
            when {
                queryType == "default" && outputType == "text" -> JsonParser.parsePlayerProfileToText(playerProfile, false)
                queryType == "default" && outputType == "image" -> ImageHandler.profileToImage(playerProfile, false)
                queryType == "detail" && outputType == "text" -> JsonParser.parsePlayerProfileToText(playerProfile, true)
                queryType == "detail" && outputType == "image" -> {}
                else -> throw Exception("Unknown exception")
            }
        } catch (e: Exception) {
            group.sendMessage("解析失败：${e.message}")
            return
        }
        when (result) {
            is String -> when(queryType){
                "default" -> group.sendMessage(result)
                "detail" -> group.sendMessage(ForwardMessageBuilder(group)
                    .add(bot,PlainText(result))
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
        val group = if (context.getGroupOrNull() == null) return else {
            context.getGroupOrNull() as Group
        }
        val bot = if (context.bot == null) return else{
            context.bot as Bot
        }
        group.sendMessage("开始查询")
        val result = JsonParser.parseB30RecordsToText(NetRequest.getB30Records(playerName, count),playerName)
        val message = ForwardMessageBuilder(group)
            .add(bot,PlainText(result))
            .build()
        group.sendMessage(message)
    }
}