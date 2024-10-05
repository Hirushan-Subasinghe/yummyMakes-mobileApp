package com.example.mobileapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.firebase.firestore.FirebaseFirestore;



import java.util.ArrayList;
import java.util.List;


import backend.Ingredient;
import backend.Recipe;

public class AddReciepeActivity extends AppCompatActivity {

    StorageReference storageReference;
    Uri image, video;
    MaterialButton selectImage,selectVideo, uploadImage, addRecipe;
    ImageView imageView1, imageView2;
    LinearLayout layout;
    Button buttonAdd;
    Button buttonSubmitList;
    private FirebaseFirestore db;

    //Image upload
    private final ActivityResultLauncher<Intent> activityResultLauncherForImages = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            addRecipe.setEnabled(true);
                            image = result.getData().getData();
                            Glide.with(getApplicationContext()).load(image).into(imageView1);
                        }
                    } else {
                        Toast.makeText(AddReciepeActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    // Video upload
    private final ActivityResultLauncher<Intent> activityResultLauncherForVideos = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            addRecipe.setEnabled(true);
                            video = result.getData().getData(); // Correctly set the video variable
                            Glide.with(getApplicationContext()).load(video).into(imageView2);
                        }
                    } else {
                        Toast.makeText(AddReciepeActivity.this, "Please select a video", Toast.LENGTH_SHORT).show();
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

        //add image buttons and eventListeners
        imageView1 = findViewById(R.id.imageView);
        selectImage = findViewById(R.id.addImageButton);
        addRecipe = findViewById(R.id.button2);
        selectImage.setOnClickListener(v -> openImageGallery());

        //add video buttons and eventListeners
        imageView2 = findViewById(R.id.videoImageView);
        selectVideo = findViewById(R.id.addVideoButton);
        selectVideo.setOnClickListener(v -> openVideoGallery());

        addRecipe.setOnClickListener(v -> {
            try {
                uploadImageToFirebase();
                uploadVideoToFirebase();
                Recipe recipe = collectFormData();
                uploadRecipeToFirebase(recipe);
            }

            catch (Exception e) {
                Log.e(TAG, "Error in addRecipe OnClickListener: " + e.getMessage(), e);
                Toast.makeText(AddReciepeActivity.this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        db = FirebaseFirestore.getInstance();

        ImageButton add_button = findViewById(R.id.imageButton5);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                addButton();
            }
        });




    }
    //add add_ingredient layout dynamically
    public void addButton(){

        //Log.d(TAG, "onCreate: Activity started");
        LinearLayout layout = findViewById(R.id.linearLayout2);
        View add_ingredient_View = getLayoutInflater().inflate(R.layout.add_ingredient, layout, false);
        layout.addView(add_ingredient_View);

        //change ingredient names in TextView when adding dynamically
        EditText ingredient_editView = findViewById(R.id.editTextIngredient);
        String ingredient_name = ingredient_editView.getText().toString();
        TextView ingredient_textView = add_ingredient_View.findViewById(R.id.textViewIngredient);
        ingredient_textView.setText(ingredient_name);

        //change qunatity names in TextView when adding dynamically
        EditText Quantity_editView = findViewById(R.id.editTextQuantity);
        String Quantity_name = Quantity_editView.getText().toString();
        TextView Quantity_textView = add_ingredient_View.findViewById(R.id.textViewQuantity);
        Quantity_textView.setText(Quantity_name);

        //clear editText variables
        ingredient_editView.setText("");
        Quantity_editView.setText("");

        int id = layout.getId();


        Log.d(TAG,""+id);

        ImageButton delete_button = add_ingredient_View.findViewById(R.id.imageButton3);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                deleteButton(add_ingredient_View);
            }
        });

    }
    //remove add_ingredient layout dynamically
    public void deleteButton(View v){
        LinearLayout layout = findViewById(R.id.linearLayout2);
        if (v != null) {
            layout.removeView(v);
        }
    }


    //Open galary image for upload
    private void openImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncherForImages.launch(intent);
    }

    //Open galary video for upload
    private void openVideoGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        activityResultLauncherForVideos.launch(intent);
    }




    //Upload selected image to firebase
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

    //Upload selected video to firebase
    private void uploadVideoToFirebase() {
        if (video != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".mp4");

            fileReference.putFile(video)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(AddReciepeActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddReciepeActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(AddReciepeActivity.this, "Please select an video", Toast.LENGTH_SHORT).show();
        }
    }


    // Add this method in AddReciepeActivity.java
    private Recipe collectFormData() {
        Recipe recipe = new Recipe();

        // Collect data from form fields
        EditText nameEditText = findViewById(R.id.editTextName);
        EditText servingsEditText = findViewById(R.id.editTextServings);
        EditText timeEditText = findViewById(R.id.editTextTime);
        EditText originEditText = findViewById(R.id.editTextOrigin);
        EditText makeEditText = findViewById(R.id.editTextMake);

        String name = nameEditText.getText().toString();
        int numberOfServings = Integer.parseInt(servingsEditText.getText().toString());
        String estimatedTime = timeEditText.getText().toString();
        String origin = originEditText.getText().toString();
        String howToMake = makeEditText.getText().toString();

        // Set the collected data to the Recipe object
        recipe.setName(name);
        recipe.setNumberOfServings(numberOfServings);
        recipe.setEstimatedTime(estimatedTime);
        recipe.setOrigin(origin);
        recipe.setHowToMake(howToMake);

        // Collect ingredients
        List<Ingredient> ingredients = new ArrayList<>();
        LinearLayout ingredientsLayout = findViewById(R.id.linearLayout2);
        for (int i = 0; i < ingredientsLayout.getChildCount(); i++) {
            View ingredientView = ingredientsLayout.getChildAt(i);
            TextView ingredientTextView = ingredientView.findViewById(R.id.textViewIngredient);
            TextView quantityTextView = ingredientView.findViewById(R.id.textViewQuantity);

            String ingredientName = ingredientTextView.getText().toString();
            String quantity = quantityTextView.getText().toString();

            Ingredient ingredient = new Ingredient();
            ingredient.setName(ingredientName);
            ingredient.setQuantity(quantity);
            ingredients.add(ingredient);
        }
        recipe.setIngredients(ingredients);

        // Collect image URIs
        List<Uri> imageUris = new ArrayList<>();
        imageUris.add(image);
        recipe.setImageUris(imageUris);

        // Collect video URI
        recipe.setVideoUri(video);

        return recipe;
    }

    private void uploadRecipeToFirebase(Recipe recipe) {
        // Add a new document with a generated ID
        try {
            db.collection("recipes")
                    .add(recipe)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(AddReciepeActivity.this, "Recipe uploaded successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddReciepeActivity.this, "Error uploading recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error uploading recipe: " + e.getMessage(), e);
            Toast.makeText(AddReciepeActivity.this, "Error uploading recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
