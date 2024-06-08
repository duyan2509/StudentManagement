package com.example.studentmanagement.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Model.SubmitItem;
import com.example.studentmanagement.R;

import java.util.List;

public class SubmitItemAdapter extends RecyclerView.Adapter<SubmitItemAdapter.FileItemViewHolder> {

    private List<SubmitItem> fileQueue;
    private Context context;

    public SubmitItemAdapter(List<SubmitItem> fileQueue, Context context) {
        this.fileQueue = fileQueue;
        this.context = context;
    }

    @NonNull
    @Override
    public FileItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_item, parent, false);
        return new FileItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileItemViewHolder holder, int position) {
        SubmitItem fileItem = fileQueue.get(position);
        holder.bind(fileItem);
    }

    @Override
    public int getItemCount() {
        return fileQueue.size();
    }

    public void add(SubmitItem submitItem) {
        fileQueue.add(submitItem);
        notifyDataSetChanged();
    }

    static class FileItemViewHolder extends RecyclerView.ViewHolder {

        TextView fileNameTextView;
        //TextView fileSizeTextView;

        public FileItemViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.document_text);
            //fileSizeTextView = itemView.findViewById(R.id.document_description);
        }

        public void bind(SubmitItem fileItem) {
            fileNameTextView.setText(fileItem.getFileName());
            //fileSizeTextView.setText(formatFileSize(fileItem.getFileSize()));
        }

        private String formatFileSize(long size) {
            // Implement logic to format file size (e.g., KB, MB, GB)
            return "";
        }
    }
}