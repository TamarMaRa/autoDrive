package com.example.autodrive.views.itemLesson;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class FireStoreLessonHelper {
    private static final String TAG = "FireStoreLessonHelper";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static CollectionReference collectionRef = db.collection("lessons").document(currentUser.getUid()).collection("my_lessons");
    private FireStoreLessonHelper.FBReply fbReply;

    public interface FBReply {
        void getAllSuccess(ArrayList<LessonItem> lessons);
        void getOneSuccess(LessonItem lesson);
    }

    public FireStoreLessonHelper(FireStoreLessonHelper.FBReply fbReply) {
        this.fbReply = fbReply;
    }

    public void add(LessonItem lesson) {
        collectionRef.add(lesson).addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId())).addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public void update(String id, LessonItem lesson) {
        collectionRef.document(id).update(
                "numLesson", lesson.getNumLesson(),
                "dateLesson", lesson.getDateLesson(),
                "timeLesson", lesson.getTimeLesson()
        ).addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot updated with ID: " + id)).addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

    public void delete(String id) {
        collectionRef.document(id).delete().addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot deleted with ID: " + id)).addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }

    public void getAll() {
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<LessonItem> lessons = new ArrayList<>();
                for (com.google.firebase.firestore.DocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    LessonItem lesson = document.toObject(LessonItem.class);
                    lessons.add(lesson);
                }
                fbReply.getAllSuccess(lessons);
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
    }

    public void getOne(String id) {
        collectionRef.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                com.google.firebase.firestore.DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    LessonItem lesson = document.toObject(LessonItem.class);
                    fbReply.getOneSuccess(lesson);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    public static CollectionReference getCollectionRef() {
        return collectionRef;
    }
}