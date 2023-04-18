package com.example.webfetchdata.JsonType
import com.example.webfetchdata.LayoutXml.Currency


class Request(
    var result: String,
    var time_last_update_utc: String,
    var time_next_update_utc: String,
    var base_code: String,
    var rates: Currency
)