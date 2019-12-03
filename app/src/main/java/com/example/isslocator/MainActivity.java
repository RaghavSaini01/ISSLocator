package com.example.isslocator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView latText;
    private TextView longText;
    private RequestQueue queue;
    private Handler handler;
    private Runnable runnable;
    private SupportMapFragment issMap;
    private GoogleMap mainMap;
    private Button toFlyover;
    private Button toAstros;

    private final String URL = "http://api.open-notify.org/iss-now.json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latText = findViewById(R.id.lat_disp);
        longText = findViewById(R.id.long_disp);
        queue = Volley.newRequestQueue(MainActivity.this);

        issMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.iss_map);
        issMap.getMapAsync(this);

        toFlyover = findViewById(R.id.go_to_flyover);
        toAstros = findViewById(R.id.go_to_astros);

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                getLocation();

                handler.postDelayed(runnable, 2500);
            }
        };

        handler.post(runnable);

        toFlyover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        toAstros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mainMap = googleMap;
    }

    private void getLocation() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String longitude;
                        String latitude;
                        try {
                            longitude = response.getJSONObject("iss_position").getString("longitude");
                            latitude = response.getJSONObject("iss_position").getString("latitude");
                            latText.setText(latitude);
                            longText.setText(longitude);

                            mainMap.clear();
                            LatLng loc = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                            mainMap.addMarker(new MarkerOptions().position(loc));

                        } catch (JSONException j) {
                            Toast.makeText(MainActivity.this, "That failed", Toast.LENGTH_LONG);
                            Log.d("Error", "Problem w/ Response?");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "That failed", Toast.LENGTH_LONG);
            }
        });

        queue.add(jsonObjectRequest);
    }

}
