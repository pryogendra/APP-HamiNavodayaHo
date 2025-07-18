package com.example.haminavodayaho;

import static com.example.haminavodayaho.Login.USER;
import static com.example.haminavodayaho.MainActivity.MAIN_URL;

import android.content.DialogInterface;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Club extends Fragment {
    String url = MAIN_URL + "getAllUser/";
    ArrayList<ModelClub> arrUser = new ArrayList<>();
    private ProgressBar clubProgressBar;
    private RecyclerView recyclerClub;
    private AdapterClub adapterClub;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public Club() {
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
        View view =inflater.inflate(R.layout.fragment_club, container, false);
        clubProgressBar = view.findViewById(R.id.clubProgressBar);
        recyclerClub = view.findViewById(R.id.recyclerClub);
        recyclerClub.setLayoutManager(new LinearLayoutManager(getContext()));

        viewUser();

        return view;
    }
    private void viewUser(){
        try{
            Map<String, String> data = new HashMap<>();
            data.put("phone", USER);

            new _HttpPostRequest.PostRequestTask(url, data, new _HttpPostRequest.PostRequestCallback() {
                @Override
                public void onPostRequestComplete(String response) {
                    try{
                        JsonParser parser = new JsonParser();
                        JsonObject rootObject = parser.parse(response).getAsJsonObject();
                        JsonElement usersElement = rootObject.get("users");
                        if (usersElement != null && usersElement.isJsonArray()) {
                            JsonArray jsonArray = usersElement.getAsJsonArray();
                            for (JsonElement element : jsonArray){
                                JsonObject jsonObject = element.getAsJsonObject();

                                String profile = jsonObject.get("profile").isJsonNull() ? null : jsonObject.get("profile").getAsString();
                                String username = jsonObject.get("username").getAsString();
                                String contact = jsonObject.get("contact").getAsString();
                                String category = jsonObject.get("category").isJsonNull() ? null : jsonObject.get("category").getAsString();
                                String location = jsonObject.get("location").isJsonNull() ? null : jsonObject.get("location").getAsString();

                                arrUser.add(new ModelClub(profile, username, contact, category, location));
                            }
                        }
                        adapterClub = new AdapterClub(getContext(), arrUser);
                        if (!arrUser.isEmpty()){
                            clubProgressBar.setVisibility(View.GONE);
                        } else {
                            clubProgressBar.setVisibility(View.VISIBLE);
                        }
                        recyclerClub.setAdapter(adapterClub);
                    } catch (Exception e){
                        Log.e("error", "Club 97 Error: "+e);
                    }
                }
            }).execute();
        } catch (Exception e) {
            Log.e("error", "Club 102 Error: "+e);
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