package com.example.phonebook;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.phonebook.databinding.ActivityContactListBinding;
import com.example.phonebook.databinding.ActivityDashboearAdminBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {

    // view binding
    private ActivityContactListBinding binding;

    private ArrayList<modelContact> contactArrayList ;

    private AdapterContact adapterContact ;
    private String categoryId, categoryTitle ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get data from intent
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryID");
        categoryTitle= intent.getStringExtra("categoryTitle");



        //set contact category
        binding.subtitleTv.setText(categoryTitle);
        loadContactList();


        //search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try{
                    adapterContact.getFilter().filter(s);
                }catch (Exception e){
                    Log.d(TAG,""+e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //handle go back btn
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadContactList() {
        //init list before adding data
        contactArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("contacts");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        contactArrayList.clear();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            //get data
                            modelContact model = ds.getValue(modelContact.class);
                            //add to list
                            contactArrayList.add(model);
                        }
                        //set up adapter
                        adapterContact = new AdapterContact(ContactListActivity.this,contactArrayList);
                        binding.contactsRv.setAdapter(adapterContact);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}