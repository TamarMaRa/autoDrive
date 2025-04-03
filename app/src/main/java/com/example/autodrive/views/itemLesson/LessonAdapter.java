package com.example.autodrive.views.itemLesson;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autodrive.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class LessonAdapter extends FirestoreRecyclerAdapter<LessonItem, LessonAdapter.LessonViewHolder> {

    private final Context context;
    private boolean isEditing = false; // Track whether the item is in edit mode

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    public LessonAdapter(@NonNull FirestoreRecyclerOptions<LessonItem> options, Context context, boolean isEditing) {
        super(options);
        this.context = context;
        this.isEditing = isEditing;
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

    public class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView numLessonTextView, dateLessonTextView, timeLessonTextView;
        private LinearLayout editButtonsLayout;
        private Button saveButton, deleteButton, editButton;

        private GestureDetector gestureDetector;

        @SuppressLint("WrongViewCast")
        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            numLessonTextView = itemView.findViewById(R.id.tvLessonNumber);
            dateLessonTextView = itemView.findViewById(R.id.tvLessonDate);
            timeLessonTextView = itemView.findViewById(R.id.tvLessonTime);
            editButtonsLayout = itemView.findViewById(R.id.editButtonsLayout);

            deleteButton = itemView.findViewById(R.id.btnDelete);
            saveButton = itemView.findViewById(R.id.btnSave); // Add these buttons in your XML layout


            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    // Show delete confirmation dialog
                //    showDeleteConfirmationDialog();
                    return super.onDoubleTap(e);
                }
            });

            deleteButton.setOnTouchListener((v,event)-> {
               showDeleteConfirmationDialog();

                return true;
            });


            itemView.setOnLongClickListener(v -> {
                if (!isEditing) {
                    // Enter edit mode
                //    enableEditing();
                }
                return true; // To consume the event
            });

            saveButton.setOnClickListener(v -> {
                // Save changes here
                LessonItem updatedLesson = new LessonItem(
                        Integer.parseInt(numLessonTextView.getText().toString().replace("Lesson ", "")),
                        dateLessonTextView.getText().toString(),
                        timeLessonTextView.getText().toString()
                );
                String lessonId = getItem(getAdapterPosition()).getDateLesson();
                FireStoreLessonHelper helper = new FireStoreLessonHelper(null);
                helper.update(lessonId, updatedLesson);
                disableEditing(); // Exit edit mode
            });
            if (isEditing) {
                editButton.setOnClickListener(v -> enableEditing()); // Cancel editing
            }
        }

        private void enableEditing() {
            isEditing = true;
            editButtonsLayout.setVisibility(View.VISIBLE);
            numLessonTextView.setEnabled(true);  // Enable editing for fields
            dateLessonTextView.setEnabled(true);
            timeLessonTextView.setEnabled(true);
        }

        private void disableEditing() {
            isEditing = false;
            editButtonsLayout.setVisibility(View.GONE);
            numLessonTextView.setEnabled(false);  // Disable editing for fields
            dateLessonTextView.setEnabled(false);
            timeLessonTextView.setEnabled(false);
        }

        private void showDeleteConfirmationDialog() {
            new AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to delete this item?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        // Call delete method here, passing the lesson ID
                        String lessonId = getItem(getAdapterPosition()).getDateLesson();
                        FireStoreLessonHelper helper = new FireStoreLessonHelper(null);
                        helper.delete(lessonId);
                        Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                    .show();
        }
    }




}
