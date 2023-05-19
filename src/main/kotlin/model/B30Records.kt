package com.lyneon.cytoidinfo.model

import kotlinx.serialization.Serializable

@Serializable
data class B30Records(val data:Data){
    @Serializable
    data class Data(val profile:Profile){
        @Serializable
        data class Profile(
            val bestRecords:List<Record>
        ){
            @Serializable
            data class Record(
                val score: Int,
                val mods: List<String>,
                val accuracy:Float,
                val rating : Float,
                val details: Detail,
                val chart: Chart
            ){
                @Serializable
                data class Detail(
                    val perfect: Int,
                    val great: Int,
                    val good: Int,
                    val bad: Int,
                    val miss: Int,
                    val maxCombo: Int
                )
                
                @Serializable
                data class Chart(
                    val type: String,
                    val difficulty:Int,
                    val notesCount: Int,
                    val level:Level
                ){
                    @Serializable
                    data class Level(
                        val title:String,
                        val bundle: Bundle
                    ){
                        @Serializable
                        data class Bundle(
                            val backgroundImage: BackgroundImage
                        ){
                            @Serializable
                            data class BackgroundImage(
                                val original: String,
                                val thumbnail: String
                            )
                        }
                    }
                }
            }
        }
    }
}