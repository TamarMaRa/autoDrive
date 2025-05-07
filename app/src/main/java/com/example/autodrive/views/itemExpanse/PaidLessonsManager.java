package com.example.autodrive.views.itemExpanse;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class PaidLessonsManager {

    public static final String NUMBER_OF_PAID_LESSONS = "numberOfPaidLessons"; // Field name in Firestore

    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firestore instance

    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the current user ID
    private DocumentReference userRef = db.collection("users").document(userId); // Reference to the user's Firestore document


    // Retrieve the number of paid lessons from Firestore
    public void getPaidLessonsNumber(OnReceive onReceive) {
        userRef.get() // Fetch user document from Firestore
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) { // Check if document exists
                            // Safely get the 'numberOfPaidLessons' field as Long
                            Long paidLessons = documentSnapshot.getLong(NUMBER_OF_PAID_LESSONS);

                            if (paidLessons != null) {
                                Log.d("FirestoreRead", "Paid lessons: " + paidLessons); // Log the value
                                onReceive.onReceive(paidLessons.intValue()); // Pass value to callback
                            } else {
                                Log.d("FirestoreRead", "Field not set yet.");
                                onReceive.onReceive(0); // If the field doesn't exist, return 0
                            }
                        } else {
                            Log.d("FirestoreRead", "Document does not exist.");
                            Map<String, Object> data = new HashMap<>();
                            data.put("numberOfPaidLessons", 0); // Set initial value for the field

                            // Create or update the document with a default value
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            db.collection("users").document(userId)
                                    .set(data, SetOptions.merge()) // Merge new data with the document
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Field added/updated"))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Error", e)); // Log errors if any
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FirestoreRead", "Failed to read document", e); // Log any failure
                    }
                });
    }

    // Update the number of paid lessons in Firestore
    public void updateNumberOfLessonsPaid(int amount) {
        userRef.update("numberOfPaidLessons", amount) // Update the number of paid lessons
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FirestoreUpdate", "Field added successfully!"); // Log success
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FirestoreUpdate", "Error adding field", e); // Log failure
                    }
                });
    }

    private static PaidLessonsManager instance; // Singleton instance

    // Get the singleton instance of PaidLessonsManager
    public static PaidLessonsManager getInstance() {
        return instance;
    }

    // Initialize the singleton instance
    public static void init() {
        if (instance == null)
            instance = new PaidLessonsManager(); // Create the instance if not already created
    }

    // Interface for callback when the value is received from Firestore
    public interface OnReceive {
        void onReceive(int value); // Callback method
    }
}
