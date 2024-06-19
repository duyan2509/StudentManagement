package com.example.studentmanagement.Adapter;



import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.ViewFileActivity;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FILE = 1;
    private static final int TYPE_FOLDER = 2;

    private List<StorageReference> filesAndFolders;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public DocumentAdapter(List<StorageReference> filesAndFolders, Context context, OnItemClickListener onItemClickListener) {
        this.filesAndFolders = filesAndFolders;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(StorageReference item);
    }

    @Override
    public int getItemViewType(int position) {
        StorageReference item = filesAndFolders.get(position);
        if (item.getName().contains(".")) {
            return TYPE_FILE;
        } else {
            return TYPE_FOLDER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_FILE) {
            View view = LayoutInflater.from(context).inflate(R.layout.file_item, parent, false);
            return new FileViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.folder_item, parent, false);
            return new FolderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StorageReference item = filesAndFolders.get(position);
        if (holder instanceof FileViewHolder) {
            ((FileViewHolder) holder).bind(item);
        } else {
            ((FolderViewHolder) holder).bind(item);
        }
    }

    @Override
    public int getItemCount() {
        return filesAndFolders.size();
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.document_text);
            itemView.setOnClickListener(v -> {
                StorageReference item = filesAndFolders.get(getAdapterPosition());
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    Intent intent = new Intent(context, ViewFileActivity.class);
                    intent.putExtra("fileUrl", uri.toString());  // Correct way to get download URL
                    Log.d("FileViewHolder", uri.toString());
                    intent.putExtra("fileName", item.getName());
                    context.startActivity(intent);
                }).addOnFailureListener(e -> {
                    // Handle error
                    Log.e("FileViewHolder", "Failed to get download URL: " + e.getMessage());
                });
            });
        }

        public void bind(StorageReference item) {
            fileName.setText(item.getName());
        }
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView folderName;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.document_text);
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(filesAndFolders.get(getAdapterPosition()));
                }
            });
        }

        public void bind(StorageReference item) {
            folderName.setText(item.getName());
        }
    }
}