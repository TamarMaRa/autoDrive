package com.example.autodrive.itemLesson;

public class LessonItem {
    int numLesson;
    String timeLesson;

    public LessonItem(int numLesson, String timeLesson) {
        this.numLesson = numLesson;
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

    @Override
    public String toString() {
        return "[" + numLesson +
                ", " + timeLesson + ']';
    }
}
