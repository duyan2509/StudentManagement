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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;


import com.example.studentmanagement.Adapter.DocumentAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.activity.result.contract.ActivityResultContracts;

import java.util.ArrayList;
import java.util.List;
//import androidx.fragment.app.FragmentManager;

public class FileFolderCancelButton extends Fragment {
    String code;

    ActivityResultLauncher<String> filePickerLauncher;
    StorageReference storageRef;
    private DocumentAdapter adapter;
    private List<StorageReference> newDataList = new ArrayList<>();

    //Thiết lập Upload Inteface

    public FileFolderCancelButton(String code,ActivityResultLauncher<String> filePickerLauncher, StorageReference storageRef,DocumentAdapter adapter) {
        // Required empty public constructor
        this.code = code;
        this.filePickerLauncher=filePickerLauncher;
        this.storageRef=storageRef;
        this.adapter=adapter;

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
                        //getDataAndUpdateRecyclerView();
                    }
                    //getDataAndUpdateRecyclerView();
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
                        .replace(R.id.container1, new PlusButton(code,filePickerLauncher,storageRef,adapter))
                        .addToBackStack(null)
                        .commit();
            }
        });
        ImageButton Add_File = view.findViewById(R.id.Add_File);
        Add_File.setOnClickListener(v ->{

            //uploadFile();
            filePickerLauncher.launch("*/*");
            //getDataAndUpdateRecyclerView();
        });


        return view;
    }
    private void getDataAndUpdateRecyclerView() {
        // Lấy dữ liệu mới từ nguồn dữ liệu nào đó (ví dụ: cơ sở dữ liệu, tập tin, ...)
        newDataList = fetchDataFromDatabaseOrFile();

        // Gọi phương thức updateRecyclerView() của FirstFragment và truyền vào newDataList
        if (getActivity() != null) {
            LectureDetailClassDocumentFragment firstFragment = (LectureDetailClassDocumentFragment) getActivity().getSupportFragmentManager().findFragmentByTag("FirstFragment");
            if (firstFragment != null) {
                firstFragment.updateRecyclerView(newDataList);
            }
        }
    }

    private List<StorageReference> fetchDataFromDatabaseOrFile() {
        newDataList.clear();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(code);
        storageRef.listAll().addOnSuccessListener(listResult -> {
            newDataList.addAll(listResult.getItems());
            newDataList.addAll(listResult.getPrefixes());
            adapter.notifyDataSetChanged();
            getDataAndUpdateRecyclerView();
        }).addOnFailureListener(e -> {
            // Handle any errors
            Toast.makeText(getContext(), "Failed to load folder contents", Toast.LENGTH_SHORT).show();
        }).addOnCompleteListener(task -> {
            // Reschedule the update after 10 seconds
            Toast.makeText(getContext(), "load folder contents", Toast.LENGTH_SHORT).show();
            //handler.postDelayed(updateRunnable, 10000);
        });
        return newDataList;
    }


    private void uploadFileToFirebase(Uri fileUri) {
        // Lấy tên file từ Uri
        String fileName = getFileName(fileUri);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(code);
        StorageReference fileRef = storageRef.child("Document/"+fileName);
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