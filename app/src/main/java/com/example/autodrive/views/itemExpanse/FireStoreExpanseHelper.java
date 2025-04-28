package com.example.autodrive.views.itemExpanse;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FireStoreExpanseHelper {
    private static final String TAG = "FireStoreExpanseHelper";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FBReply fbReply;

    public interface FBReply {
        void getAllSuccess(ArrayList<ExpenseItem> expanses);
        void getOneSuccess(ExpenseItem expanse);
    }

    // Constructor that accepts FBReply
    public FireStoreExpanseHelper(FBReply fbReply) {
        this.fbReply = fbReply;
    }

    // New constructor with no arguments
    public FireStoreExpanseHelper() {
        this.fbReply = null;
    }

    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private CollectionReference getUserCollection() {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("User not authenticated");
        }
        return db.collection("expanses")
                .document(user.getUid())
                .collection("my_expanses");
    }

    public void add(ExpenseItem expanse) {
        getUserCollection().add(expanse)
                .addOnSuccessListener(docRef -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + docRef.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                });
    }

    public void update(String id, ExpenseItem expense) {
        getUserCollection().document(id).update(
                "expense", expense.getExpense(),
                "date", expense.getDate(),
                "description", expense.getDescription()
        ).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "DocumentSnapshot updated with ID: " + id);
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error updating document", e);
        });
    }

    public void delete(String id) {
        getUserCollection().document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot deleted with ID: " + id);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting document", e);
                });
    }

    public CollectionReference getCollectionRef() {
        return getUserCollection();
    }
}
