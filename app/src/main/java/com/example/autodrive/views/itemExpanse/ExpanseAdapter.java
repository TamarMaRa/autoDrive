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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ExpanseAdapter extends FirestoreRecyclerAdapter<ExpenseItem, ExpanseAdapter.ExpanseViewHolder> {

    private final Context context;

    public ExpanseAdapter(@NonNull FirestoreRecyclerOptions<ExpenseItem> options, Context context, boolean isEditing) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ExpanseViewHolder holder, int position, @NonNull ExpenseItem expenseItem) {
        // Bind data to views (set values for amount, date, and description)
        holder.amountTV.setText(String.valueOf(expenseItem.getExpense()));
        holder.dateExpanseTV.setText(expenseItem.getDate());
        holder.discriptionExpanseTV.setText(expenseItem.getDescription());

        // Set up listeners for buttons based on whether editing mode is enabled
        if (holder.isEditing()) {
            holder.enableEditing(); // Enable editing views if in edit mode
        } else {
            holder.disableEditing(); // Disable editing views if not in edit mode
        }

        // Setup edit button listener to allow entry into editing mode
        holder.editButton.setOnClickListener(v -> {
            holder.enableEditing();  // Enable editing UI components
            holder.setEditing(true); // Set editing flag to true
        });

        // Setup save button listener to save the updated expense
        holder.saveButton.setOnClickListener(v -> {
            try {
                // Safely extract and parse the entered amount value
                String rawAmount = holder.amountTV.getText().toString().trim();
                int numExpanse = Integer.parseInt(rawAmount);

                // Create an updated expense object to save
                ExpenseItem updatedExpanse = new ExpenseItem(
                        numExpanse,
                        holder.dateExpanseTV.getText().toString(),
                        holder.discriptionExpanseTV.getText().toString()
                );

                // Retrieve the Firestore document ID for this item
                String expenseID = getSnapshots().getSnapshot(position).getId();
                Log.d("ExpanseAdapter", "Saving expanse with ID: " + expenseID);

                // Update the expense in Firestore
                FireStoreExpanseHelper helper = new FireStoreExpanseHelper(null);
                helper.update(expenseID, updatedExpanse); // Update the document in Firestore

                // Disable editing mode and show a toast for feedback
                holder.disableEditing();
                Toast.makeText(context, "expanse updated", Toast.LENGTH_SHORT).show();

                // Refresh the item in the RecyclerView to reflect the changes
                notifyItemChanged(position);
            } catch (NumberFormatException e) {
                // Handle invalid amount format error
                Toast.makeText(context, "Invalid amount format!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // Handle general errors
                Toast.makeText(context, "Error saving expanse: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Setup delete button listener to show confirmation dialog before deleting
        holder.deleteButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick(); // Trigger click action
                String docId = this.getSnapshots().getSnapshot(position).getId(); // Get document ID
                holder.showDeleteConfirmationDialog(docId); // Show confirmation dialog for deletion
            }

            // Update the number of paid lessons when an item is deleted
            PaidLessonsManager.getInstance().getPaidLessonsNumber(value -> {
                PaidLessonsManager.getInstance().updateNumberOfLessonsPaid(value - Integer.parseInt(holder.discriptionExpanseTV.getText().toString()));
            });

            return true;
        });
    }

    @NonNull
    @Override
    public ExpanseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view for each expense item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpanseViewHolder(view); // Return the ViewHolder
    }

    public class ExpanseViewHolder extends RecyclerView.ViewHolder {
        EditText amountTV, dateExpanseTV, discriptionExpanseTV;

        private Button saveButton, deleteButton, editButton;
        private boolean isEditing = false; // Flag to track if the item is in edit mode

        // Setter for editing flag
        public void setEditing(boolean editing) {
            isEditing = editing;
        }

        // Getter for editing flag
        public boolean isEditing() {
            return isEditing;
        }

        public ExpanseViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize view components (amount, date, description, buttons)
            amountTV = itemView.findViewById(R.id.tvAmount);
            dateExpanseTV = itemView.findViewById(R.id.tvDateExpanse);
            discriptionExpanseTV = itemView.findViewById(R.id.tvDescription);
            deleteButton = itemView.findViewById(R.id.btnDelete2);
            saveButton = itemView.findViewById(R.id.btnSave2);
            editButton = itemView.findViewById(R.id.btnEDIT2);
        }

        // Method to enable editing UI components
        public void enableEditing() {
            isEditing = true;
            editButton.setVisibility(View.GONE); // Hide edit button
            deleteButton.setVisibility(View.VISIBLE); // Show delete button
            saveButton.setVisibility(View.VISIBLE); // Show save button
            amountTV.setEnabled(true); // Enable amount EditText
            dateExpanseTV.setEnabled(true); // Enable date EditText
            discriptionExpanseTV.setEnabled(true); // Enable description EditText
        }

        // Method to disable editing UI components
        public void disableEditing() {
            isEditing = false;
            editButton.setVisibility(View.VISIBLE); // Show edit button
            deleteButton.setVisibility(View.GONE); // Hide delete button
            saveButton.setVisibility(View.GONE); // Hide save button
            amountTV.setEnabled(false); // Disable amount EditText
            dateExpanseTV.setEnabled(false); // Disable date EditText
            discriptionExpanseTV.setEnabled(false); // Disable description EditText
        }

        // Show a confirmation dialog for deleting an expense item
        public void showDeleteConfirmationDialog(String expanseID) {
            new AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to delete this item?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        // Delete the expense from Firestore
                        FireStoreExpanseHelper helper = new FireStoreExpanseHelper(null);
                        helper.delete(expanseID);
                        Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();

                        // Disable editing mode after deletion
                        disableEditing();
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss()) // Dismiss dialog on No
                    .show();
        }
    }
}
