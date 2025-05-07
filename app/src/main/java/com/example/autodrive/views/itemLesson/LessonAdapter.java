package com.example.autodrive.views.itemLesson;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autodrive.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class LessonAdapter extends FirestoreRecyclerAdapter<LessonItem, LessonAdapter.LessonViewHolder> {

    private final Context context;

    // Constructor initializes the adapter with Firestore options and context
    public LessonAdapter(@NonNull FirestoreRecyclerOptions<LessonItem> options, Context context, boolean isEditing) {
        super(options);
        this.context = context;
    }

    // Bind data to the views and set up listeners for editing and deleting lessons
    @Override
    protected void onBindViewHolder(@NonNull LessonViewHolder holder, int position, @NonNull LessonItem lesson) {
        // Bind lesson data to the UI
        holder.numLessonTextView.setText(String.valueOf(lesson.getNumLesson()));
        holder.dateLessonTextView.setText(lesson.getDateLesson());
        holder.timeLessonTextView.setText(lesson.getTimeLesson());

        // Set up listeners for buttons in edit mode
        if (holder.isEditing()) {
            holder.enableEditing(); // Enable editing if the item is in editing mode
        } else {
            holder.disableEditing(); // Disable editing if the item is not in editing mode
        }

        // Edit button listener: Switch to edit mode
        holder.editButton.setOnClickListener(v -> {
            holder.enableEditing();
            holder.setEditing(true); // Set global editing flag
        });

        // Save button listener: Save changes to Firestore
        holder.saveButton.setOnClickListener(v -> {
            try {
                // Safely extract and parse lesson number
                String rawText = holder.numLessonTextView.getText().toString().replace("Lesson: ", "").trim();
                int numLesson = Integer.parseInt(rawText);

                // Create updated lesson object
                LessonItem updatedLesson = new LessonItem(
                        numLesson,
                        holder.dateLessonTextView.getText().toString(),
                        holder.timeLessonTextView.getText().toString()
                );

                // Get the correct Firestore document ID
                String lessonId = getSnapshots().getSnapshot(position).getId();
                Log.d("LessonAdapter", "Saving lesson with ID: " + lessonId); // Log document ID for reference

                // Update lesson in Firestore
                FireStoreLessonHelper helper = new FireStoreLessonHelper(null);
                helper.update(lessonId, updatedLesson); // Update the lesson in Firestore

                // Disable editing and show success message
                holder.disableEditing();
                Toast.makeText(context, "Lesson updated", Toast.LENGTH_SHORT).show();

                // Refresh the RecyclerView item
                notifyItemChanged(position); // This will update the specific item
            } catch (NumberFormatException e) {
                // Show error message if number format is invalid
                Toast.makeText(context, "Invalid lesson number format!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // Show error message for other exceptions
                Toast.makeText(context, "Error saving lesson: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Delete button listener: Confirm deletion of the lesson
        holder.deleteButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
                String docId = this.getSnapshots().getSnapshot(position).getId();
                holder.showDeleteConfirmationDialog(docId); // Show confirmation dialog
            }
            return true;
        });
    }

    // Create a view holder for lesson items
    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    // ViewHolder class to hold views for each lesson item
    public class LessonViewHolder extends RecyclerView.ViewHolder {
        EditText numLessonTextView, dateLessonTextView, timeLessonTextView;
        private Button saveButton, deleteButton, editButton;
        private boolean isEditing = false; // Track whether the item is in edit mode

        // Set editing mode flag
        public void setEditing(boolean editing) {
            isEditing = editing;
        }

        // Check if the item is in editing mode
        public boolean isEditing() {
            return isEditing;
        }

        // Constructor to initialize views
        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            numLessonTextView = itemView.findViewById(R.id.tvLessonNumber);
            dateLessonTextView = itemView.findViewById(R.id.tvLessonDate);
            timeLessonTextView = itemView.findViewById(R.id.tvLessonTime);
            deleteButton = itemView.findViewById(R.id.btnDelete);
            saveButton = itemView.findViewById(R.id.btnSave);
            editButton = itemView.findViewById(R.id.btnEDIT);
        }

        // Enable editing mode
        public void enableEditing() {
            isEditing = true;
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            numLessonTextView.setEnabled(true);
            dateLessonTextView.setEnabled(true);
            timeLessonTextView.setEnabled(true);
        }

        // Disable editing mode
        public void disableEditing() {
            isEditing = false;
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            numLessonTextView.setEnabled(false);
            dateLessonTextView.setEnabled(false);
            timeLessonTextView.setEnabled(false);
        }

        // Show confirmation dialog for deleting the lesson
        public void showDeleteConfirmationDialog(String lessonId) {
            new AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to delete this item?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        FireStoreLessonHelper helper = new FireStoreLessonHelper(null);
                        helper.delete(lessonId); // Delete the lesson from Firestore
                        Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();

                        disableEditing(); // Disable editing after deletion
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss()) // Dismiss the dialog if "No" is clicked
                    .show();
        }
    }
}
