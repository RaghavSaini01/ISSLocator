package com.example.isslocator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PassOverISSActivity extends AppCompatActivity {

    private EditText latEdit, longEdit;
    private Button calcFlyOver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_over_iss);

        latEdit = findViewById(R.id.lat_edit);
        longEdit = findViewById(R.id.long_edit);
        calcFlyOver = findViewById(R.id.calc_flyover_button);


        calcFlyOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PassOverISSActivity.this, "Will calculate flyover times using " +
                        "latitude and longitude.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
