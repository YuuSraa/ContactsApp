package com.example.phonebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.phonebook.databinding.ActivityDashboearAdminBinding;
import com.example.phonebook.databinding.ActivityDashboearUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboearUserActivity extends AppCompatActivity {

    // view binding
    private ActivityDashboearAdminBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboearAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        //handle click , log out
        binding.logoutBtn.setOnClickListener(v -> {
            firebaseAuth.signOut();
            startActivity(new Intent(this,MainActivity.class));
            finish();
        });

    }

        private void checkUser() {
            //get current user, if logged in
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser == null) {
                //user not logged in
                binding.subtitleTv.setText("Not logged in");

            } else {
                //logged in, get user info
                String email = firebaseUser.getEmail();

                //set in textview of toolbar
                binding.subtitleTv.setText(email);

            }

        }
    }