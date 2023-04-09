package com.lyneon.cytoidinfo

import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info

object CytoidInfo : KotlinPlugin(
    JvmPluginDescription(
        id = "com.lyneon.cytoidinfo",
        name = "CytoidInfo",
        version = "0.1.0",
    ) {
        author("Lyneon")
    }
) {
    override fun onEnable() {
        logger.info { "插件已加载" }
        //注册/cytoid命令
        CommandManager.registerCommand(MainCommand())
        logger.info("命令已注册")
    }
}