package com.example.isslocator.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.isslocator.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONException

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var latText: TextView? = null
    private var longText: TextView? = null
    private var queue: RequestQueue? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var issMap: SupportMapFragment? = null
    private var mainMap: GoogleMap? = null
    private val url = "http://api.open-notify.org/iss-now.json"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        latText = findViewById(R.id.lat_disp)
        longText = findViewById(R.id.long_disp)
        queue = Volley.newRequestQueue(applicationContext)
        val toFlyover: Button = findViewById(R.id.go_to_flyover)
        val toAstros: Button = findViewById(R.id.go_to_astros)
        issMap = supportFragmentManager.findFragmentById(R.id.iss_map) as SupportMapFragment?
        issMap!!.getMapAsync(this)
        toFlyover.setOnClickListener {
            val toPassOver = Intent(applicationContext, FlyOverISSActivity::class.java)
            startActivity(toPassOver)
        }
        toAstros.setOnClickListener {
            val toAstroInfo = Intent(applicationContext, AstroInfoActivity::class.java)
            startActivity(toAstroInfo)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mainMap = googleMap
        handler = Handler()
        runnable = Runnable {
            updateLocation()
            handler!!.postDelayed(runnable, 2500)
        }
        handler!!.post(runnable)
    }

    private fun updateLocation() {
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    val longitude: String
                    val latitude: String
                    try {
                        longitude = response.getJSONObject("iss_position").getString("longitude")
                        latitude = response.getJSONObject("iss_position").getString("latitude")
                        latText!!.text = latitude
                        longText!!.text = longitude
                        mainMap!!.clear()
                        val loc = LatLng(latitude.toDouble(), longitude.toDouble())
                        mainMap!!.addMarker(MarkerOptions().position(loc))
                        mainMap!!.moveCamera(CameraUpdateFactory.newLatLng(loc))
                    } catch (j: JSONException) {
                        Toast.makeText(applicationContext, "JSON Field parsed incorrectly", Toast.LENGTH_LONG).show()
                    }
                }, Response.ErrorListener { error -> error.printStackTrace() })
        queue!!.add(jsonObjectRequest)
    }
}