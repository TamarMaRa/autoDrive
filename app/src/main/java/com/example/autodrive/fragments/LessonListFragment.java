package com.example.autodrive.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autodrive.R;
import com.example.autodrive.views.itemLesson.LessonItem;
import com.example.autodrive.views.itemLesson.LessonAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LessonListFragment extends Fragment {

    private RecyclerView recyclerView;
    private LessonAdapter lessonAdapter;
    private List<LessonItem> lessonList;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LessonPrefs";
    private static final String LESSONS_KEY = "lessons";

    public LessonListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson_list, container, false);

        recyclerView = view.findViewById(R.id.rvNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        lessonList = loadLessons();
        lessonAdapter = new LessonAdapter(getContext(), lessonList);
        recyclerView.setAdapter(lessonAdapter);

        Button btnAddLesson = view.findViewById(R.id.btnAddNote);
        btnAddLesson.setOnClickListener(v -> updateLessons());

        return view;
    }

    private List<LessonItem> loadLessons() {
        Set<String> lessonsSet = sharedPreferences.getStringSet(LESSONS_KEY, new HashSet<>());
        List<LessonItem> lessons = new ArrayList<>();
        for (String lesson : lessonsSet) {
            String[] parts = lesson.split(",");
            if (parts.length == 3) {
                lessons.add(new LessonItem(Integer.parseInt(parts[0]), parts[1], parts[2]));
            }
        }
        return lessons;
    }

    private void updateLessons() {
        lessonList.clear();
        lessonList.addAll(loadLessons());
        lessonAdapter.notifyDataSetChanged();
    }
}
