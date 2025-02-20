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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.autodrive.R;
import com.example.autodrive.views.itemLesson.FireStoreLessonHelper;
import com.example.autodrive.views.itemLesson.LessonItem;

public class EditLessonNote extends Fragment {

    private EditText lessonNumberInput, dateInput, timeInput;
    private Button btnAddLesson;
    private SharedPreferences sharedPreferences;
    private FireStoreLessonHelper fireStoreLessonHelper;

    public EditLessonNote() {
        // Required empty public constructor
        fireStoreLessonHelper = new FireStoreLessonHelper(this);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_lesson_note, container, false);

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("LessonPrefs", Context.MODE_PRIVATE);

        // Find UI components
        lessonNumberInput = rootView.findViewById(R.id.lesson_number_input);
        dateInput = rootView.findViewById(R.id.date_input);
        timeInput = rootView.findViewById(R.id.time_input);
        btnAddLesson = rootView.findViewById(R.id.btn_add_event);

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

        saveLesson(newLesson);
        Toast.makeText(getContext(), "Lesson added!", Toast.LENGTH_SHORT).show();
    }

    private void saveLesson(LessonItem lesson) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> lessonSet = sharedPreferences.getStringSet("lessons", new HashSet<>());
        lessonSet.add(lesson.getNumLesson() + "," + lesson.getDateLesson() + "," + lesson.getTimeLesson());
        editor.putStringSet("lessons", lessonSet);
        editor.apply();

        fireStoreLessonHelper.add(lesson);
    }

    @Override
    public void getAllSuccess(ArrayList<LessonItem> lessons) {

    }

    @Override
    public void getOneSuccess(LessonItem lesson) {

    }
}
