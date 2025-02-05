package com.example.autodrive.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autodrive.R;
import com.example.autodrive.views.itemLesson.LessonItem;
import com.example.autodrive.views.itemLesson.MyViewAdapterLessons;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimeManagerFragment extends Fragment {

    private EditText lessonNumberInput, dateInput, timeInput;
    private Button btnAddLesson;
    private RecyclerView recyclerView;
    private MyViewAdapterLessons lessonAdapter;
    private List<LessonItem> lessonList;
    private SharedPreferences sharedPreferences;

    public TimeManagerFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_time_manager, container, false);

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("LessonPrefs", Context.MODE_PRIVATE);

        // Find UI components
        lessonNumberInput = rootView.findViewById(R.id.lesson_number_input);
        dateInput = rootView.findViewById(R.id.date_input);
        timeInput = rootView.findViewById(R.id.time_input);
        btnAddLesson = rootView.findViewById(R.id.btn_add_event);
        recyclerView = rootView.findViewById(R.id.recyclerViewLesson);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load lessons from SharedPreferences
        lessonList = loadLessons();
        lessonAdapter = new MyViewAdapterLessons(getContext(), lessonList);
        recyclerView.setAdapter(lessonAdapter);

        btnAddLesson.setOnClickListener(v -> addLesson());

        return rootView;
    }

    private void addLesson() {
        String lessonNumberStr = lessonNumberInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();
        String time = timeInput.getText().toString().trim();

        if (lessonNumberStr.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int lessonNumber = Integer.parseInt(lessonNumberStr);
        LessonItem newLesson = new LessonItem(lessonNumber, date, time);
        lessonList.add(newLesson);
        lessonAdapter.notifyDataSetChanged();

        saveLessons();
        Toast.makeText(getContext(), "Lesson added!", Toast.LENGTH_SHORT).show();
    }

    private void saveLessons() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> lessonSet = new HashSet<>();
        for (LessonItem lesson : lessonList) {
            lessonSet.add(lesson.getNumLesson() + "," + lesson.getDateLesson());
        }
        editor.putStringSet("lessons", lessonSet);
        editor.apply();
    }

    private List<LessonItem> loadLessons() {
        Set<String> lessonSet = sharedPreferences.getStringSet("lessons", new HashSet<>());
        List<LessonItem> lessons = new ArrayList<>();
        for (String lessonString : lessonSet) {
            String[] parts = lessonString.split(",");
            if (parts.length == 2) {
                lessons.add(new LessonItem(Integer.parseInt(parts[0]), parts[1], "Scheduled"));
            }
        }
        return lessons;
    }
}
