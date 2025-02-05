package com.example.autodrive.views.itemLesson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autodrive.R;

import java.util.List;

public class MyViewAdapterLessons extends RecyclerView.Adapter<MyViewAdapterLessons.MyViewHolder> {

    private Context context;
    private List<LessonItem> lessonList;

    public MyViewAdapterLessons(Context context, List<LessonItem> lessonList) {
        this.context = context;
        this.lessonList = lessonList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lesson, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LessonItem lesson = lessonList.get(position);
        holder.numLessonTextView.setText("Lesson: " + lesson.getNumLesson());
        holder.dateLessonTextView.setText(lesson.getDateLesson());
        holder.timeLessonTextView.setText("Time of lesson: " + lesson.getTimeLesson());
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    public void updateLessons(List<LessonItem> newLessonList) {
        this.lessonList = newLessonList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView numLessonTextView;
        TextView dateLessonTextView;
        TextView timeLessonTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            numLessonTextView = itemView.findViewById(R.id.tvLessonNumber);
            dateLessonTextView = itemView.findViewById(R.id.tvLessonDate);
            timeLessonTextView = itemView.findViewById(R.id.tvLessonTime);
        }
    }
}
