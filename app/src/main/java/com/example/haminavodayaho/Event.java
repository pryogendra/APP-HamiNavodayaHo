package com.example.haminavodayaho;

import static com.example.haminavodayaho.Login.USER;
import static com.example.haminavodayaho.MainActivity.MAIN_URL;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.haminavodayaho.ServerRequest._HttpPostRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Event extends Fragment {
    String url = MAIN_URL + "getAllEvent/";
    ArrayList<ModelEvent> arrEvent = new ArrayList<>();
    private ProgressBar eventProgressBar;
    private RecyclerView recyclerEvent;
    private  AdapterEvent adapterEvent;
    private FloatingActionButton eventFloatingButton;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public Event() {
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        eventProgressBar = view.findViewById(R.id.eventProgressBar);
        recyclerEvent = view.findViewById(R.id.recyclerEvent);
        eventFloatingButton = view.findViewById(R.id.eventFloatingButton);
        recyclerEvent.setLayoutManager(new LinearLayoutManager(getContext()));

        viewEvent();
        eventFloatingButton.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), CreateEvent.class));

        });
        return view;
    }
    private void viewEvent(){
        try{
            Map<String, String> data = new HashMap<>();
            data.put("phone", USER);

            new _HttpPostRequest.PostRequestTask(url, data, new _HttpPostRequest.PostRequestCallback(){
                @Override
                public void onPostRequestComplete(String response) {
                    try{
                        JsonParser parser = new JsonParser();
                        JsonObject rootObject = parser.parse(response).getAsJsonObject();
                        JsonArray jsonArray = rootObject.getAsJsonArray("events");

                        for (JsonElement element : jsonArray){
                            Log.d("taga", "onPostRequestComplete: "+element);
                            JsonObject jsonObject = element.getAsJsonObject();
                            String eventId = jsonObject.get("event_id").getAsString();
                            String profile = jsonObject.get("profile").isJsonNull() ? null : jsonObject.get("profile").getAsString();
                            String username = jsonObject.get("username").getAsString();
                            String contact = jsonObject.get("contact").getAsString();
                            String timestamp = jsonObject.get("category").getAsString();
                            String description = jsonObject.get("description").getAsString();
                            String url = jsonObject.get("url").isJsonNull() ? null : jsonObject.get("url").getAsString();
                            String dataUrl = jsonObject.get("data").isJsonNull() ? null : jsonObject.get("data").getAsString();

                            arrEvent.add(new ModelEvent(eventId, profile, username, contact, timestamp, dataUrl, description, url));
                        }
                        adapterEvent = new AdapterEvent(getContext(), arrEvent);
                        if (!arrEvent.isEmpty()){
                            eventProgressBar.setVisibility(View.GONE);
                        } else {
                            eventProgressBar.setVisibility(View.VISIBLE);
                        }
                        recyclerEvent.setAdapter(adapterEvent);
                    } catch (Exception e) {
                        Log.e("error", "Event 105 Error: "+e);
                    }
                }
            }).execute();

        } catch (Exception e) {
            Log.e("error", "Event 105 Error: "+e);
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