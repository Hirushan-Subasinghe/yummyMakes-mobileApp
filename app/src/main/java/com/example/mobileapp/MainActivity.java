package com.example.mobileapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mobileapp.databinding.ActivityMainBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //BBottom menu feature   --  Im/2021/101 start
        binding = ActivityMainBinding.inflate(getLayoutInflater());



        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        replaceFragment(new homeFr());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the selected item ID

            if (itemId == R.id.home) {
                replaceFragment(new homeFr());
            } else if (itemId == R.id.profile) {
                replaceFragment(new profileFr());
            } else if (itemId == R.id.settings) {
                Intent intent = new Intent(MainActivity.this, EmailAuthActivity.class);
                startActivity(intent);
                return false;
            }



            return true;
        });


        //Im/2021/101 end
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        // Initialize Firebase Realtime Database
//        FirebaseDatabase database = FirebaseDatabase.getInstance(BuildConfig.DATABASE_URL);
//        DatabaseReference myRef = database.getReference("message_by_ushan");
//
//        // Write data to Firebase Realtime Database
//        myRef.setValue("Hello, Firebase!");

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}