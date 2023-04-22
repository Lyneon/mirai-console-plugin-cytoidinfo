package com.lyneon.cytoidinfo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerProfile(
    val user: User,
    val exp: Exp,
    val rating: String,
    val grade: Grade,
    val activities: Activities,
    val tier: Tier,
    val character: Character,
    val recentRecords: List<Record>
){
    @Serializable
    data class Record(
        val score: Int,
        val accuracy: Float,
        val details: Detail,
        val chart: Chart
    ) {
        @Serializable
        data class Detail(
            val bad: Int,
            val good: Int,
            val miss: Int,
            val great: Int,
            val perfect: Int,
            val maxCombo: Int
        )
        
        @Serializable
        data class Chart(
            val type: String,
            val difficulty: Int,
            val level: Level,
            val notesCount: Int
        ) {
            @Serializable
            data class Level(
                val title: String
            )
            
        }
    }
    
    @Serializable
    data class Character(
        val name: String,
        val exp: Exp
    ) {
        @Serializable
        data class Exp(
            val currentLevel: Int
        )
    }
    
    @Serializable
    data class Tier(
        val name:String
    )
    
    @Serializable
    data class User(
        val id:String,
        val uid:String,
        val avatar: Avatar
    ){
        @Serializable
        data class Avatar(
            val original:String
        )
    }
    
    @Serializable
    data class Exp(
        val basicExp:Int,
        val levelExp:Int,
        val totalExp:Int,
        val currentLevel:Int,
        val nextLevelExp:Int,
        val currentLevelExp:Int
    )
    
    @Serializable
    data class Grade(
        @SerialName("B")val b:Int,
        @SerialName("SSS")val sss:Int,
        @SerialName("AA")val aa:Int,
        @SerialName("SS")val ss:Int,
        @SerialName("C")val c:Int,
        @SerialName("MAX")val max:Int,
        @SerialName("D")val d:Int,
        @SerialName("F")val f:Int,
        @SerialName("S")val s:Int,
        @SerialName("A")val a:Int
    )
    
    @Serializable
    data class Activities(
        val totalRankedPlays:Int,
        val clearedNotes:Long,
        val maxCombo:Int,
        val averageRankedAccuracy:Double,
        val totalRankedScore:Long
    )
    
}