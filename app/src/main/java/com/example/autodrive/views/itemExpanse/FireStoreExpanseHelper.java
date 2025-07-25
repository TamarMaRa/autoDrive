package com.example.autodrive.views.itemExpanse;

import android.util.Log;

import com.example.autodrive.fragments.ExpenseManagerFragment;
import com.example.autodrive.views.itemLesson.LessonItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FireStoreExpanseHelper {

    private ExpenseManagerFragment fbReply; // Reference to the fragment to handle Firestore responses
    private static final String TAG = "FireStoreExpanseHelper"; // Tag for logging
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firestore instance

    // Interface for Firestore responses (success and failure)
    public interface FBReply {
        void getAllSuccess(ArrayList<LessonItem> lessons);
        void getOneSuccess(LessonItem lesson);
    }

    // Constructor
    public FireStoreExpanseHelper(ExpenseManagerFragment fbReply) {
        this.fbReply = fbReply;
    }

    // Dynamically get the reference to the user's "my_expenses" collection
    private CollectionReference getUserExpenseCollection() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return db.collection("users").document(currentUser.getUid()).collection("my_expenses");
        } else {
            throw new IllegalStateException("No user is currently logged in");
        }
    }

    // Add a new expense to Firestore
    public void add(ExpenseItem expenseItem) {
        getUserExpenseCollection().add(expenseItem).addOnSuccessListener(documentReference -> {
            String docId = documentReference.getId(); // Log the document ID after adding
            Log.d(TAG, "DocumentSnapshot added with ID: " + docId);
        }).addOnFailureListener(e -> Log.w(TAG, "Error adding document", e)); // Log failure
    }

    // Update an existing expense in Firestore by its document ID
    public void update(String id, ExpenseItem expenseItem) {
        getUserExpenseCollection().document(id).update(
                "expense", expenseItem.getExpense(),
                "date", expenseItem.getDate(),
                "description", expenseItem.getDescription()
        ).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "DocumentSnapshot updated with ID: " + id); // Log success
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error updating document", e); // Log failure
        });
    }

    // Delete an expense from Firestore by its document ID
    public void delete(String id) {
        getUserExpenseCollection().document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot deleted with ID: " + id); // Log success
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting document", e); // Log failure
                });
    }

    // Static method to get the collection reference for expenses
    public static CollectionReference getCollectionRef() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return FirebaseFirestore.getInstance()
                    .collection("users").document(currentUser.getUid())
                    .collection("my_expenses");
        } else {
            throw new IllegalStateException("No user is currently logged in");
        }
    }
}
