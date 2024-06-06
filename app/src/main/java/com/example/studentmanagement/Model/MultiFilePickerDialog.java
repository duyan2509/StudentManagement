package com.example.studentmanagement.Model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MultiFilePickerDialog {
    private final Context context;
    private final ActivityResultLauncher<String> filePickerLauncher;
    private final List<Uri> selectedFiles = new ArrayList<>();
    private final OnFilesSelectedListener listener;

    public MultiFilePickerDialog(Context context, ActivityResultLauncher<String> filePickerLauncher, OnFilesSelectedListener listener) {
        this.context = context;
        this.filePickerLauncher = filePickerLauncher;
        this.listener = listener;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Files");
        builder.setPositiveButton("Add Files", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filePickerLauncher.launch("*/*");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                listener.onFilesSelected(selectedFiles);
            }
        });
        builder.show();
    }

    public void addSelectedFile(Uri fileUri) {
        selectedFiles.add(fileUri);
    }

    public interface OnFilesSelectedListener {
        void onFilesSelected(List<Uri> selectedFiles);
    }
}
