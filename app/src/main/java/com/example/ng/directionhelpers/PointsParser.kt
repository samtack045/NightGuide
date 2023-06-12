package com.example.ng.directionhelpers

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import com.example.ng.directionhelpers.DataParser
import com.example.ng.directionhelpers.TaskLoadedCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import org.json.JSONObject

class PointsParser(mContext: TaskLoadedCallback, directionMode: String) :
    AsyncTask<String?, Int?, List<List<HashMap<String, String>>>?>() {
    private var taskCallback: TaskLoadedCallback
    var directionMode = "walking"

    init {
        this.taskCallback = mContext
        this.directionMode = directionMode
    }

    // Parsing the data in non-ui thread
    protected override fun doInBackground(vararg jsonData: String?): List<List<HashMap<String, String>>>? {
        val jObject: JSONObject
        var routes: List<List<HashMap<String, String>>>? = null
        try {
            jObject = JSONObject(jsonData[0])
            //jsonData[0]?.let { Log.d("mylog", it) }
            val parser = DataParser()


            // Starts parsing data
            routes = parser.parse(jObject)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return routes
    }

    fun findSafestPath(result: List<List<HashMap<String, String>>>?) {
        for (i in result!!.indices) {
            val path = result[i]


        }
    }



    // Executes in UI thread, after the parsing process
    override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
        var points: ArrayList<LatLng?>
        var lineOptions: PolylineOptions? = null




        // Traversing through all the routes
        for (i in result!!.indices) {
            Log.d("mylog", result!!.indices.toString())
            Log.d("mylog", i.toString())
            points = ArrayList()
            lineOptions = PolylineOptions()
            // Fetching i-th route
//            if (i == -1) {
//                break
//            }
            val path = result[i]
            Log.d("mylog", path.toString())
            // Fetching all the points in i-th route
            for (j in path.indices) {
                val point = path[j]
                val lat = point["lat"]!!.toDouble()
                val lng = point["lng"]!!.toDouble()
                val position = LatLng(lat, lng)
                points.add(position)
            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points)
            if (directionMode.equals("walking", ignoreCase = true)) {
                lineOptions.width(10f)
                lineOptions.color(Color.MAGENTA)
            } else {
                lineOptions.width(20f)
                lineOptions.color(Color.BLUE)
            }

        }


        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null) {
            //Log.d("mylog", "end of this1")
            //mMap.addPolyline(lineOptions);
            taskCallback.onTaskDone(lineOptions)

        } else {
            //Log.d("mylog", "without Polylines drawn")
        }
        //Log.d("mylog", "end of this2")
    }
}