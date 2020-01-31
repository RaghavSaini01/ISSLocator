package com.example.isslocator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import static java.util.TimeZone.getDefault;

public class FlyOverISSActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText latEdit, longEdit;
    private Button calcFlyOver;
    private RequestQueue queue;
    private FlyOverRecyclerAdapter myAdapter;
    private RecyclerView flyOverRecycler;
    private List<FlyOverData> flyOverObjectList;
    private boolean numPassesDefault = true;
    private Spinner numSelectedPasses;
    private final String URL = "http://api.open-notify.org/iss-pass.json?lat=LAT&lon=LON";
    private final String extension = URL.substring(URL.indexOf("&"), URL.indexOf("LON"));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fly_over_iss);

        latEdit = findViewById(R.id.lat_edit);
        longEdit = findViewById(R.id.long_edit);
        calcFlyOver = findViewById(R.id.calc_flyover_button);
        flyOverRecycler = findViewById(R.id.fly_over_recycler);
        flyOverObjectList = new ArrayList<>();
        numSelectedPasses = findViewById(R.id.num_passes);

        List<String>passOptions = new ArrayList<>();
        passOptions.add("Number of flyovers to calculate (Optional)");

        for (int i = 0; i < 100; i++) {
            passOptions.add(i + 1 + "");
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, passOptions);

        numSelectedPasses.setAdapter(arrayAdapter);
        numSelectedPasses.setSelection(0);
        numSelectedPasses.setOnItemSelectedListener(this);


        queue = Volley.newRequestQueue(FlyOverISSActivity.this);
        calcFlyOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double latToCalc = Double.parseDouble(latEdit.getText().toString());
                    double longToCalc = Double.parseDouble(longEdit.getText().toString());

                    if (!((latToCalc >= -90.0 && latToCalc <= 90.0)
                            && (longToCalc >= -180.0 && longToCalc <= 80.0))) {
                        Toast.makeText(FlyOverISSActivity.this,
                                "Incorrect Latitude or Longitude Value:" +
                                " latitude is between -90 and 90 and longitude between " +
                                "-180 and 80", Toast.LENGTH_LONG).show();
                    } else {
                        updateFlyoverList(latToCalc, longToCalc);
                        setUpRecycler();
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(FlyOverISSActivity.this,
                            "Invalid inputs, try again!",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void updateFlyoverList(double latitude, double longitude) {
        String urlToUse = URL.substring(0, URL.indexOf("LAT")) + latitude +
                extension + longitude;

        if (!numPassesDefault) {
            urlToUse =  URL.substring(0, URL.indexOf("LAT")) + latitude +
                    extension + longitude + "&n="
                    + numSelectedPasses.getSelectedItem().toString();
        }
        makeFlyoverRequest(urlToUse);
    }

    public void makeFlyoverRequest(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray flyOverList = response.getJSONArray("response");
                            flyOverObjectList = new ArrayList<>(flyOverList.length());

                            for (int i = 0; i < flyOverList.length(); i++) {
                                JSONObject flyOverResponse = flyOverList.getJSONObject(i);
                                FlyOverData flyOverObject = new FlyOverData();

                                int unixTime = flyOverResponse.getInt("risetime");
                                Date d = new Date(unixTime * 1000L);
                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd, HH:mm");
                                sdf.setTimeZone(getDefault());
                                String itemDateStr = sdf.format(d);
                                flyOverObject.setRiseTime(itemDateStr);

                                int passDuration = flyOverResponse.getInt("duration");
                                String formatDuration = (passDuration / 60) + " minutes, " +
                                        (passDuration % 60) + " seconds";
                                flyOverObject.setDuration(formatDuration);

                                flyOverObjectList.add(i, flyOverObject);

                            }
                        } catch (JSONException j) {
                            Toast.makeText(FlyOverISSActivity.this, "JSON Parse Error",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FlyOverISSActivity.this, "Could not make request from server: " +
                        "please check internet connection", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(jsonObjectRequest);
    }

    public void setUpRecycler() {
        myAdapter = new FlyOverRecyclerAdapter(this, flyOverObjectList);
        flyOverRecycler.setAdapter(myAdapter);
        flyOverRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false));
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if (pos > 0 && pos != 5) {
            numPassesDefault = false;
        } else {
            numPassesDefault = true;
        }
        numSelectedPasses.setSelection(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }

}
