package com.example.webfetchdata.Http

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.webfetchdata.JsonType.Request
import com.example.webfetchdata.R
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class WebHttpDate : AppCompatActivity() {
    //private lateinit var binding: ActivityMainBinding
    var baseCurrency: TextView? = null
    var lastUpdated: TextView? = null
    var nzd: TextView? = null
    var usd: TextView? = null
    var gbp: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_http_date)

        //binding = ActivityMainBinding.inflate(layoutInflater)
        //初始化 Kotlin 需要
        //setContentView(binding.root)

         baseCurrency = findViewById<TextView>(R.id.baseCurrency)
         lastUpdated = findViewById<TextView>(R.id.lastUpdated)
         nzd = findViewById<TextView>(R.id.nzd)
         usd = findViewById<TextView>(R.id.usd)
         gbp = findViewById<TextView>(R.id.gbp)

        fetchCurrencyData().start()

    }
    //等待 2秒刷新
     fun fetchCurrencyData(): Thread {

         return Thread {
             val url = URL("https://open.er-api.com/v6/latest/aud")
             val connection  = url.openConnection() as HttpsURLConnection
             if(connection.responseCode == 200)
             {
                 val inputSystem = connection.inputStream
                 val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                 val request = Gson().fromJson(inputStreamReader, Request::class.java)
                 updateUI(request)
                 inputStreamReader.close()
                 inputSystem.close()
             }
             else
             {
                 baseCurrency!!.text = "Failed Connection"
             }
         }
    }
    //抓設定型態
     fun updateUI(request: Request) {

         runOnUiThread {
             kotlin.run {
                 lastUpdated!!.text = request.time_last_update_utc
                 nzd!!.text = String.format("NZD: %.2f", request.rates.NZD)
                 usd!!.text = String.format("USD: %.2f", request.rates.USD)
                 gbp!!.text = String.format("GBP: %.2f", request.rates.GBP)
             }
         }
    }
}