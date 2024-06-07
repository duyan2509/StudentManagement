package com.example.studentmanagement;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.studentmanagement.Adapter.ClassViewAdapter;
import com.example.studentmanagement.Adapter.RecentChatAdapter;
import com.example.studentmanagement.Adapter.SearchChatAdapter;
import com.example.studentmanagement.Model.ChatRoom;
import com.example.studentmanagement.Model.Course;
import com.example.studentmanagement.Model.User;
import com.example.studentmanagement.Utils.AndroidUtil;
import com.example.studentmanagement.Utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Profile extends Fragment {
    private Button btLogout;
    private Button btChangePassword;
    private ImageView profilePic;
    private TextView name;
    private TextView email;
    private TextView role;
    private RecyclerView recyclerView;
    private User currentUserModel;
    private ActivityResultLauncher<Intent> imagePickLauncher;
    private Uri selectedImageUri;
    private ClassViewAdapter adapter;


    public Profile() {
        // Required empty public constructor
    }

    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        btLogout = view.findViewById(R.id.btLogout);
        btChangePassword = view.findViewById(R.id.btChangePassword);
        profilePic = view.findViewById(R.id.profilePic);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        role = view.findViewById(R.id.role);
        recyclerView = view.findViewById(R.id.recyler_view1);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getUserData();
        setUpCourseRecycleView();

        btLogout.setOnClickListener(view1->{
            onClickLogOut();
        });

        btChangePassword.setOnClickListener(view1->{
            startActivity(new Intent(getContext(), ChangePasswordActivity.class));
        });

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext() ,selectedImageUri,profilePic);
                        }

                        if(selectedImageUri!=null) {
                            FirebaseUtil.getCurrentUserPicStorageRef("profile_pic").putFile(selectedImageUri)
                                    .addOnCompleteListener(task -> {
                                        updateToFirestore();
                                    });
                        }
                    }
                }
        );

        profilePic.setOnClickListener((v)->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        return view;
    }

    private void onClickLogOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(requireActivity(), SignInActivity.class));
        requireActivity().finish();
    }

    void updateToFirestore(){
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        AndroidUtil.showToast(getContext(),"Updated successfully");
                    }else{
                        AndroidUtil.showToast(getContext(),"Updated failed");
                    }
                });
    }

    void getUserData(){
        FirebaseUtil.getCurrentUserPicStorageRef("profile_pic").getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri  = task.getResult();
                        AndroidUtil.setProfilePic(getContext(),uri,profilePic);
                    }else{
                        Log.d("Get_Profile_Image", "User has no profile image");
                    }
                });

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            currentUserModel = task.getResult().toObject(User.class);
            if(currentUserModel.getName() != null)
                name.setText(currentUserModel.getName());
            else
                name.setText(FirebaseUtil.currentUserId());
            email.setText(currentUserModel.getEmail());
            role.setText(currentUserModel.getRole());
        });
    }

    void setUpCourseRecycleView() {
        CollectionReference courseRef = FirebaseUtil.getAllCourse();
        CollectionReference userCourseRef = FirebaseUtil.getAllUserCourses();
        List<String> courseList = new ArrayList<>();

        // Fetch all documents from the userCourseRef collection
        userCourseRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Get the course ID from the user_course document
                        Map<String, Object> data = document.getData();
                        String user_id = data.get("user_id").toString();

                        if (user_id.equals(FirebaseUtil.currentUserId())) {
                            courseList.add(data.get("course_id").toString());
                        }
                    }

                    if (!courseList.isEmpty()) {
                        Query query = courseRef.whereIn("id", courseList);

                        // Call method to set up the RecyclerView adapter
                        setUpRecyclerView(query);
                    } else {
                        // Handle case when no courses found
                        Log.d("TAG", "No courses found for the user");
                    }
                } else {
                    // Handle errors
                    Log.d("TAG", "Error getting user courses: ", task.getException());
                }
            }
        });
    }


    private void setUpRecyclerView(Query query) {
        // Create a FirestoreRecyclerOptions object with a custom query

        FirestoreRecyclerOptions<Course> options = new FirestoreRecyclerOptions.Builder<Course>()
                .setQuery(query, Course.class)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }

        adapter = new ClassViewAdapter(options, getContext());
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}