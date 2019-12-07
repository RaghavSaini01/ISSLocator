package com.example.isslocator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FlyOverISSActivity extends AppCompatActivity {

    private EditText latEdit, longEdit;
    private Button calcFlyOver;
    private RequestQueue queue;
    private FlyOverRecyclerAdapter myAdapter;
    private RecyclerView flyOverRecycler;
    private List<FlyOverData> flyOverData;
    private String URL = "http://api.open-notify.org/iss-pass.json?lat=LAT&lon=LON";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fly_over_iss);

        latEdit = findViewById(R.id.lat_edit);
        longEdit = findViewById(R.id.long_edit);
        calcFlyOver = findViewById(R.id.calc_flyover_button);
        flyOverRecycler = findViewById(R.id.fly_over_recycler);
        flyOverData = new ArrayList<FlyOverData>(0);

        queue = Volley.newRequestQueue(FlyOverISSActivity.this);


        calcFlyOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latToCalc = Double.parseDouble(latEdit.getText().toString());
                double longToCalc = Double.parseDouble(longEdit.getText().toString());
                System.out.println(latToCalc);
                System.out.println(longToCalc);

                if (latToCalc < -90.0 || latToCalc > 90.0
                        || longToCalc < -90.0 || longToCalc > 90.0) {
                    Toast.makeText(FlyOverISSActivity.this, "Incorrect Latitude or Longitude Value:" +
                            " both must be within -90.0 and 90.0", Toast.LENGTH_LONG).show();
                } else {
                    updateFlyoverList(latToCalc, longToCalc);
                    setUpRecycler();
                }
            }
        });
    }

    public void updateFlyoverList(double latitude, double longitude) {
        String urlToUse = URL.substring(0, URL.indexOf("LAT")) + latitude +
                URL.substring(URL.indexOf("&"), URL.indexOf("LON")) + longitude;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToUse, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray flyOverList = response.getJSONArray("response");

                            for (int i = 0; i < flyOverList.length(); i++) {
                                JSONObject data = flyOverList.getJSONObject(i);
                                FlyOverData flyOver = new FlyOverData();

                                int unixTime = data.getInt("risetime");
                                java.util.Date d = new java.util.Date(unixTime * 1000);
                                String itemDateStr = d.toString();
                                flyOver.setRiseTime(itemDateStr);

                                int passDuration = data.getInt("duration");
                                String formatDuration = (passDuration / 60) + "min " +
                                        (passDuration % 60) + "sec";
                                flyOver.setDuration(formatDuration);

                                flyOverData.add(flyOver);

                            }
                        } catch (JSONException j) {
                            Toast.makeText(FlyOverISSActivity.this, "JSON Parse Error",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FlyOverISSActivity.this, "Could not make requst from server: " +
                        "please check internet connection", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(jsonObjectRequest);

    }

    public void setUpRecycler() {
        myAdapter = new FlyOverRecyclerAdapter(this, flyOverData);
        flyOverRecycler.setAdapter(myAdapter);
        flyOverRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
    }
}
