package com.example.haminavodayaho;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class DocVerify extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;

    private Uri fileUri;
    private TextView fileName, verifyStatus;
    private Button btnUpload, btnSubmit;
    private AppCompatButton btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doc_verify);

        fileName = findViewById(R.id.fileName);
        btnUpload = findViewById(R.id.btnUploadDoc);
        btnSubmit = findViewById(R.id.btn_submit_doc);
        verifyStatus = findViewById(R.id.verifyStatus);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setVisibility(AppCompatButton.GONE);

        btnUpload.setOnClickListener(v -> openFileChooser());

        btnSubmit.setOnClickListener(v -> {
            if (fileUri != null) {
                verifyStatus.setVisibility(TextView.VISIBLE);
                btnRefresh.setVisibility(AppCompatButton.VISIBLE);
                verifyStatus.setText("Document Submitted. \nPlease wait...");
                fileName.setVisibility(TextView.GONE);
                btnUpload.setVisibility(Button.GONE);
                btnSubmit.setVisibility(Button.GONE);
                //send to the server for verification process
                Toast.makeText(this, "Document submitted for verification!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please upload a document first.", Toast.LENGTH_SHORT).show();
            }
        });
        btnRefresh.setOnClickListener(v -> {
            //send a request to server to get the verification status
            startActivity(new Intent(getApplicationContext(), FragmentManager.class));
        });
    }
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow all file types, or change to "application/pdf", "image/*" etc.
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            String tvFileName = fileUri.getLastPathSegment();
            fileName.setText("Selected: " + tvFileName);
        }
    }
}