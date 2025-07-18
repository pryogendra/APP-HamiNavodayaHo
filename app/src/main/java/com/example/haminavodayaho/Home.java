package com.example.haminavodayaho;

import static com.example.haminavodayaho.Login.USER;
import static com.example.haminavodayaho.MainActivity.IP;
import static com.example.haminavodayaho.MainActivity.MAIN_URL;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.haminavodayaho.ServerRequest._HttpPostRequest;
import com.example.haminavodayaho.WebSocketRequest._WebSocketService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;

import okhttp3.WebSocket;

public class Home extends Fragment {

    String url = MAIN_URL + "getAllPost/";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ArrayList<ModelPost> arrPost = new ArrayList<>();
    private ProgressBar homeProgressBar;
    private RecyclerView recyclerHome;
    private AdapterHome adapterHome;
    private FloatingActionButton homeFloatingButton;
    private final BroadcastReceiver webSocketReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message != null) {
                //Log.d("taga", "Received message: " + message);
                adapterHome.onWebSocketMessageReceived(message);
            }
        }
    };
    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("com.example.haminavodayaho.WEBSOCKET_MESSAGE");
        ContextCompat.registerReceiver(requireActivity(), webSocketReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }
    @Override
    public void onStop() {
        super.onStop();
        requireActivity().unregisterReceiver(webSocketReceiver);
    }

    public Home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        homeProgressBar = view.findViewById(R.id.homeProgressBar);
        recyclerHome = view.findViewById(R.id.recyclerHome);
        recyclerHome.setLayoutManager(new LinearLayoutManager(getContext()));
        homeFloatingButton = view.findViewById(R.id.homeFloatingButton);

        viewPost();
        homeFloatingButton.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), CreatePost.class));
        });

        return view;
    }
    private void viewPost(){
        try{
            Map<String, String> data = new HashMap<>();
            data.put("phone", USER);

            new _HttpPostRequest.PostRequestTask(url, data, new _HttpPostRequest.PostRequestCallback() {
                @Override
                public void onPostRequestComplete(String response) {
                    try{
                        JsonParser parser = new JsonParser();
                        JsonObject rootObject = JsonParser.parseString(response).getAsJsonObject();
                        JsonArray jsonArray = rootObject.getAsJsonArray("posts");

                        for (JsonElement element : jsonArray){
                            JsonObject jsonObject = element.getAsJsonObject();
                            String post_id = jsonObject.get("post_id").getAsString();
                            String profile = jsonObject.get("profile").isJsonNull() ? null : jsonObject.get("profile").getAsString();
                            String username = jsonObject.get("username").getAsString();
                            String contact = jsonObject.get("contact").getAsString();
                            String timestamp = jsonObject.get("date").getAsString();
                            String description = jsonObject.get("description").getAsString();
                            String dataUrl = jsonObject.get("data").isJsonNull() ? null : jsonObject.get("data").getAsString();
                            int supportCount = jsonObject.get("support_count").getAsInt();
                            int commentCount = jsonObject.get("comment_count").getAsInt();
                            int shareCount = jsonObject.get("share_count").getAsInt();
                            boolean support = jsonObject.get("support").getAsBoolean();

                            arrPost.add(new ModelPost(post_id, profile, username, contact, timestamp, description, dataUrl, supportCount, commentCount, shareCount, support));
                        }
                        adapterHome = new AdapterHome(getContext(), arrPost);
                        if (!arrPost.isEmpty()){
                            homeProgressBar.setVisibility(View.GONE);
                        } else {
                            homeProgressBar.setVisibility(View.VISIBLE);
                        }
                        recyclerHome.setAdapter(adapterHome);
                    } catch (Exception e){
                        Log.e("error", "Home 107 Error: "+response);
                    }
                }
            }).execute();
        } catch (Exception e){
            Log.e("error", "Home 113 Error: "+e);
        }
    }
    private void openDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}