package com.example.autodrive.views.itemLesson;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class LessonViewModel extends ViewModel {
    private final MutableLiveData<List<LessonItem>> lessons = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<LessonItem>> getLessons() {
        return lessons;
    }

    public void addLesson(LessonItem lesson) {
        List<LessonItem> currentLessons = new ArrayList<>(lessons.getValue());
        currentLessons.add(lesson);
        lessons.setValue(currentLessons);
    }
}
