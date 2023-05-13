package com.example.phonebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.phonebook.databinding.ActivityDashboearAdminBinding;
import com.example.phonebook.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    // view binding
    private ActivityProfileBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //array list to handle favorite a=contact
    private ArrayList<modelContact> contactArrayList;

    private AdapterFavorite adapterFavorite ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadUseInfo();
        loadfavoriteContacts();



        //handle backBtn
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void loadUseInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get info of user here
                        String email = ""+snapshot.child("email").getValue();
                        String name = ""+snapshot.child("name").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();


                        //set data to ui
                        binding.emailTv.setText(email);
                        binding.nameTv.setText(name);

                        //set image, using glide
                        Glide.with(ProfileActivity.this)
                                .load(profileImage)
                                .placeholder(R.drawable.baseline_person_24)
                                .into(binding.profileIv);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadfavoriteContacts() {
        //init list
        contactArrayList = new ArrayList<>();
        //load favorite contact
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(firebaseAuth.getUid()).child("Favorites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        contactArrayList.clear();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            String contactId =""+ds.child("contactId").getValue();


                            //set id to model
                            modelContact modelcontact = new modelContact();
                            modelcontact.setId(contactId);

                            contactArrayList.add(modelcontact);
                        }

                        //set up adapter
                        adapterFavorite = new AdapterFavorite(ProfileActivity.this, contactArrayList);
                        //set adapter to recycler view
                        binding.contactsRv.setAdapter(adapterFavorite);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}