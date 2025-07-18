package com.example.haminavodayaho;

import static com.example.haminavodayaho.Login.USER;
import static com.example.haminavodayaho.MainActivity.MAIN_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.haminavodayaho.ServerRequest._HttpPostRequest;

import java.util.HashMap;
import java.util.Map;

public class CreateEvent extends AppCompatActivity {
    String url = MAIN_URL + "createEvent/";
    ImageView eventData;
    Button btnSelect, btnSubmit;
    EditText description, link;
    private Uri selectedImageUri;
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    selectedImageUri = result.getData().getData();
                    eventData.setImageURI(selectedImageUri);
                    btnSelect.setVisibility(View.GONE);
                }
            });
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_event);

        eventData = findViewById(R.id.eventData);
        btnSelect = findViewById(R.id.selectFile);
        description = findViewById(R.id.eventDescription);
        link = findViewById(R.id.eventLink);
        btnSubmit = findViewById(R.id.btnEventSubmit);

        btnSelect.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        btnSubmit.setOnClickListener(v -> {
            String imgUrl = getImagePathFromURI(selectedImageUri);
            if (imgUrl != null && description.getText() != null){
                String des = String.valueOf(description.getText());
                String lnk = String.valueOf(link.getText());
                try{
                    Map<String, String> postData = new HashMap<>();
                    postData.put("description", des);
                    postData.put("url", lnk);
                    postData.put("data", imgUrl);
                    postData.put("phone", USER);

                    new _HttpPostRequest.PostRequestTask(url, postData, new _HttpPostRequest.PostRequestCallback() {
                        @Override
                        public void onPostRequestComplete(String response) {
                            try {
                                Toast.makeText(CreateEvent.this, "Event Added Successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CreateEvent.this, FragmentManager.class));
                            } catch (Exception e) {
                                Log.e("error", "CreateEvent 81 Error: "+e );
                            }
                        }
                    }).execute();
                } catch (Exception e) {
                    Log.e("error", "CreateEvent Error : "+e.toString() );
                }
            }
        });
    }
    public String getImagePathFromURI(Uri contentUri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }
}