package com.example.autodrive.views.itemLesson;

public class LessonItem {
    private int numLesson;  // Ensure this is an int
    private String dateLesson;
    private String timeLesson;

    // Empty constructor (required for Firestore)
    public LessonItem() {}

    public LessonItem(int numLesson, String dateLesson, String timeLesson) {
        this.numLesson = numLesson;
        this.dateLesson = dateLesson;
        this.timeLesson = timeLesson;
    }

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
