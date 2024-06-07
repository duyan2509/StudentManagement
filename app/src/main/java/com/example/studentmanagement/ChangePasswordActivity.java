package com.example.studentmanagement;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagement.Model.User;
import com.example.studentmanagement.Utils.AndroidUtil;
import com.example.studentmanagement.Utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    ImageView backButton;
    EditText currentPasswordET;
    EditText newPasswordET;
    EditText confirmPasswordET;
    Button updatePassword;
    ProgressBar progressBar;
    User currentUserModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getUserData();

        backButton = findViewById(R.id.backImageView);
        currentPasswordET = findViewById(R.id.currentPasswordET);
        newPasswordET = findViewById(R.id.newPasswordET);
        confirmPasswordET = findViewById(R.id.confirmPasswordET);
        updatePassword = findViewById(R.id.updatePasswordButton);
        progressBar = findViewById(R.id.progressBar);
        setInProgress(false);

        backButton.setOnClickListener((v -> {
            finish();
        }));

        updatePassword.setOnClickListener((v -> {
            updatePasswordClick();
        }));
    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updatePassword.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            updatePassword.setVisibility(View.VISIBLE);
        }
    }

    void updatePasswordClick(){
        String currentPassword = currentPasswordET.getText().toString();
        String newPassword = newPasswordET.getText().toString();
        String confirmPassword = confirmPasswordET.getText().toString();

        if(currentPassword.isEmpty()){
            currentPasswordET.setError("Enter your current password...");
            return;
        }else if (!currentUserModel.getPassword().equals(currentPassword)){
            currentPasswordET.setError("Wrong password");
            return;
        }

        if(newPassword.length() < 6){
            newPasswordET.setError("Password length must be longer than 6 characters...");
            return;
        }

        if(!confirmPassword.equals(newPassword)){
            confirmPasswordET.setError("Passwords must be same");
            return;
        }

        currentUserModel.setPassword(newPassword);
        FirebaseUser user = FirebaseUtil.currentUser();
        Log.d("PASSWORD", "Password updated " + user);
        AuthCredential credential = EmailAuthProvider
                .getCredential(currentUserModel.getEmail(), currentPassword);
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("PASSWORD", "Password updated");
                                    } else {
                                        Log.d("PASSWORD", "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            Log.d("PASSWORD", "Error auth failed");
                        }
                    }
                });
        setInProgress(true);
        updateToFirestore();
    }

    void getUserData() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            currentUserModel = task.getResult().toObject(User.class);
        });
    }

    void updateToFirestore(){
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if(task.isSuccessful()){
                        AndroidUtil.showToast(this,"Updated successfully");
                        clearEditTextFields();
                    }else{
                        AndroidUtil.showToast(this,"Updated failed");
                    }
                });
    }

    void clearEditTextFields() {
        currentPasswordET.getText().clear();
        newPasswordET.getText().clear();
        confirmPasswordET.getText().clear();
    }
}