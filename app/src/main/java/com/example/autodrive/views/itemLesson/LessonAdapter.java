package com.example.autodrive.views.itemLesson;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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


    public LessonAdapter(@NonNull FirestoreRecyclerOptions<LessonItem> options, Context context, boolean isEditing) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull LessonViewHolder holder, int position, @NonNull LessonItem lesson) {
        // Bind data to views
        holder.numLessonTextView.setText(String.valueOf(lesson.getNumLesson()));
        holder.dateLessonTextView.setText(lesson.getDateLesson());
        holder.timeLessonTextView.setText(lesson.getTimeLesson());


        // Set up listeners for buttons in edit mode
        if (holder.isEditing()) {
            holder.enableEditing();
        } else {
            holder.disableEditing();
        }

        // Setup edit button listener
        holder.editButton.setOnClickListener(v -> {
            holder.enableEditing();
            holder.setEditing(true); // Set global editing flag
        });

        // Setup save button listener
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
                Log.d("LessonAdapter", "Saving lesson with ID: " + lessonId); // Add logging for document ID

                // Update lesson in Firestore
                FireStoreLessonHelper helper = new FireStoreLessonHelper(null);
                helper.update(lessonId, updatedLesson);  // Ensure this is actually being called

                // Disable editing and show a toast message
                holder.disableEditing();
                Toast.makeText(context, "Lesson updated", Toast.LENGTH_SHORT).show();

                // Force the RecyclerView to refresh the data
                notifyItemChanged(position); // This will update the specific item
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid lesson number format!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "Error saving lesson: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // Setup delete button listener
        holder.deleteButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
                String docId = this.getSnapshots().getSnapshot(position).getId();
                holder.showDeleteConfirmationDialog(docId);
            }
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
        EditText numLessonTextView, dateLessonTextView, timeLessonTextView;

        private Button saveButton, deleteButton, editButton;
        private boolean isEditing = false; // Track whether the item is in edit mode

        public void setEditing(boolean editing) {
            isEditing = editing;
        }
        public boolean isEditing() {
            return isEditing;
        }
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
            isEditing = true;
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            numLessonTextView.setEnabled(true);
            dateLessonTextView.setEnabled(true);
            timeLessonTextView.setEnabled(true);
        }
        public void disableEditing() {
            isEditing = false;
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
