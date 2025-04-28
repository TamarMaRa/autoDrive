package com.example.autodrive.views.itemExpanse;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FireStoreExpanseHelper {
    private static final String TAG = "FireStoreExpanseHelper";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static CollectionReference collectionRef = db.collection("expanses").document(currentUser.getUid()).collection("my_expanses");
    private FireStoreExpanseHelper.FBReply fbReply;

    public interface FBReply {
        void getAllSuccess(ArrayList<ExpenseItem> expanses);
        void getOneSuccess(ExpenseItem expanse);
    }

    public FireStoreExpanseHelper(FireStoreExpanseHelper.FBReply fbReply) {
        this.fbReply = fbReply;
    }

    public void add(ExpenseItem expenseItem) {
        collectionRef.add(expenseItem).addOnSuccessListener(documentReference -> {
            String docId = documentReference.getId(); // Log the document ID
            Log.d(TAG, "DocumentSnapshot added with ID: " + docId);
        }).addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public void update(String id, ExpenseItem expenseItem) {
        collectionRef.document(id).update(
                "expanse", expenseItem.getExpense(),
                "date", expenseItem.getDate(),
                "description", expenseItem.getDescription()
        ).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "DocumentSnapshot updated with ID: " + id);
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error updating document", e);
            // You might also show a Toast to notify the user of failure
        });
    }
    public void delete(String id) {
        collectionRef.document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot deleted with ID: " + id);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting document", e);
                });
    }

    public static CollectionReference getCollectionRef() {
        return collectionRef;
    }
}

