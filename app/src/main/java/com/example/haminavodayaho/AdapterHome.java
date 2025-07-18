package com.example.haminavodayaho;

import static com.example.haminavodayaho.Login.USER;
import static com.example.haminavodayaho.MainActivity.IP;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.haminavodayaho.WebSocketRequest._WebSocketForegroundService;
import com.example.haminavodayaho.WebSocketRequest._WebSocketService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.WebSocket;

public class AdapterHome extends RecyclerView.Adapter<AdapterHome.ViewHolder>{
    private String getCommentUrl = "ws://"+IP+"/ws/getComments/"+USER;
    private WebSocket commentWebSocket;
    public Context context;
    ArrayList<ModelPost> arrPost;
    private AdapterComment adapterComment;
    private List<ModelComment> arrComment;
    private int lastPosition = -1;
    WebSocket customWebSocket = _WebSocketForegroundService.customWebSocket;
    public AdapterHome(Context context, ArrayList<ModelPost> arrPost) {
        this.context = context;
        this.arrPost = arrPost;
    }
    @NonNull
    @Override
    public AdapterHome.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_post, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull AdapterHome.ViewHolder holder, int position) {
        String profileUrl = arrPost.get(position).profileUrl;
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
            intent.putExtra("contact", arrPost.get(position).contact);
            context.startActivity(intent);
        });

        holder.support.setOnClickListener(v -> {
            if (customWebSocket != null) {
                try {
                    JSONObject jsonMsg = new JSONObject();
                    jsonMsg.put("type", "support");
                    jsonMsg.put("id", arrPost.get(position).id);
                    customWebSocket.send(jsonMsg.toString());

                    boolean state = arrPost.get(position).support;
                    if (!state) {
                        holder.support.setImageResource(R.drawable.ic_support_clicked);
                        int supportCount = arrPost.get(position).supportCount + 1;
                        arrPost.get(position).supportCount = supportCount;
                        holder.supportCount.setText(String.valueOf(supportCount));
                        notifyItemChanged(position);
                        arrPost.get(position).support = true;
                    } else {
                        holder.support.setImageResource(R.drawable.ic_support);
                        int supportCount = arrPost.get(position).supportCount - 1;
                        arrPost.get(position).supportCount = supportCount;
                        holder.supportCount.setText(String.valueOf(supportCount));
                        notifyItemChanged(position);
                        arrPost.get(position).support = false;
                    }
                } catch (Exception e) {
                    Log.e("taga", "AdapterHome 99 Error : ", e);
                }
            } else {
                Log.e("taga", "AdapterHome 102 Error : ");
                _WebSocketForegroundService.startService(context);
            }
        });
        holder.comment.setOnClickListener(v -> {
            String id = arrPost.get(position).id;
            try{
                if (customWebSocket != null) {
                    JSONObject jsonMsg = new JSONObject();
                    jsonMsg.put("type", "get_comment");
                    jsonMsg.put("id", id);
                    customWebSocket.send(jsonMsg.toString());
                } else {
                    _WebSocketForegroundService.startService(context);
                }
            } catch (Exception e) {
                Log.e("taga", "AdapterHome viewComments Error : ", e);
            }
            viewComments(id, holder);
        });
        holder.share.setOnClickListener(v -> {
            share(arrPost.get(position).data);
            try {
                if (customWebSocket != null) {
                    JSONObject jsonMsg = new JSONObject();
                    jsonMsg.put("type", "share");
                    jsonMsg.put("id", arrPost.get(position).id);
                    customWebSocket.send(jsonMsg.toString());

                    int shareCount = arrPost.get(position).shareCount + 1;
                    arrPost.get(position).shareCount = shareCount;
                    holder.shareCount.setText(String.valueOf(shareCount));
                    notifyItemChanged(position);
                } else {
                    _WebSocketForegroundService.startService(context);
                }
            } catch (Exception e) {
                Log.e("error", "AdapterHome share Error : ", e);
            }
        });

        holder.username.setText(arrPost.get(position).username);
        holder.timestamp.setText(arrPost.get(position).timestamp);
        holder.description.setText(arrPost.get(position).description);
        holder.supportCount.setText(String.valueOf(arrPost.get(position).supportCount));
        holder.commentCount.setText(String.valueOf(arrPost.get(position).commentCount));
        holder.shareCount.setText(String.valueOf(arrPost.get(position).shareCount));
        boolean support = arrPost.get(position).support;
        if (support){
            holder.support.setImageResource(R.drawable.ic_support_clicked);
        } else {
            holder.support.setImageResource(R.drawable.ic_support);
        }

        if (arrPost.get(position).data != null) {
            String fileUrl = arrPost.get(position).data;
            displayContent(holder, fileUrl);
        }
        setAnimation(holder.itemView, position);
    }
    private void displayContent(AdapterHome.ViewHolder holder, String fileUrl) {
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
        return arrPost.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile, support, comment, share, imageView;
        WebView dataView;
        TextView username, timestamp, description, supportCount, commentCount, shareCount, documentName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.postProfile);
            support = itemView.findViewById(R.id.postSupport);
            comment = itemView.findViewById(R.id.postComment);
            share = itemView.findViewById(R.id.postShare);
            username = itemView.findViewById(R.id.postUsername);
            timestamp = itemView.findViewById(R.id.postTimestamp);
            imageView = itemView.findViewById(R.id.imageView);
            dataView = itemView.findViewById(R.id.dataView);
            documentName = itemView.findViewById(R.id.documentText);
            description = itemView.findViewById(R.id.postDescription);
            supportCount = itemView.findViewById(R.id.postSupportCount);
            commentCount = itemView.findViewById(R.id.postCommentCount);
            shareCount = itemView.findViewById(R.id.postShareCount);
        }
    }
    public void setAnimation(View viewToAnimate, int position){
        if (position > lastPosition){
            Animation alphaAnim = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.alpha_once);
            viewToAnimate.startAnimation(alphaAnim);
            lastPosition = position;
        }
    }
    private void displayVideo(AdapterHome.ViewHolder holder, String videoUrl) {
        String html = "<html><body><video width='100%' height='100%' controls autoplay>" +
                "<source src='" + videoUrl + "' type='video/mp4'></video></body></html>";
        holder.dataView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }
    private void displayImage(AdapterHome.ViewHolder holder, String imageUrl) {
        String html = "<html><body><img src='" + imageUrl + "' width='100%' height ='100%' /></body></html>";
        holder.dataView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }
    private void displayFile(AdapterHome.ViewHolder holder, String fileUrl){
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
        }        holder.imageView.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(fileUrl), "*/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            } catch (Exception e) {
                Log.i("error", "AdapterHome 166 Error "+e);
            }
        });
    }
    private void share(String content) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = "🚀 Check out this awesome app I found! 🎉\n\n" +
                "📱 Download it now and make your life easier!\n\n" +
                "👉 https://play.google.com/store/apps/details?id=" + context.getPackageName() + "\n\n" +
                "👍 Don't forget to rate and share! 🌟\n\n\n " + content;
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        context.startActivity(Intent.createChooser(shareIntent, "Share App Via"));
    }
    @SuppressLint("NotifyDataSetChanged")
    private void viewComments(String id, ViewHolder holder){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_comment_dialog, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        RecyclerView recyclerComments = view.findViewById(R.id.recyclerComments);
        TextView textComment = view.findViewById(R.id.textComment);
        ImageView btnSendComment = view.findViewById(R.id.btnSendComment);

        arrComment = new ArrayList<>();
        adapterComment = new AdapterComment(context,arrComment);

        recyclerComments.setLayoutManager(new LinearLayoutManager(context));
        recyclerComments.setAdapter(adapterComment);
        btnSendComment.setOnClickListener(v -> {
            String newComment = textComment.getText().toString();
            if (!newComment.isEmpty()){
                if (customWebSocket != null) {
                    try {
                        JSONObject jsonMsg = new JSONObject();
                        jsonMsg.put("type", "comment");
                        jsonMsg.put("id", id);
                        jsonMsg.put("message", newComment);
                        customWebSocket.send(jsonMsg.toString());
                        textComment.setText("");

                        int commentCount = Integer.parseInt(holder.commentCount.getText().toString()) + 1;
                        holder.commentCount.setText(String.valueOf(commentCount));

                        long currentTime = System.currentTimeMillis();
                        ModelComment comment = new ModelComment(USER, newComment, String.valueOf(currentTime));
                        arrComment.add(comment);
                        adapterComment.notifyItemInserted(arrComment.size() - 1);
                        recyclerComments.scrollToPosition(arrComment.size() - 1);
                    } catch (Exception e) {
                        Log.e("taga", "AdapterHome viewComments Error : ", e);
                    }
                }
            }
        });
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onWebSocketMessageReceived(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);

            if (jsonObject.getString("type").equals("get_comment")) {
                JSONArray comments = jsonObject.getJSONArray("data");

                if (arrComment == null || adapterComment == null || !(context instanceof Activity)) {
                    Log.e("taga", "Comment list or adapter not initialized yet. Skipping update.");
                    return;
                }

                List<ModelComment> tempList = new ArrayList<>();
                for (int i = 0; i < comments.length(); i++) {
                    JSONObject comment = comments.getJSONObject(i);
                    String user = comment.getString("user");
                    String messageText = comment.getString("message");
                    String date = comment.getString("date");
                    tempList.add(new ModelComment(user, messageText, date));
                }
                ((Activity) context).runOnUiThread(() -> {
                    arrComment.clear();
                    arrComment.addAll(tempList);
                    adapterComment.notifyDataSetChanged();
                });
            }
        } catch (Exception e) {
            Log.e("error", "Error parsing JSON: " + e.getMessage());
        }

    }
}
