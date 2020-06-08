package com.thanosfisherman.wifiutils.sample

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionErrorCode
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionSuccessListener
import com.thanosfisherman.wifiutils.wifiRemove.RemoveErrorCode
import com.thanosfisherman.wifiutils.wifiRemove.RemoveSuccessListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainKotlinActivity : AppCompatActivity() {

    private val SSID = "telenet-F1EB5"
    private val PASSWORD = "hommeltjes"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 555)
        WifiUtils.enableLog(true)
        textview_ssid.text = SSID
        textview_password.text = PASSWORD
        button_connect.setOnClickListener { connectWithWpa(applicationContext) }
        button_disconnect.setOnClickListener { disconnect(applicationContext) }
        button_remove.setOnClickListener{ remove(applicationContext) }
        button_test_internet.setOnClickListener{ testInternet(applicationContext) }
    }

    private fun connectWithWpa(context: Context) {
        WifiUtils.withContext(context)
            .connectWith(SSID, PASSWORD)
            .setTimeout(15000)
            .onConnectionResult(object : ConnectionSuccessListener {
                override fun success() {
                    Toast.makeText(context, "SUCCESS!", Toast.LENGTH_SHORT).show()
                }

                override fun failed(errorCode: ConnectionErrorCode) {
                    Toast.makeText(context, "EPIC FAIL!$errorCode", Toast.LENGTH_SHORT).show()
                }
            })
            .start()
    }

    private fun disconnect(context: Context) {
        WifiUtils.withContext(context)
            .disconnect(object : DisconnectionSuccessListener {
                override fun success() {
                    Toast.makeText(context, "Disconnect success!", Toast.LENGTH_SHORT).show()
                }

                override fun failed(errorCode: DisconnectionErrorCode) {
                    Toast.makeText(context, "Failed to disconnect: $errorCode", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun remove(context: Context) {
        WifiUtils.withContext(context)
                .remove(SSID, object : RemoveSuccessListener {
                    override fun success() {
                        Toast.makeText(context, "Remove success!", Toast.LENGTH_SHORT).show()
                    }

                    override fun failed(errorCode: RemoveErrorCode) {
                        Toast.makeText(context, "Failed to disconnect and remove: $errorCode", Toast.LENGTH_SHORT).show()
                    }
                })
    }

    //region test internet

    private fun testInternet(context: Context) {
         GlobalScope.async {
             val result = httpGet("https://android.com/")
             Log.d("WifiUtils", result)
        }
    }

    private suspend fun httpGet(myURL: String?): String? {
        Log.d("WifiUtils", "prepare");

        val result = withContext(Dispatchers.IO) {
            val inputStream: InputStream

            // create URL
            val url: URL = URL(myURL)

            // create HttpURLConnection
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection

            // make GET request to the given URL
            conn.connect()

            conn.responseCode.toString()
        }
        return result
    }

    //endregion
}