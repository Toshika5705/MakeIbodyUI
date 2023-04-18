package com.example.webfetchdata.JsonType

import com.example.webfetchdata.LayoutXml.Data

class Cooperative(
    val contentType: Any,
    val `data`: List<Data>,
    val errorCode: Int,
    val id: String,
    val isImage: Boolean,
    val message: Any,
    val success: Boolean
)
