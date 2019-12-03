package com.example.isslocator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AstroInfoActivity extends AppCompatActivity {

    private TextView numAstros;
    private RequestQueue queue;
    private final String URL = "http://api.open-notify.org/astros.json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_astro_info);

        numAstros = findViewById(R.id.number_astros_disp);

        queue = Volley.newRequestQueue(this);

        updateAstroInfo();

    }

    private void updateAstroInfo() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String numAstroStr;
                        try {
                            numAstroStr = response.getInt("number") + "";
                            numAstros.setText(numAstroStr + " astronauts:");
                        } catch (JSONException j) {
                            Toast.makeText(AstroInfoActivity.this, "JSON Field parsed incorrectly", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AstroInfoActivity.this, "Request failed; check internet connection", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(jsonObjectRequest);
    }
}
