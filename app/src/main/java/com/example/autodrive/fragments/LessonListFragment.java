package com.example.autodrive.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.autodrive.R;
import com.example.autodrive.views.itemLesson.LessonItem;
import com.example.autodrive.views.itemLesson.MyViewAdapterLessons;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//TODO Ah, I see the issue now! If the RecyclerView is inside LessonListFragment,
// but you're trying to access it in TimeManagerFragment,
// then TimeManagerFragment should not be directly setting up the RecyclerView.
// Instead, LessonListFragment should handle that.

public class LessonListFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyViewAdapterLessons lessonAdapter;
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

        recyclerView = view.findViewById(R.id.recyclerViewLesson);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        lessonList = loadLessons();
        lessonAdapter = new MyViewAdapterLessons(getContext(), lessonList);
        recyclerView.setAdapter(lessonAdapter);

        Button btnAddLesson = view.findViewById(R.id.btn_add_event);
        btnAddLesson.setOnClickListener(v -> addLesson());

        return view;
    }

    private List<LessonItem> loadLessons() {
        Set<String> lessonsSet = sharedPreferences.getStringSet(LESSONS_KEY, new HashSet<>());
        List<LessonItem> lessons = new ArrayList<>();
        for (String lesson : lessonsSet) {
            String[] parts = lesson.split(";");
            if (parts.length == 3) {
                lessons.add(new LessonItem(Integer.parseInt(parts[0]), parts[1], parts[2]));
            }
        }
        return lessons;
    }

    private void saveLessons() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> lessonsSet = new HashSet<>();
        for (LessonItem lesson : lessonList) {
            lessonsSet.add(lesson.getNumLesson() + ";" + lesson.getDateLesson() + ";" + lesson.getTimeLesson());
        }
        editor.putStringSet(LESSONS_KEY, lessonsSet);
        editor.apply();
    }

    private void addLesson() {
        int newLessonNumber = lessonList.size() + 1;
        LessonItem newLesson = new LessonItem(newLessonNumber, "New Date", "New Time");
        lessonList.add(newLesson);
        lessonAdapter.notifyDataSetChanged();
        saveLessons();
    }

}
