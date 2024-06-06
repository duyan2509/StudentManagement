package com.example.studentmanagement.Model;

public class DocumentItem {
    private String name;
    private boolean isFolder;

    public DocumentItem(String name, boolean isFolder) {
        this.name = name;
        this.isFolder = isFolder;
    }

    public String getName() {
        return name;
    }

    public boolean isFolder() {
        return isFolder;
    }
}
