package com.example.studentmanagement.Adapter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.studentmanagement.Model.DocumentItem;
import com.example.studentmanagement.R;

import java.util.List;
public class DocumentViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FILE = 0;
    private static final int TYPE_FOLDER = 1;

    private List<DocumentItem> documentList;
    private Context context;

    public DocumentViewAdapter(List<DocumentItem> documentList, Context context) {
        this.documentList = documentList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        DocumentItem item = documentList.get(position);
        return item.isFolder() ? TYPE_FOLDER : TYPE_FILE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_FILE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
            return new FileViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_item, parent, false);
            return new FolderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DocumentItem item = documentList.get(position);
        if (holder instanceof FileViewHolder) {
            ((FileViewHolder) holder).fileNameTextView.setText(item.getName());
        } else if (holder instanceof FolderViewHolder) {
            ((FolderViewHolder) holder).folderNameTextView.setText(item.getName());
        }
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameTextView;

        FileViewHolder(View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.document_text);
        }
    }

    static class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView folderNameTextView;

        FolderViewHolder(View itemView) {
            super(itemView);
            folderNameTextView = itemView.findViewById(R.id.document_text);
        }
    }
}
