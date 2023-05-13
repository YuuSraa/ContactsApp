package com.example.phonebook;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phonebook.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // view binding
    private ActivityMainBinding binding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //handle loginBtn click, start login screen
        binding.loginBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,LoginActivity.class)));


        //handle skipBtn click, start continue without login screen
        binding.skipBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DashboearUserActivity.class)));


    }
}



