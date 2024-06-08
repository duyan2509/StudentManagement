package com.example.studentmanagement.Model;

import android.net.Uri;

public class SubmitItem {
    private String fileName;
    private long fileSize;
    private Uri fileUri;

    public SubmitItem(String fileName, long fileSize, Uri fileUri) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileUri = fileUri;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public Uri getFileUri() {
        return fileUri;
    }
}
