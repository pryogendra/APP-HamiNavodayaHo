package com.example.haminavodayaho;

import static com.example.haminavodayaho.MainActivity.IP;
import static com.example.haminavodayaho.MainActivity.MAIN_URL;
import static com.example.haminavodayaho.Login.USER;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.haminavodayaho.ServerRequest._HttpPostRequest;
import com.example.haminavodayaho.WebSocketRequest._WebSocketService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.WebSocket;

public class Inbox extends Fragment {
    String url = MAIN_URL + "getNameProfile/";
    String inboxWebsocketUrl = "ws://"+IP+"/ws/inbox/"+USER;
    WebSocket inboxWebSocket;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ArrayList<ModelInbox> arrInbox = new ArrayList<>();
    private ProgressBar inboxProgressBar;
    private RecyclerView recyclerInbox;
    private AdapterInbox adapterInbox;

    public Inbox() {
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
        View view =inflater.inflate(R.layout.fragment_inbox, container, false);
        inboxProgressBar = view.findViewById(R.id.inboxProgressBar);
        recyclerInbox = view.findViewById(R.id.recyclerInbox);
        recyclerInbox.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }
    private void viewInbox(String curr_user){
        try{
            inboxWebSocket = _WebSocketService.connect(inboxWebsocketUrl, new _WebSocketService.WebSocketCallback() {
                        @Override
                        public void onOpen(WebSocket webSocket) {
                            Log.d("WebSocket", "Connected successfully");
                        }
                        @Override
                        public void onMessage(WebSocket webSocket, String text) {
                            Log.d("WebSocket", "Message: " + text);
                            try {
                                JSONArray inboxArray = new JSONArray(text);
                                for (int i = 0; i < inboxArray.length(); i++) {
                                    JSONObject item = inboxArray.getJSONObject(i);
                                    String phone = item.getString("phone");
                                    String profileUrl = item.optString("profile", null);
                                    String username = item.getString("username");
                                    String lastMessage = item.getString("last_message");
                                    int unreadCount = item.getInt("unread_count");

                                    arrInbox.add(new ModelInbox(phone, profileUrl, username, lastMessage, unreadCount));
                                }
                                ((Activity) requireContext()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!arrInbox.isEmpty()) {
                                            inboxProgressBar.setVisibility(ProgressBar.GONE);
                                        } else {
                                            inboxProgressBar.setVisibility(ProgressBar.VISIBLE);
                                        }

                                        adapterInbox = new AdapterInbox(getContext(), arrInbox,
                                                new AdapterInbox.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(int position) {
                                                        Intent intent = new Intent(getContext(), Chat.class);
                                                        intent.putExtra("username", arrInbox.get(position).username);
                                                        intent.putExtra("contact", arrInbox.get(position).id);
                                                        intent.putExtra("profile", arrInbox.get(position).profile);
                                                        startActivity(intent);
                                                    }
                                                },
                                                new AdapterInbox.OnItemLongClickListener() {
                                                    @Override
                                                    public void onItemLongClick(int position, View view) {
                                                        // Long click action
                                                    }
                                                });

                                        recyclerInbox.setAdapter(adapterInbox);
                                    }
                                });
                            } catch (Exception e) {
                                Log.e("WebSocket", "Inbox Error : "+e );
                            }
                        }
                    });
        } catch (Exception e) {
            openDialog("Error", e.toString());
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

    @Override
    public void onStop() {
        super.onStop();
        if (inboxWebSocket != null) {
            inboxWebSocket.close(1000, "User left fragment");
            inboxWebSocket = null;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (inboxWebSocket == null) {
            arrInbox = new ArrayList<>();
            viewInbox(USER);
        }
    }
}