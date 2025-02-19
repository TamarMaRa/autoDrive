package com.example.autodrive.views.itemLesson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autodrive.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class LessonAdapter extends FirestoreRecyclerAdapter<LessonItem, LessonAdapter.LessonViewHolder> {

    private final Context context;

    public LessonAdapter(@NonNull FirestoreRecyclerOptions<LessonItem> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull LessonViewHolder holder, int position, @NonNull LessonItem lesson) {
        holder.numLessonTextView.setText("Lesson: " + lesson.getNumLesson());
        holder.dateLessonTextView.setText("Date: " + lesson.getDateLesson());
        holder.timeLessonTextView.setText("Time: " + lesson.getTimeLesson());
    }



    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView numLessonTextView, dateLessonTextView, timeLessonTextView;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            numLessonTextView = itemView.findViewById(R.id.tvLessonNumber);
            dateLessonTextView = itemView.findViewById(R.id.tvLessonDate);
            timeLessonTextView = itemView.findViewById(R.id.tvLessonTime);
        }
    }
}
