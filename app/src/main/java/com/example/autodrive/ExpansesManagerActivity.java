package com.example.autodrive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ExpansesManagerActivity extends AppCompatActivity {
    Button btnViewExpanses;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expanses_manager);

        btnViewExpanses = findViewById(R.id.btn_view_expanses);
        btnViewExpanses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExpansesManagerActivity.this, ExpansesListActivity.class);
                startActivity(intent);
            }
        });

    }
}
