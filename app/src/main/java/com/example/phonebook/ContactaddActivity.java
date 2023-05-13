package com.example.phonebook;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.phonebook.databinding.ActivityContactaddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactaddActivity extends AppCompatActivity {
    //setup view binding
    private ActivityContactaddBinding binding ;

    private FirebaseAuth firebaseAuth ;
    //arraylist to hold off categories
    private ArrayList<String> categoryTitleArrayList;
    private ArrayList<String> categoryIdArrayList;

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactaddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadCategories();

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //handle click, go to previous activity
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




        //handle click,pick category
        binding.categoryTv.setOnClickListener(v -> categoryPickDialog());


        //handle backBtn
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //handle click,submit button
        binding.submitBtn.setOnClickListener(v -> {
            //validate data
            validateData();

        });
    }

    private String Fname="",Lname="",phone="";

    private void validateData() {

        //step 1 validate data
        Fname = binding.FnameEt.getText().toString().trim();
        Lname = binding.LnameEt.getText().toString().trim();
        phone = binding.phoneEt.getText().toString().trim();

        if(TextUtils.isEmpty(Fname)){
            Toast.makeText(this,"Enter First name...",Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this,"Enter Phone number...",Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectCategoryTitle)) {
            Toast.makeText(this,"Pick category...",Toast.LENGTH_SHORT).show();

        }else{
            //all data is valid
            uploadcontact();
        }

    }

    private void uploadcontact() {
        //step 2 upload contact to storage
        progressDialog.setMessage("uploading contact...");
        progressDialog.show();

        //get timestamp
        long timestamp = System.currentTimeMillis();

        //setup info to add in firebase
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id", ""+timestamp);
        hashMap.put("Fname", Fname);
        hashMap.put("Lname", Lname);
        hashMap.put("phone", phone);
        hashMap.put("categoryId", selectCategoryId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("uid", ""+firebaseAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("contacts");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //data added to db
                        progressDialog.dismiss();
                        Toast.makeText(ContactaddActivity.this,"Contact added successfully...",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //data failed adding to db
                        progressDialog.dismiss();
                        Toast.makeText(ContactaddActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void loadCategories() {
        categoryTitleArrayList = new ArrayList<String>();
        categoryIdArrayList = new ArrayList<String>();

        //db ref to load categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear array list before adding data into it
                categoryTitleArrayList.clear();
                categoryIdArrayList.clear();

                //get id and title of category
                for (DataSnapshot ds : snapshot.getChildren()){
                    String categoryId = ""+ ds.child("id").getValue();
                    String categoryTitle = ""+ ds.child("category").getValue();


                    //add to respective arraylists
                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //select category id and title
    private String selectCategoryId, selectCategoryTitle;
    private void categoryPickDialog() {
        Log.d(TAG,"Categorypickdialog : showing ");


        //first we need to get categories from firebase

        //get array of categories from arraylist
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for (int i = 0; i< categoryTitleArrayList.size(); i++){
            categoriesArray[i] = String.valueOf(categoryTitleArrayList.get(i));
        }

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("pick a Category")
                    .setItems(categoriesArray, (dialog, which) -> {
                        //handle item click
                        //get clicked item from list
                        selectCategoryTitle = String.valueOf(categoryTitleArrayList.get(which));
                        selectCategoryId = String.valueOf(categoryIdArrayList.get(which));
                        //set to category textView
                        binding.categoryTv.setText(selectCategoryTitle);
                    });
        builder.show();
        }
    }




