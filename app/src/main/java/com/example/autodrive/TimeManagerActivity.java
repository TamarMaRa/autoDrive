package com.example.autodrive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class TimeManagerActivity extends AppCompatActivity {
    Button btnViewLessons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_time_manager);

        btnViewLessons = findViewById(R.id.btn_view_lessons);
        btnViewLessons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimeManagerActivity.this, LessonsListActivity.class);
                startActivity(intent);
            }
        });
    }
}
