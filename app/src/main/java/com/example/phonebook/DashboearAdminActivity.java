package com.example.phonebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.example.phonebook.databinding.ActivityDashboearAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboearAdminActivity extends AppCompatActivity {

    // view binding
    private ActivityDashboearAdminBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //arraylist to stor category
    private ArrayList categoryArrayList;

    private Button addNoteBtn  ;
    //adapter
    private AdapterCategory adapterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboearAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        addNoteBtn = findViewById(R.id.addNoteBtn);
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadCategories();

        //search text change listen, search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){
                try{
                    adapterCategory.getFilter().filter(s);
                } catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //handle click , log out
        binding.logoutBtn.setOnClickListener((View.OnClickListener) v -> {
            firebaseAuth.signOut();
            checkUser();
        });

        //handle click, open profile
        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(DashboearAdminActivity.this, ProfileActivity.class));

            }
        });


        //handle click, start category add screen
        binding.addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboearAdminActivity.this, CategoryAddActivity.class));
            }
        });


        //handle click, start contact add screen
        binding.addContactfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboearAdminActivity.this, ContactaddActivity.class));
            }
        });


        //handle click, start note add screen
        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboearAdminActivity.this, NoteActivity.class));
            }
        });


        //handle click, start contact add screen
        binding.addNotefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboearAdminActivity.this, NoteAddActivity.class));
            }
        });


    }

    private void loadCategories() {
        //init arraylist
        categoryArrayList = new ArrayList<>();

        // get all categories from firebase categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear array list before adding data into it
                categoryArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    //get data
                    modelCategory model = ds.getValue(modelCategory.class);

                    //add to arraylist
                    categoryArrayList.add(model);
                }
                    //setup adapter
                    adapterCategory = new AdapterCategory(DashboearAdminActivity.this,categoryArrayList);

                    //set adapter to recyvlerview
                    binding.categoriesRv.setAdapter(adapterCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

    }

    private void checkUser() {
        //get current user, if logged in
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            //user not logged in
            //start main screen
            startActivity(new Intent(DashboearAdminActivity.this, MainActivity.class));
            finish();
        } else {
            //logged in, get user info
            String email = firebaseUser.getEmail();

            //set in textview of toolbar
            binding.subtitleTv.setText(email);

        }
    }
}