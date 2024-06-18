package com.example.studentmanagement;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.appcheck.AppCheckTokenResult;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ViewFileActivity extends AppCompatActivity {

    private WebView webView;
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_file);

//         Firebase initialization
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        firebaseAppCheck.getAppCheckToken(true)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d("TAG", "getAppCheckToken");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseException) {
                            Log.d("TAG", "addOnFailureListener");
                            FirebaseException firebaseException = (FirebaseException) e;
                            if (firebaseException.getMessage().contains("Too many attempts")) {
                                // Implement exponential backoff
                            } else {
                                // Handle other errors
                            }
                        }
                    }
                });

        // Views initialization
        webView = findViewById(R.id.webView);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

//         Get file URL and name from intent
        String fileUrl = getIntent().getStringExtra("fileUrl");
        String fileName = getIntent().getStringExtra("fileName");

        Log.d("ViewFileActivity", "fileUrl: " + fileUrl);

        displayFileWithGoogleDocs(fileUrl);

//         Display file based on file type
        if (fileName.endsWith(".pdf")) {
            Log.d("TAG", "pdf");
            Log.d("ViewFileActivity", "fileUrl: " + fileUrl);
            displayFileWithGoogleDocs(fileUrl);
        } else if (fileName.matches(".*\\.(png|jpg|jpeg|gif|bmp)$")) {
            displayImage(fileUrl);
        } else if (fileName.matches(".*\\.(txt|csv)$")) {
            displayText(fileUrl);
        } else {
            displayFileWithGoogleDocs(fileUrl);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void displayFileWithGoogleDocs(String fileUrl) {
        // Enable JavaScript if needed
        webView.getSettings().setJavaScriptEnabled(true);

        // Set a WebViewClient to handle the URL loading within the app
        webView.setWebViewClient(new WebViewClient());

        // Load the Google homepage
        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("WebView", "Error: " + description);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Log.e("WebView", "HTTP error: " + errorResponse.getStatusCode());
            }
        });
        String encodedUrl = "https://docs.google.com/";
        try {
            encodedUrl = URLEncoder.encode(fileUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        webView.loadUrl("https://docs.google.com/viewer?url=" + encodedUrl);
    }

    private void displayImage(String fileUrl) {
        imageView.setVisibility(View.VISIBLE);
        Picasso.get().load(fileUrl).into(imageView);
    }

    private void displayText(String fileUrl) {
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(fileUrl);
    }
}
