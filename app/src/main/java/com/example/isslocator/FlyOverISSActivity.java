package com.example.isslocator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Date;
import java.util.TimeZone;

import static java.util.TimeZone.getDefault;
import static java.util.TimeZone.getTimeZone;

public class FlyOverISSActivity extends AppCompatActivity {

    private EditText latEdit, longEdit;
    private Button calcFlyOver;
    private RequestQueue queue;
    private FlyOverRecyclerAdapter myAdapter;
    private RecyclerView flyOverRecycler;
    private List<FlyOverData> flyOverData;
    private final String URL = "http://api.open-notify.org/iss-pass.json?lat=LAT&lon=LON";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fly_over_iss);

        latEdit = findViewById(R.id.lat_edit);
        longEdit = findViewById(R.id.long_edit);
        calcFlyOver = findViewById(R.id.calc_flyover_button);
        flyOverRecycler = findViewById(R.id.fly_over_recycler);
        flyOverData = new ArrayList<>(0);

        queue = Volley.newRequestQueue(FlyOverISSActivity.this);


        calcFlyOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double latToCalc = Double.parseDouble(latEdit.getText().toString());
                    double longToCalc = Double.parseDouble(longEdit.getText().toString());

                    if (!((latToCalc >= -90.0 && latToCalc <= 90.0)
                            && (longToCalc >= -180.0 && longToCalc <= 80.0))) {
                        Toast.makeText(FlyOverISSActivity.this, "Incorrect Latitude or Longitude Value:" +
                                " latitude is between -90 and 90 and longitude between " +
                                "-180 and 80", Toast.LENGTH_LONG).show();
                    } else {
                        updateFlyoverList(latToCalc, longToCalc);
                        setUpRecycler();
                    }

                } catch (Exception e) {
                    Toast.makeText(FlyOverISSActivity.this, "Invalid inputs, try again!",
                            Toast.LENGTH_LONG).show();
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
                            flyOverData = new ArrayList<>(flyOverList.length());

                            for (int i = 0; i < flyOverList.length(); i++) {
                                JSONObject data = flyOverList.getJSONObject(i);
                                FlyOverData flyOver = new FlyOverData();

                                int unixTime = data.getInt("risetime");
                                Date d = new Date(unixTime * 1000L);
                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd, HH:mm");
                                sdf.setTimeZone(getDefault());
                                String itemDateStr = sdf.format(d);
                                flyOver.setRiseTime(itemDateStr);

                                int passDuration = data.getInt("duration");
                                String formatDuration = (passDuration / 60) + " minutes, " +
                                        (passDuration % 60) + " seconds";
                                flyOver.setDuration(formatDuration);

                                flyOverData.add(i, flyOver);

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
                LinearLayoutManager.HORIZONTAL, false));
    }
}
