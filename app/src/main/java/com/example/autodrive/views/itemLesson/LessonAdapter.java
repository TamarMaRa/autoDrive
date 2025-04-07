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
        // Bind data to views
        holder.numLessonTextView.setText("Lesson: " + lesson.getNumLesson());
        holder.dateLessonTextView.setText("Date: " + lesson.getDateLesson());
        holder.timeLessonTextView.setText("Time: " + lesson.getTimeLesson());

        // Set up listeners for buttons in edit mode
        if (isEditing) {
            holder.enableEditing();
        } else {
            holder.disableEditing();
        }

        // Setup edit button listener
        holder.editButton.setOnClickListener(v -> {
            holder.enableEditing();
            isEditing = true; // Set global editing flag
        });

        // Setup save button listener
        holder.saveButton.setOnClickListener(v -> {
            // Save changes to Firestore
            LessonItem updatedLesson = new LessonItem(
                    Integer.parseInt(holder.numLessonTextView.getText().toString().replace("Lesson ", "")),
                    holder.dateLessonTextView.getText().toString(),
                    holder.timeLessonTextView.getText().toString()
            );
            String lessonId = getItem(position).getDateLesson();
            FireStoreLessonHelper helper = new FireStoreLessonHelper(null);
            helper.update(lessonId, updatedLesson);
            holder.disableEditing(); // Exit edit mode
        });

        // Setup delete button listener
        holder.deleteButton.setOnTouchListener((v, event) -> {
            String docId = this.getSnapshots().getSnapshot(position).getId();
            holder.showDeleteConfirmationDialog(docId);
            return true;
        });
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

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            numLessonTextView = itemView.findViewById(R.id.tvLessonNumber);
            dateLessonTextView = itemView.findViewById(R.id.tvLessonDate);
            timeLessonTextView = itemView.findViewById(R.id.tvLessonTime);
            deleteButton = itemView.findViewById(R.id.btnDelete);
            saveButton = itemView.findViewById(R.id.btnSave);
            editButton = itemView.findViewById(R.id.btnEDIT);
        }

        public void enableEditing() {
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            numLessonTextView.setEnabled(true);
            dateLessonTextView.setEnabled(true);
            timeLessonTextView.setEnabled(true);
        }

        public void disableEditing() {
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            numLessonTextView.setEnabled(false);
            dateLessonTextView.setEnabled(false);
            timeLessonTextView.setEnabled(false);
        }

        public void showDeleteConfirmationDialog(String lessonId) {
            new AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to delete this item?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        //String lessonId = getItem(position).getId(); // Ensure ID is correct
                        FireStoreLessonHelper helper = new FireStoreLessonHelper(null);
                        helper.delete(lessonId); // Make sure Firestore helper is correctly deleting the document
                        Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();

                        disableEditing(); // Ensure this is called to stop edit mode
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                    .show();
        }
    }
}
