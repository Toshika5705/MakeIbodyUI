package com.example.webfetchdata.JsonType


class Inbody : ArrayList<InbodyItem>()

data class InbodyItem(
    val createdtime: String,
    val fat: Double,
    val muscle: Double,
    val name: String,
    val pfla: Double,
    val pfll: Double,
    val pfra: Double,
    val pfrl: Double,
    val pft: Double,
    val pilla: Double,
    val pilll: Double,
    val pilra: Double,
    val pilrl: Double,
    val pilt: Double,
    val score: Int,
    val row:Int
)
