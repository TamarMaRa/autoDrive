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

    private ExpenseManagerFragment fbReply;
    private static final String TAG = "FireStoreExpanseHelper";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    //private static CollectionReference collectionRef = db.collection("expanses").document(currentUser.getUid()).collection("my_expanses");
    private static CollectionReference collectionRef = db.collection("users").document(currentUser.getUid()).collection("my_expanses");
    public interface FBReply {
        void getAllSuccess(ArrayList<LessonItem> lessons);
        void getOneSuccess(LessonItem lesson);
    }
    public FireStoreExpanseHelper(ExpenseManagerFragment fbReply) {
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
                "expense", expenseItem.getExpense(),
                "date", expenseItem.getDate(),
                "description", expenseItem.getDescription()
        ).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "DocumentSnapshot updated with ID: " + id);
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error updating document", e);
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