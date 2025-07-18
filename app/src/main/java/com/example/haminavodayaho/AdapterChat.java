package com.example.haminavodayaho;

import static com.example.haminavodayaho.Login.USER;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;
public class AdapterChat extends  RecyclerView.Adapter<AdapterChat.viewHolder>{
    Context context;
    ArrayList<ModelChat> arrChat;
    private int lastPosition = -1;
    public AdapterChat(Context context, ArrayList<ModelChat> arrChat) {
        this.context = context;
        this.arrChat = arrChat;
    }
    @NonNull
    @Override
    public AdapterChat.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_chat, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint("setJavaScriptEnabled")
    @Override
    public void onBindViewHolder(@NonNull AdapterChat.viewHolder holder, int position) {
        if (Objects.equals(arrChat.get(position).receiver, USER)) {
            holder.msgStatus.setVisibility(View.GONE);
            holder.msgLayout.setGravity(Gravity.START);
            holder.msgStampLayout.setGravity(Gravity.START);
            holder.msgTextView.setGravity(Gravity.START);
        } else{
            holder.msgStatus.setVisibility(View.VISIBLE);
            holder.msgLayout.setGravity(Gravity.END);
            holder.msgStampLayout.setGravity(Gravity.END);
            holder.msgTextView.setGravity(Gravity.END);
        }
        try {
            String dataUrl = arrChat.get(position).dataUrl;
            if (dataUrl != null && (
                    Patterns.WEB_URL.matcher(dataUrl).matches() ||
                            dataUrl.toLowerCase().startsWith("mailto:") ||
                            dataUrl.toLowerCase().startsWith("tel:") ||
                            dataUrl.toLowerCase().startsWith("ftp://") ||
                            dataUrl.toLowerCase().startsWith("file://"))) {
                holder.msgWebView.setVisibility(View.VISIBLE);
                holder.msgTextView.setVisibility(View.GONE);
                String url = arrChat.get(position).dataUrl;

                WebSettings settings = holder.msgWebView.getSettings();
                settings.setJavaScriptEnabled(true);
                holder.msgWebView.setWebChromeClient(new WebChromeClient());
                holder.msgWebView.setWebViewClient(new WebViewClient());

                if (url.matches(".*\\.(jpg|jpeg|png|gif|webp|bmp)$")) {
                    displayImage(holder, url);
                } else if (url.matches(".*\\.(mp4|mkv|avi|mov|flv|webm)$")) {
                    displayVideo(holder, url);
                } else if (url.matches(".*\\.(mp3|wav|ogg|m4a)$")) {
                    displayAudio(holder, url);
                } else if (url.matches(".*\\.(pdf|doc|docx|xls|xlsx|ppt|pptx)$")) {
                    displayFile(holder, url);
                } else {
                    holder.msgWebView.loadUrl(url);
                }
            } else {
                holder.msgWebView.setVisibility(View.GONE);
                holder.msgTextView.setVisibility(View.VISIBLE);
                holder.msgTextView.setText(arrChat.get(position).textMessage);
            }
            holder.msgTimestamp.setText(arrChat.get(position).timestamp);
            setAnimation(holder.itemView, position);
        } catch (Exception e) {
            Log.e("error", "AdapterChat 112 Error: "+e );
        }
    }
    @Override
    public int getItemCount() {
        return arrChat.size();
    }
    public static class viewHolder extends RecyclerView.ViewHolder {
        WebView msgWebView;
        TextView msgTextView, msgTimestamp;
        LinearLayout msgLayout, msgStampLayout;
        ImageView msgStatus;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            msgWebView = itemView.findViewById(R.id.msgWebView);
            msgTextView = itemView.findViewById(R.id.msgTextView);
            msgTimestamp = itemView.findViewById(R.id.msgTimestamp);
            msgStatus = itemView.findViewById(R.id.msgStatus);
            msgLayout = itemView.findViewById(R.id.chatLayout);
            msgStampLayout = itemView.findViewById(R.id.msgStampLayout);
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
        holder.msgWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }
    private void displayImage(viewHolder holder, String imageUrl) {
        String html = "<html><body><img src='" + imageUrl + "' width='100%' height ='100%' /></body></html>";
        holder.msgWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }
    private void displayAudio(viewHolder holder, String audioUrl) {
        String html = "<html><body><audio controls autoplay>" +
                "<source src='" + audioUrl + "' type='audio/mp3'></audio></body></html>";
        holder.msgWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void displayFile(viewHolder holder, String fileUrl){
        String googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=" + fileUrl;
        WebSettings settings = holder.msgWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        holder.msgWebView.setWebChromeClient(new WebChromeClient());
        holder.msgWebView.loadUrl(googleDocsUrl);
    }
}
