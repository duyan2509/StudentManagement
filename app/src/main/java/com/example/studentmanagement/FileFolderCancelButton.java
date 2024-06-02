package com.example.studentmanagement;

import android.app.NotificationChannel;
import android.app.NotificationManager;
//import android.content.ContentResolver;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.fragment.app.FragmentManager;

public class FileFolderCancelButton extends Fragment {
    String code;
    //ContentResolver contentResolver = requireContext().getContentResolver();

    private ActivityResultLauncher<String> filePickerLauncher;
    //FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference("SE104.027");

    public FileFolderCancelButton(String code) {
        // Required empty public constructor
        this.code = code;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase databaseRef = FirebaseDatabase.getInstance();
        DatabaseReference myRef = databaseRef.getReference();
        myRef.setValue("Check");

        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {

                    if (uri != null) {
                        uploadFileToFirebase(uri);
                    }
                });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "upload_channel";
            String channelName = "File Uploads";
            String channelDescription = "Notifications for file uploads";
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_folder_cancel_button, container, false);
        ImageButton Cancel = view.findViewById(R.id.Cancel);


        Cancel.setOnClickListener(v -> {
            // Load CancelFragment
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container1, new PlusButton(code))
                        .addToBackStack(null)
                        .commit();
            }
        });
        ImageButton Add_File = view.findViewById(R.id.Add_File);
        Add_File.setOnClickListener(v ->{
            //uploadFile();
            filePickerLauncher.launch("*/*");
        });

        return view;
    }



    private void uploadFileToFirebase(Uri fileUri) {
        // Lấy tên file từ Uri
        String fileName = getFileName(fileUri);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(code);
        StorageReference fileRef = storageRef.child(fileName);
        //Define Notification
        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(requireContext(), "upload_channel")
                .setSmallIcon(R.drawable.ic_upload) // Bạn có thể thay đổi icon này
                .setContentTitle("Uploading File")
                .setContentText("Upload in progress")
                .setPriority(NotificationCompat.PRIORITY_LOW);

        int notificationId = 1;
        // Tải file lên Firebase Storage
        fileRef.putFile(fileUri)
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    notificationBuilder.setProgress(100, (int) progress, false)
                            .setContentText("Uploaded " + (int) progress + "%");
                    notificationManager.notify(notificationId, notificationBuilder.build());
                })
                .addOnSuccessListener(taskSnapshot -> {
                    // Khi tải lên thành công
                    notificationBuilder.setContentText("Upload complete")
                            .setProgress(0, 0, false);
                    notificationManager.notify(notificationId, notificationBuilder.build());

                    Toast.makeText(requireContext(), "Done", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    // Khi tải lên thất bại
                    notificationBuilder.setContentText("Upload failed")
                            .setProgress(0, 0, false);
                    notificationManager.notify(notificationId, notificationBuilder.build());

                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                });
    }
    private String getFileName(Uri uri) {
        String result = null;
        if (uri != null && "content".equals(uri.getScheme())) {
            try (android.database.Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            assert uri != null;
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}