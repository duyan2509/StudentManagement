package com.example.studentmanagement.Utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.studentmanagement.Model.Course;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirebaseUtil {
    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("user");
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("user").document(currentUserId());
    }

    public static CollectionReference getAllUserCourses() {
        return FirebaseFirestore.getInstance().collection("user_course");
    }

    public static CollectionReference getAllCourse(){
        return FirebaseFirestore.getInstance().collection("course");
    }

    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatroom").document(chatroomId);
    }

    public static String getChatroomId(String courseId){
        return courseId;
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatroom");
    }

    public static DocumentReference getCourseById(String courseId){
        return FirebaseFirestore.getInstance().collection("course").document(courseId);
    }

    public static DocumentReference getUserById(String userId){
        return FirebaseFirestore.getInstance().collection("user").document(userId);
    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    public static StorageReference getCurrentUserPicStorageRef(String path){
        return FirebaseStorage.getInstance().getReference().child(path)
                .child(FirebaseUtil.currentUserId());
    }
}


