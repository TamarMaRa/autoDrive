package com.example.autodrive.views.itemLesson;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FireStoreLessonHelper {
    private static final String TAG = "FireStoreLessonHelper"; // Log tag for debugging
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firestore instance

    private FireStoreLessonHelper.FBReply fbReply; // Callback interface to handle responses from Firestore

    // Interface for handling success responses when retrieving lessons from Firestore
    public interface FBReply {
        void getAllSuccess(ArrayList<LessonItem> lessons); // Success callback for fetching all lessons
        void getOneSuccess(LessonItem lesson); // Success callback for fetching a single lesson
    }

    // Constructor to initialize FireStoreLessonHelper with a callback implementation
    public FireStoreLessonHelper(FireStoreLessonHelper.FBReply fbReply) {
        this.fbReply = fbReply;
    }

    // Dynamically get the reference to the user's "my_lessons" collection
    private CollectionReference getUserLessonCollection() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return db.collection("users").document(currentUser.getUid()).collection("my_lessons");
        } else {
            throw new IllegalStateException("No user is currently logged in");
        }
    }

    // Add a new lesson to Firestore
    public void add(LessonItem lesson) {
        getUserLessonCollection().add(lesson)
                .addOnSuccessListener(documentReference -> {
                    String docId = documentReference.getId(); // Get the ID of the added document
                    Log.d(TAG, "DocumentSnapshot added with ID: " + docId); // Log the document ID for reference
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e)); // Log any error that occurs
    }

    // Update an existing lesson in Firestore
    public void update(String id, LessonItem lesson) {
        getUserLessonCollection().document(id).update(
                "numLesson", lesson.getNumLesson(), // Update the number of lessons
                "dateLesson", lesson.getDateLesson(), // Update the lesson date
                "timeLesson", lesson.getTimeLesson() // Update the lesson time
        ).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "DocumentSnapshot updated with ID: " + id); // Log success
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error updating document", e); // Log failure if the update fails
        });
    }

    // Delete a lesson from Firestore
    public void delete(String id) {
        getUserLessonCollection().document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot deleted with ID: " + id); // Log successful deletion
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting document", e); // Log failure if the deletion fails
                });
    }

    // Get the reference to the "my_lessons" collection (for external static access if needed)
    public static CollectionReference getCollectionRef() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return FirebaseFirestore.getInstance()
                    .collection("users").document(currentUser.getUid())
                    .collection("my_lessons");
        } else {
            throw new IllegalStateException("No user is currently logged in");
        }
    }
}
