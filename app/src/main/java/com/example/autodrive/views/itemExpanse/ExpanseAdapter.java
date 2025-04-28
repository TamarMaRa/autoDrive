package com.example.autodrive.views.itemExpanse;

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
import com.example.autodrive.views.itemLesson.LessonItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
public class ExpanseAdapter extends FirestoreRecyclerAdapter<ExpenseItem, ExpanseAdapter.ExpanseViewHolder> {

    private final Context context;

    public ExpanseAdapter(@NonNull FirestoreRecyclerOptions<ExpenseItem> options, Context context, boolean isEditing) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull com.example.autodrive.views.itemExpanse.ExpanseAdapter.ExpanseViewHolder holder, int position, @NonNull ExpenseItem expenseItem) {
        // Bind data to views
        holder.amountTV.setText(String.valueOf(expenseItem.getExpense()));
        holder.dateExpanseTV.setText(expenseItem.getDate());
        holder.discriptionExpanseTV.setText(expenseItem.getDescription());


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
                String rawText = holder.dateExpanseTV.getText().toString().replace("Date: ", "").trim();
                int numExpanse = Integer.parseInt(rawText);

                // Create updated lesson object
                ExpenseItem updatedExpanse = new ExpenseItem(
                        numExpanse,
                        holder.dateExpanseTV.getText().toString(),
                        holder.discriptionExpanseTV.getText().toString()
                );

                // Get the correct Firestore document ID
                String expenseID = getSnapshots().getSnapshot(position).getId();
                Log.d("ExpanseAdapter", "Saving expanse with ID: " + expenseID); // Add logging for document ID

                // Update expanse in Firestore
                FireStoreExpanseHelper helper = new FireStoreExpanseHelper(null);
                helper.update(expenseID, updatedExpanse);  // Ensure this is actually being called

                // Disable editing and show a toast message
                holder.disableEditing();
                Toast.makeText(context, "expanse updated", Toast.LENGTH_SHORT).show();

                // Force the RecyclerView to refresh the data
                notifyItemChanged(position); // This will update the specific item
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid expanse number format!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "Error saving expanse: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public ExpanseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new com.example.autodrive.views.itemExpanse.ExpanseAdapter.ExpanseViewHolder(view);
    }

    public class ExpanseViewHolder extends RecyclerView.ViewHolder {
        EditText amountTV, dateExpanseTV, discriptionExpanseTV;

        private Button saveButton, deleteButton, editButton;
        private boolean isEditing = false; // Track whether the item is in edit mode

        public void setEditing(boolean editing) {
            isEditing = editing;
        }
        public boolean isEditing() {
            return isEditing;
        }
        public ExpanseViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTV = itemView.findViewById(R.id.tvAmount);
            dateExpanseTV = itemView.findViewById(R.id.tvDateExpanse);
            discriptionExpanseTV = itemView.findViewById(R.id.tvDescription);
            deleteButton = itemView.findViewById(R.id.btnDelete2);
            saveButton = itemView.findViewById(R.id.btnSave2);
            editButton = itemView.findViewById(R.id.btnEDIT2);
        }
        public void enableEditing() {
            isEditing = true;
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            amountTV.setEnabled(true);
            dateExpanseTV.setEnabled(true);
            discriptionExpanseTV.setEnabled(true);
        }
        public void disableEditing() {
            isEditing = false;
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            amountTV.setEnabled(false);
            dateExpanseTV.setEnabled(false);
            discriptionExpanseTV.setEnabled(false);
        }

        public void showDeleteConfirmationDialog(String expanseID) {
            new AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to delete this item?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        //String lessonId = getItem(position).getId(); // Ensure ID is correct
                        FireStoreExpanseHelper helper = new FireStoreExpanseHelper(null);
                        helper.delete(expanseID); // Make sure Firestore helper is correctly deleting the document
                        Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();

                        disableEditing(); // Ensure this is called to stop edit mode
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                    .show();
        }
    }
}
