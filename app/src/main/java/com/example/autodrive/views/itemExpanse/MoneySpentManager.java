package com.example.autodrive.views.itemExpanse;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;


import java.util.Map;
public class MoneySpentManager {
    public static final String MONEY_SPENT_FIELD = "moneySpent";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DocumentReference userRef = db.collection("users").document(userId);

    public void getMoneySpent(OnReceive onReceive) {
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long amount = documentSnapshot.getLong(MONEY_SPENT_FIELD);
                onReceive.onReceive(amount != null ? amount.intValue() : 0);
            } else {
                userRef.set(Map.of(MONEY_SPENT_FIELD, 0), SetOptions.merge());
                onReceive.onReceive(0);
            }
        }).addOnFailureListener(e -> Log.e("FirestoreRead", "Error reading money", e));
    }

    public void updateMoneySpent(int newAmount) {
        userRef.update(MONEY_SPENT_FIELD, newAmount)
                .addOnSuccessListener(aVoid -> Log.d("FirestoreUpdate", "Money updated"))
                .addOnFailureListener(e -> Log.e("FirestoreUpdate", "Failed to update", e));
    }

    private static MoneySpentManager instance;
    public static MoneySpentManager getInstance() {
        return instance;
    }

    public static void init() {
        if (instance == null)
            instance = new MoneySpentManager();
    }

    public interface OnReceive {
        void onReceive(int value);
    }
}
