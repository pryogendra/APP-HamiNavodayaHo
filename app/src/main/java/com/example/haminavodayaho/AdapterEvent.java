package com.example.haminavodayaho;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterEvent extends RecyclerView.Adapter<AdapterEvent.viewHolder> {
    Context context;
    ArrayList<ModelEvent> arrEvent;
    private int lastPosition = -1;
    public AdapterEvent(Context context, ArrayList<ModelEvent> arrEvent){
        this.context = context;
        this.arrEvent = arrEvent;
    }
    @NonNull
    @Override
    public AdapterEvent.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_event, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterEvent.viewHolder holder, int position) {
        String profileUrl = arrEvent.get(position).profile;
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
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("contact", arrEvent.get(position).contact);
            context.startActivity(intent);
        });
        holder.username.setText(arrEvent.get(position).username);
        holder.timestamp.setText(arrEvent.get(position).timestamp);
        holder.description.setText(arrEvent.get(position).description);
        String link = arrEvent.get(position).registerLink;
        if (link == null || link.isEmpty())
        {
            holder.registerBtn.setVisibility(View.GONE);
        }
        holder.registerBtn.setOnClickListener(v -> {
            if(link != null) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                context.startActivity(browserIntent);
            }
        });

        if (arrEvent.get(position).data != null) {
            String fileUrl = arrEvent.get(position).data;
            displayContent(holder, fileUrl);
        }
        setAnimation(holder.itemView, position);
    }
    private void displayContent(viewHolder holder, String fileUrl) {
        if (fileUrl.matches(".*\\.(mp4|webm|mkv)$")) {
            holder.dataView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            displayVideo(holder, fileUrl);
        } else if (fileUrl.matches(".*\\.(jpg|jpeg|png|gif)$")) {
            holder.dataView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            displayImage(holder, fileUrl);
        } else if (fileUrl.matches(".*\\.(pdf|doc|docx|xls|xlsx|ppt|pptx)$")) {
            holder.dataView.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            displayFile(holder, fileUrl);
        }
    }

    @Override
    public int getItemCount() {
        return arrEvent.size();
    }
    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView profile, imageView;
        WebView dataView;
        TextView username, timestamp, description, documentName;
        Button registerBtn;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.eventProfile);
            username = itemView.findViewById(R.id.eventUsername);
            timestamp = itemView.findViewById(R.id.eventTimestamp);
            dataView = itemView.findViewById(R.id.dataView);
            imageView = itemView.findViewById(R.id.imageView);
            documentName = itemView.findViewById(R.id.documentText);
            description = itemView.findViewById(R.id.eventDescription);
            registerBtn = itemView.findViewById(R.id.eventBtnLink);
        }
    }
    public void setAnimation(View viewToAnimate, int position){
        if (position > lastPosition){
            Animation alphaAnim = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.alpha_once);
            viewToAnimate.startAnimation(alphaAnim);
            lastPosition = position;
        }
    }
    private void displayVideo(viewHolder holder, String videoUrl) {
        String html = "<html><body><video width='100%' height='100%' controls autoplay>" +
                "<source src='" + videoUrl + "' type='video/mp4'></video></body></html>";
        holder.dataView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }
    private void displayImage(viewHolder holder, String imageUrl) {
        String html = "<html><body><img src='" + imageUrl + "' width='100%' height ='100%' /></body></html>";
        holder.dataView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }
    private void displayFile(viewHolder holder, String fileUrl){
        int width = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());

        ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
        params.width = width;
        params.height = height;
        holder.imageView.setLayoutParams(params);
        holder.imageView.setImageResource(R.drawable.ic_document);
        List<String> urls = Collections.singletonList(fileUrl);
        for (String url : urls) {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            holder.documentName.setText(fileName);
        }
        holder.imageView.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(fileUrl), "*/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            } catch (Exception e) {
                Log.i("error", "AdapterEvent 155 Error "+e);
            }
        });
    }
}
