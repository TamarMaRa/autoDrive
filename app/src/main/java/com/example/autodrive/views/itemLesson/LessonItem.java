package com.example.autodrive.views.itemLesson;

public class LessonItem {
    int numLesson;
    String timeLesson;
    String dateLesson;

    public LessonItem(int numLesson,String dateLesson, String timeLesson) {
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

    public String getTimeLesson() {
        return timeLesson;
    }
    public void setTimeLesson(String timeLesson) {
        this.timeLesson = timeLesson;
    }

    public String getDateLesson() {
        return dateLesson;
    }
    public void setDateLesson(String date) {
        this.dateLesson = date;
    }
}
