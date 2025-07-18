package com.example.haminavodayaho;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.ViewHolder>{
    Context context;
    List<ModelComment> arrComment;
    public AdapterComment(Context context, List<ModelComment> arrComment) {
        this.context = context;
        this.arrComment = arrComment;
    }
    @NonNull
    @Override
    public AdapterComment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterComment.ViewHolder holder, int position) {
        ModelComment modelComment = arrComment.get(position);
        holder.username.setText(modelComment.username);
        holder.comment.setText(modelComment.content);
        holder.timestamp.setText(modelComment.timestamp);
    }

    @Override
    public int getItemCount() {
        return arrComment.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, timestamp, comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.textUsername);
            timestamp = itemView.findViewById(R.id.textTimestamp);
            comment = itemView.findViewById(R.id.textComment);
        }
    }
}
