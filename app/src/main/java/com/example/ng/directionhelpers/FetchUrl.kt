package com.example.ng.directionhelpers
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import com.example.ng.directionhelpers.PointsParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class FetchURL(var mContext: TaskLoadedCallback) {
    var directionMode = "walking"

    fun execute(url: String, mode: String) {
        directionMode = mode
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = downloadUrl(url)
//                Log.d("mylog", "Background task data $data")
//                Log.d("testlog","HI")
                onPostExecute(data)
            } catch (e: Exception) {

                //Log.d("BackgroundTask", e.toString())
            }
        }
    }

    private fun onPostExecute(s: String) {
        val parserTask = PointsParser(mContext, directionMode)
        // Invokes the thread for parsing the JSON data
        parserTask.execute(s)
    }

    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)
            // Creating an http connection to communicate with url
            urlConnection = url.openConnection() as HttpURLConnection
            // Connecting to url
            urlConnection.connect()
            // Reading data from url
            iStream = urlConnection!!.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            //Log.d("mylog", "Downloaded URL: $data")
            br.close()
        } catch (e: Exception) {
            //Log.d("mylog", "Exception downloading URL: $e")
        } finally {
            iStream?.close()
            urlConnection?.disconnect()
        }
        return data
    }
}
