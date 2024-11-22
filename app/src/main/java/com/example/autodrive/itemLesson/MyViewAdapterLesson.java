package com.example.autodrive.itemLesson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autodrive.R;

import java.util.List;

class MyViewAdapterLessons extends RecyclerView.Adapter<MyViewAdapterLessons.MyViewHolder> {

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
        holder.timeLessonTextView.setText("Time: " + lesson.getTimeLesson());
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView numLessonTextView;
        TextView timeLessonTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            numLessonTextView = itemView.findViewById(R.id.tvLessonNumber);
            timeLessonTextView = itemView.findViewById(R.id.tvLessonTime);
        }
    }
}
