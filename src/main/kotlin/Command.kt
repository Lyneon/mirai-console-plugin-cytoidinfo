package com.lyneon.cytoidinfo

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.getGroupOrNull

class MainCommand : CompositeCommand(
    CytoidInfo,"cytoid","ctd"
){
    @SubCommand
    suspend fun query(context:CommandSender,playerName:String,type:String = "default"){
        context.getGroupOrNull()?.sendMessage("执行中")
        val playerProfile =try {
            when (type) {
                "default" -> NetRequest.getPlayerProfile(playerName)
                "detail" -> NetRequest.getDetailPlayerProfile(playerName)
                else -> {
                    context.getGroupOrNull()?.sendMessage("参数不正确")
                    return
                }
            }
        }catch (e:Exception){
            context.getGroupOrNull()?.sendMessage("请求失败：${e.message}")
            return
        }
        val result = try{
            JsonParser.parsePlayerProfileToText(playerProfile)
        }catch (e:Exception){
            context.getGroupOrNull()?.sendMessage("解析失败：${e.message}")
            return
        }
        context.getGroupOrNull()?.sendMessage(result)
    }
}