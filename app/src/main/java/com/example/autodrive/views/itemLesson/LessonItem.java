package com.example.autodrive.views.itemLesson;

import java.util.UUID;

public class LessonItem {
    private String id;
    private int numLesson;  // Ensure this is an int
    private String dateLesson;
    private String timeLesson;

    // Empty constructor (required for Firestore)
    public LessonItem() {}

    public LessonItem(int numLesson, String dateLesson, String timeLesson) {
        this.id = UUID.randomUUID().toString(); // Generate a unique ID
        this.numLesson = numLesson;
        this.dateLesson = dateLesson;
        this.timeLesson = timeLesson;
    }

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public int getNumLesson() {
        return numLesson;
    }

    public void setNumLesson(int numLesson) {
        this.numLesson = numLesson;
    }

    public String getDateLesson() {
        return dateLesson;
    }

    public void setDateLesson(String dateLesson) {
        this.dateLesson = dateLesson;
    }

    public String getTimeLesson() {
        return timeLesson;
    }

    public void setTimeLesson(String timeLesson) {
        this.timeLesson = timeLesson;
    }
}
