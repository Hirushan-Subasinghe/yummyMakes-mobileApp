package com.example.mobileapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddReciepeActivity extends AppCompatActivity {

    StorageReference storageReference;
    Uri image;
    MaterialButton selectImage, uploadImage;
    ImageView imageView;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            uploadImage.setEnabled(true);
                            image = result.getData().getData();
                            Glide.with(getApplicationContext()).load(image).into(imageView);
                        }
                    } else {
                        Toast.makeText(AddReciepeActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_reciepe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        storageReference = FirebaseStorage.getInstance().getReference("recipes");

        imageView = findViewById(R.id.imageView);
        selectImage = findViewById(R.id.button);
        uploadImage = findViewById(R.id.button2);

        selectImage.setOnClickListener(v -> openGallery());
        uploadImage.setOnClickListener(v -> uploadImageToFirebase());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    private void uploadImageToFirebase() {
        if (image != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

            fileReference.putFile(image)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(AddReciepeActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddReciepeActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(AddReciepeActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }
}
