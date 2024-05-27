package com.example.studentmanagement.Domain;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.studentmanagement.Utils.RoleUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserRepository {
    private FirebaseFirestore db;
    private CollectionReference users;

    public UserRepository(Context ccontext) {
        db = FirebaseFirestore.getInstance();
        users = db.collection("user");
    }
    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        users = db.collection("user");
    }

    public void initUser(String email){
        DocumentReference collectionRef = users.document(FirebaseAuth.getInstance().getUid());
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("role", RoleUtil.getRole());
        collectionRef.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Success init user");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error init user:", e);
                    }
                });
    }

    public void createUser(String email){
        Query query = users.whereEqualTo("email", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        initUser(email);
                    }
                } else {
                    Log.e(TAG, "Error query:", task.getException());
                }
            }
        });
    };

    public Task<String> getNameById(String id) {
        return users.document(id)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            return document.getString("name");
                        } else {
                            return null;
                        }
                    } else {
                        throw Objects.requireNonNull(task.getException());
                        }
                });
        }
    public Task<String> getRole() {
        return users.document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            return document.getString("role");
                        } else {
                            return null;
                        }
                    } else {
                        throw Objects.requireNonNull(task.getException());
                    }
                });
    }

}
