package com.dange.roomly;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.*;

import java.io.File;
import java.util.ArrayList;

public class UploadPGActivity extends AppCompatActivity {

    private static final int LOCATION_PICKER_REQUEST = 1001;
    private static final int PICK_IMAGES = 101;

    private EditText etPgName, etCity, etDescription, etOwnerName, etOwnerPhone, pgRent;
    private RadioGroup rgPgType;
    private Button btnSubmitPG, btnUploadImages, btnPickLocation;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private String selectedType = "Both"; // Default value
    private double latitude = 0.0, longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pg);

        // Initialize all views first
        initializeViews();

        // Request permissions
        requestPermissions();

        // Set up radio group listener
        rgPgType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbBoys) {
                selectedType = "Boys";
            } else if (checkedId == R.id.rbGirls) {
                selectedType = "Girls";
            } else {
                selectedType = "Both";
            }
        });

        btnUploadImages.setOnClickListener(v -> openImagePicker());
        btnSubmitPG.setOnClickListener(v -> uploadPG());
        btnPickLocation.setOnClickListener(v -> openLocationPicker());
    }

    private void initializeViews() {
        etPgName = findViewById(R.id.etPgName);
        etCity = findViewById(R.id.etCity);
        etDescription = findViewById(R.id.etDescription);
        etOwnerName = findViewById(R.id.etOwnerName);
        etOwnerPhone = findViewById(R.id.etOwnerPhone);
        pgRent = findViewById(R.id.pgRent); // Fixed variable name (was Pg_rent)
        rgPgType = findViewById(R.id.rgPgType);
        btnSubmitPG = findViewById(R.id.btnSubmitPG);
        btnUploadImages = findViewById(R.id.btnUploadImages);
        btnPickLocation = findViewById(R.id.btnPickLocation);

        // Validate all views are initialized
        if (etPgName == null || etCity == null || etDescription == null ||
                etOwnerName == null || etOwnerPhone == null || pgRent == null) {
            Toast.makeText(this, "Error initializing form fields", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select up to 3 images"), PICK_IMAGES);
    }

    private void openLocationPicker() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent, LOCATION_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == PICK_IMAGES) {
            handleImageSelection(data);
        } else if (requestCode == LOCATION_PICKER_REQUEST) {
            handleLocationSelection(data);
        }
    }

    private void handleImageSelection(Intent data) {
        imageUris.clear();

        if (data.getClipData() != null) {
            int count = Math.min(data.getClipData().getItemCount(), 3);
            for (int i = 0; i < count; i++) {
                imageUris.add(data.getClipData().getItemAt(i).getUri());
            }
        } else if (data.getData() != null) {
            imageUris.add(data.getData());
        }

        Toast.makeText(this, imageUris.size() + " images selected", Toast.LENGTH_SHORT).show();
    }

    private void handleLocationSelection(Intent data) {
        latitude = data.getDoubleExtra("latitude", 0.0);
        longitude = data.getDoubleExtra("longitude", 0.0);
        Toast.makeText(this, "Location selected", Toast.LENGTH_SHORT).show();
    }

    private void uploadPG() {
        // Validate all fields
        if (!validateForm()) return;

        // Validate images
        if (imageUris.isEmpty()) {
            Toast.makeText(this, "Select at least one image", Toast.LENGTH_LONG).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading PG...");
        dialog.setCancelable(false);
        dialog.show();

        try {
            ApiService api = ApiClient.getClient().create(ApiService.class);

            // Prepare request parts
            MultipartBody.Part[] imageParts = prepareImageParts();
            if (imageParts == null) {
                dialog.dismiss();
                return;
            }

            // Create the API call
            // Update your API call to match the interface:
            Call<Void> call = api.uploadPG(
                    createRequestBody(etPgName.getText().toString()),
                    createRequestBody(selectedType),
                    createRequestBody(etCity.getText().toString()),
                    createRequestBody(etDescription.getText().toString()),
                    createRequestBody(etOwnerName.getText().toString()),
                    createRequestBody(etOwnerPhone.getText().toString()),
                    createRequestBody(String.valueOf(latitude)),
                    createRequestBody(String.valueOf(longitude)),
                    createRequestBody(pgRent.getText().toString()), // Added rent parameter
                    imageParts[0],
                    imageParts.length > 1 ? imageParts[1] : null,
                    imageParts.length > 2 ? imageParts[2] : null
            );

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        Toast.makeText(UploadPGActivity.this, "PG Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UploadPGActivity.this, "Upload failed: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(UploadPGActivity.this, "Network failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            dialog.dismiss();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateForm() {
        if (etPgName.getText().toString().trim().isEmpty() ||
                etCity.getText().toString().trim().isEmpty() ||
                etDescription.getText().toString().trim().isEmpty() ||
                etOwnerName.getText().toString().trim().isEmpty() ||
                etOwnerPhone.getText().toString().trim().isEmpty() ||
                pgRent.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private MultipartBody.Part[] prepareImageParts() {
        MultipartBody.Part[] imageParts = new MultipartBody.Part[imageUris.size()];

        for (int i = 0; i < imageUris.size(); i++) {
            File file = FileUtils.getFile(this, imageUris.get(i));
            if (file == null || !file.exists()) {
                Toast.makeText(this, "Invalid image selected", Toast.LENGTH_LONG).show();
                return null;
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            imageParts[i] = MultipartBody.Part.createFormData("images[]", file.getName(), requestFile);
        }

        return imageParts;
    }

    private RequestBody createRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }
}