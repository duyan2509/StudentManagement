package com.example.studentmanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentmanagement.Domain.UserRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1001;
    EditText edEmail, edPassword;
    Button btnSignIn;
    ImageButton btnSignInWithGoogle;
    GoogleSignInClient client;
    FirebaseAuth mAuth;
    TextView forgotPass;
    com.google.android.material.textfield.TextInputLayout layout_edt;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        edEmail = findViewById(R.id.edEmail);
        edEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                layout_edt=findViewById(R.id.layout_si_ed_em);

                if(!Patterns.EMAIL_ADDRESS.matcher(charSequence.toString()).matches())
                    layout_edt.setError("Input isn't email format");
                else {
                    layout_edt.setHelperText("Email is valid");
                    layout_edt.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edPassword = findViewById(R.id.edPassword);
        edPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                layout_edt=findViewById(R.id.layout_si_ed_pass);
                if (charSequence.length() >= 6) {
                    layout_edt.setHelperText("Password is valid");
                    layout_edt.setError("");
                } else {
                    layout_edt.setError("Password must 6 characters long");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this,options);
        btnSignInWithGoogle =(android.widget.ImageButton)findViewById(R.id.btSIGG);
        btnSignInWithGoogle.setOnClickListener(view-> {
            onClickSignInWithGoogle();
        });


        btnSignIn = (Button) findViewById(R.id.btSignIn);
        btnSignIn.setOnClickListener(view ->{
            onClickSignIn();
        });


        forgotPass = findViewById(R.id.forgotPass);
        forgotPass.setOnClickListener(view->{
            handleForgotPass();
        });
    }

    private void handleForgotPass() {
        View view1 = LayoutInflater.from(SignInActivity.this).inflate(R.layout.bottom_sheet_forgotpass,null);
        Button btnSendCode = view1.findViewById(R.id.btSendCode);
        EditText edtEmail = view1.findViewById(R.id.edtEmail);
        ProgressBar progressBar = view1.findViewById(R.id.prbSendCode);



        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(SignInActivity.this);
        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
        bottomSheetDialog.show();

        btnSendCode.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               String email = edtEmail.getText().toString().trim();
                                               if(TextUtils.isEmpty(email))
                                                   edtEmail.setError("Email field can't be empty");
                                               else {
                                                   ResetPassword(progressBar, btnSendCode, email);
                                                   bottomSheetDialog.dismiss();
                                               }
                                           }

                                       }
        );

    }
    private void ResetPassword(ProgressBar progressBar, Button btnSendCode, String email){
        progressBar.setVisibility(View.VISIBLE);
        btnSendCode.setVisibility(View.INVISIBLE);
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(SignInActivity.this,"Check your email!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignInActivity.this,"Error :-"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        btnSendCode.setVisibility(View.VISIBLE);
                    }

                });
    }
    private void onClickSignIn() {
        findViewById(R.id.prbSI).setVisibility(View.VISIBLE);
        btnSignIn.setVisibility(View.INVISIBLE);
        String email = edEmail.getText().toString().trim();
        String password = edPassword.getText().toString().trim();
        boolean check = true;
        if (TextUtils.isEmpty(email)){
            layout_edt=findViewById(R.id.layout_si_ed_em);
            layout_edt.setError("Email is empty!");
            check=false;
        }

        if (TextUtils.isEmpty(password)){
            layout_edt=findViewById(R.id.layout_si_ed_pass);
            layout_edt.setError("Password is empty!");
            check=false;
        }
        if(check)
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                UserRepository userRepository = new UserRepository(SignInActivity.this);
                                userRepository.createUser(email);
                                startActivity(new Intent(SignInActivity.this,UserActivity.class));
                                finishAffinity();
                            } else {
                                Toast.makeText(SignInActivity.this, "Password or Email wrong!.",
                                        Toast.LENGTH_SHORT).show();
                                findViewById(R.id.prbSI).setVisibility(View.INVISIBLE);
                                btnSignIn.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        else {
            findViewById(R.id.prbSI).setVisibility(View.INVISIBLE);
            btnSignIn.setVisibility(View.VISIBLE);
        }
    }


    private void onClickSignInWithGoogle() {
        ProgressBar progressBar = findViewById(R.id.prbGG);
        progressBar.setVisibility(View.VISIBLE);
        btnSignInWithGoogle.setVisibility(View.INVISIBLE);
        Intent i = client.getSignInIntent();
        startActivityForResult(i,1234);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1234)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String userEmail = account.getEmail();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    UserRepository userRepository = new UserRepository(SignInActivity.this);
                                    userRepository.createUser(userEmail);
                                    ProgressBar progressBar = findViewById(R.id.prbGG);


                                    Intent i = new Intent(SignInActivity.this, UserActivity.class);
                                    startActivity((i));
                                }
                                else
                                {
                                    Toast.makeText(SignInActivity.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                                    findViewById(R.id.prbGG).setVisibility(View.INVISIBLE);
                                    btnSignInWithGoogle.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }

    }


}