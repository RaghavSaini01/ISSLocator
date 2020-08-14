package com.example.isslocator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.List;

public class AstroInfoActivity extends AppCompatActivity {

    private TextView numAstros;
    private RequestQueue numRequestQueue;
    private RequestQueue namesRequestQueue;
    private List<AstroInfoData> astroInfoList;
    private AstroInfoRecyclerAdapter myAdapter;
    private RecyclerView astroInfoRecycler;
    private final String URL = "http://api.open-notify.org/astros.json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_astro_info);

        numAstros = findViewById(R.id.number_astros_disp);
        astroInfoRecycler = findViewById(R.id.astro_info_recycler);
        astroInfoList = new ArrayList<>();

        numRequestQueue = Volley.newRequestQueue(this);
        namesRequestQueue = Volley.newRequestQueue(this);

        updateNumAstros();
        updateAstroNameInfo();

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setUpRecycler();
            }
        }, 2000);

    }

    private void updateNumAstros() {
        JsonObjectRequest numsJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String numAstros;
                        try {
                            numAstros = response.getInt("number") + "";
                            AstroInfoActivity.this.numAstros.setText(numAstros + " astronauts:");
                        } catch (JSONException j) {
                            Toast.makeText(AstroInfoActivity.this, "JSON Field parsed incorrectly", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(AstroInfoActivity.this, "Request failed; check internet connection", Toast.LENGTH_LONG).show();
            }
        });

        numRequestQueue.add(numsJsonObjectRequest);
    }

    private void updateAstroNameInfo() {
        JsonObjectRequest namesJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray astros = response.getJSONArray("people");

                            for (int i = 0; i < astros.length(); i++) {
                                JSONObject astro = astros.getJSONObject(i);
                                AstroInfoData astronaut = new AstroInfoData();
                                astronaut.setName(astro.getString("name"));
                                String[] splitAstroName = astro.getString("name").split(" ");
                                astronaut.setLink("https://en.wikipedia.org/wiki/" + splitAstroName[0] +
                                        "_" + splitAstroName[splitAstroName.length - 1]);
                                astroInfoList.add(i, astronaut);
                                System.out.println(astroInfoList.size());
                            }
                        } catch (JSONException j) {
                            Toast.makeText(getApplicationContext(), "JSON Parsing Error", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Could not connect to internet; please try again or wait",
                        Toast.LENGTH_LONG).show();
            }
        });
        namesRequestQueue.add(namesJsonObjectRequest);
    }

    public void setUpRecycler() {
        myAdapter = new AstroInfoRecyclerAdapter(this, astroInfoList);
        astroInfoRecycler.setAdapter(myAdapter);
        astroInfoRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
    }
}
