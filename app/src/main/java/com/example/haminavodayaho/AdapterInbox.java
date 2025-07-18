package com.example.haminavodayaho;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class AdapterInbox extends RecyclerView.Adapter<AdapterInbox.viewHolder> {
    Context context;
    ArrayList<ModelInbox> arrInbox;
    private int lastPosition = -1;
    private final OnItemClickListener onItemClickListener;
    private final OnItemLongClickListener onItemLongClickListener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(int position, View view);
    }
    public AdapterInbox(Context context, ArrayList<ModelInbox> arrInbox, OnItemClickListener onItemClickListener, OnItemLongClickListener onItemLongClickListener) {
        this.context = context;
        this.arrInbox = arrInbox;
        this.onItemClickListener = onItemClickListener;
        this.onItemLongClickListener = onItemLongClickListener;
    }
    @NonNull
    @Override
    public AdapterInbox.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_inbox, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterInbox.viewHolder holder, int position) {
        String profileUrl = arrInbox.get(position).profile;
        if (!TextUtils.isEmpty(profileUrl) || profileUrl != null) {
            if (profileUrl.startsWith("/")) {
                File imageFile = new File(profileUrl);
                Glide.with(holder.itemView.getContext())
                        .load(imageFile)
                        .into(holder.profile);
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(profileUrl)
                        .into(holder.profile);
            }
        }
        holder.profile.setOnClickListener(v -> {
            Uri imgUri = Uri.parse(arrInbox.get(position).profile);
            viewProfile(imgUri);
        });
        holder.username.setText(arrInbox.get(position).username);
        if (!arrInbox.get(position).timestamp.isEmpty()) {
            holder.timestamp.setText(arrInbox.get(position).timestamp);
        } else {
            holder.timestamp.setVisibility(View.GONE);
        }
        int count = arrInbox.get(position).unreadCount;
        if (count>0){
            holder.unreadDot.setVisibility(View.VISIBLE);
        }
        else{
            holder.unreadDot.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null){
                onItemLongClickListener.onItemLongClick(position, v);
            }
            return true;
        });
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return arrInbox.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView username, timestamp;
        View unreadDot;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.inboxProfile);
            username = itemView.findViewById(R.id.inboxUsername);
            timestamp = itemView.findViewById(R.id.inboxTimestamp);
            unreadDot = itemView.findViewById(R.id.inboxUnreadDot);
        }
    }
    public void setAnimation(View viewToAnimate, int position){
        if (position > lastPosition){
            Animation alphaAnim = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.alpha_once);
            viewToAnimate.startAnimation(alphaAnim);
            lastPosition = position;
        }
    }
    private void viewProfile(Uri imageUri){
        android.widget.ImageView imageView = new android.widget.ImageView(context);
        imageView.setImageURI(imageUri);
        imageView.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Profile")

                .setView(imageView)

                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();

    }
}
