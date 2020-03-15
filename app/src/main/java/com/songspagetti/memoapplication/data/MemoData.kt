package com.songspagetti.memoapplication.data

import java.util.*

class MemoData (
    var id: String = UUID.randomUUID().toString(), //메모의 고유 ID
    var createdAt: Date = Date(),
    var title: String = "",
    var content: String = "",
    var summary: String = "",
    var imageFile: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var alarmTime: Date = Date(),
    var weather: String = ""
)