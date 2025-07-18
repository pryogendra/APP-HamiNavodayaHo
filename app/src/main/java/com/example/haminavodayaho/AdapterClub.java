package com.example.haminavodayaho;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class AdapterClub extends RecyclerView.Adapter<AdapterClub.viewHolder> {
    Context context;
    ArrayList<ModelClub> arrClub;
    private int lastPosition = -1;
    public AdapterClub(Context context, ArrayList<ModelClub> arrClub){
        this.context = context;
        this.arrClub = arrClub;
    }
    @NonNull
    @Override
    public AdapterClub.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_club, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterClub.viewHolder holder, int position) {
        String profileUrl = arrClub.get(position).profile;
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
        holder.username.setText(arrClub.get(position).username);
        holder.category.setText(arrClub.get(position).category);
        holder.location.setText(arrClub.get(position).location);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("contact", arrClub.get(position).contact);
            context.startActivity(intent);
        });
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return arrClub.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView username, category, location;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.clubProfile);
            username = itemView.findViewById(R.id.clubUsername);
            category = itemView.findViewById(R.id.clubCategory);
            location = itemView.findViewById(R.id.clubLocation);
        }
    }
    public void setAnimation(View viewToAnimate, int position){
        if (position > lastPosition){
            Animation alphaAnim = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.alpha_once);
            viewToAnimate.startAnimation(alphaAnim);
            lastPosition = position;
        }
    }
}
