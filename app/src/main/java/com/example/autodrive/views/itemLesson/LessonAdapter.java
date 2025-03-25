package com.example.autodrive.views.itemLesson;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    public class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView numLessonTextView, dateLessonTextView, timeLessonTextView;
        private ImageView ivDelete;
        private LinearLayout editButtonsLayout;
        private TextView saveButton, backButton;

        private GestureDetector gestureDetector;

        @SuppressLint("WrongViewCast")
        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            numLessonTextView = itemView.findViewById(R.id.tvLessonNumber);
            dateLessonTextView = itemView.findViewById(R.id.tvLessonDate);
            timeLessonTextView = itemView.findViewById(R.id.tvLessonTime);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            editButtonsLayout = itemView.findViewById(R.id.editButtonsLayout); // Ensure you have this in your XML
            saveButton = itemView.findViewById(R.id.ivSave); // Add these buttons in your XML layout
            backButton = itemView.findViewById(R.id.ivBack);


            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    // Show delete confirmation dialog
                //    showDeleteConfirmationDialog();
                    return super.onDoubleTap(e);
                }
            });

            ivDelete.setOnTouchListener((v, event) -> {
                gestureDetector.onTouchEvent(event);
                return true;
            });


            itemView.setOnLongClickListener(v -> {
                if (!isEditing) {
                    // Enter edit mode
                //    enableEditing();
                }
                return true; // To consume the event
            });

//            saveButton.setOnClickListener(v -> {
//                // Save changes here
//                LessonItem updatedLesson = new LessonItem(
//                        Integer.parseInt(numLessonTextView.getText().toString().replace("Lesson ", "")),
//                        dateLessonTextView.getText().toString(),
//                        timeLessonTextView.getText().toString()
//                );
//                String lessonId = getItem(getAdapterPosition()).getId();
//                FireStoreLessonHelper helper = new FireStoreLessonHelper(null);
//                helper.update(lessonId, updatedLesson);
//                disableEditing(); // Exit edit mode
//            });
//
//            backButton.setOnClickListener(v -> disableEditing()); // Cancel editing
//        }
//    }
//    private void showDeleteConfirmationDialog() {
//        new AlertDialog.Builder(context)
//                .setMessage("Are you sure you want to delete this item?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", (dialog, id) -> {
//                    // Call delete method here, passing the lesson ID
//                    String lessonId = getItem(getAdapterPosition()).getId();
//                    FireStoreLessonHelper helper = new FireStoreLessonHelper(null);
//                    helper.delete(lessonId);
//                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
//                })
//                .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
//                .show();
//    }
//
//    private void enableEditing() {
//        isEditing = true;
//        editButtonsLayout.setVisibility(View.VISIBLE);
//        tvLessonNumber.setEnabled(true);  // Enable editing for fields
//        tvLessonDate.setEnabled(true);
//        tvLessonTime.setEnabled(true);
//    }
//
//    private void disableEditing() {
//        isEditing = false;
//        editButtonsLayout.setVisibility(View.GONE);
//        tvLessonNumber.setEnabled(false);  // Disable editing for fields
//        tvLessonDate.setEnabled(false);
//        tvLessonTime.setEnabled(false);
//    }
}}}
