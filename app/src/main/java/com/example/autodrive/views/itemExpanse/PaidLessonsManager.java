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

    public static final String NUMBER_OF_PAID_LESSONS = "numberOfPaidLessons";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // or a known user ID
    private DocumentReference userRef = db.collection("users").document(userId);


    public void getPaidLessonsNumber(OnReceive onReceive) {
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Safely get the field as Long or Integer
                            Long paidLessons = documentSnapshot.getLong(NUMBER_OF_PAID_LESSONS);

                            if (paidLessons != null) {
                                Log.d("FirestoreRead", "Paid lessons: " + paidLessons);

                                onReceive.onReceive(paidLessons.intValue());
                            } else {
                                Log.d("FirestoreRead", "Field not set yet.");
                                onReceive.onReceive(0);

                            }
                        } else {
                            Log.d("FirestoreRead", "Document does not exist.");
                            Map<String, Object> data = new HashMap<>();
                            data.put("numberOfPaidLessons", 0); // or any value

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // or known ID

                            db.collection("users").document(userId)
                                    .set(data, SetOptions.merge())  // This will create or update the document safely
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Field added/updated"))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Error", e));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FirestoreRead", "Failed to read document", e);
                    }
                });
    }

    public void updateNumberOfLessonsPaid(int amount) {
        userRef.update("numberOfPaidLessons", amount)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FirestoreUpdate", "Field added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FirestoreUpdate", "Error adding field", e);
                    }
                });

    }

    private static PaidLessonsManager instance;

    public static PaidLessonsManager getInstance() {
        return instance;
    }

    public static void init() {
        if (instance == null)
            instance = new PaidLessonsManager();
    }

    public interface OnReceive {
        void onReceive(int value);
    }
}
