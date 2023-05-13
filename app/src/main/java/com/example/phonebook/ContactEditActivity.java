package com.example.phonebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.phonebook.databinding.ActivityContactEditBinding;
import com.example.phonebook.databinding.ActivityContactaddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactEditActivity extends AppCompatActivity {
    //setup view binding
    private ActivityContactEditBinding binding;

    //contact id from intent started from adaptercontact
    private String contactId;

    //progress
    private ProgressDialog progressDialog;

    private ArrayList<String> categoryTitleArrayList, categoryIdArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        contactId = getIntent().getStringExtra("contactId");

        //init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadCategories();
        loadContactInfo();

        //handle click ,pick category
        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });


        //handle backBtn
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //handle click begin upload
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
                
            }
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
        } else if (TextUtils.isEmpty(selectedCategoryId)) {
            Toast.makeText(this,"Pick category...",Toast.LENGTH_SHORT).show();

        }else{
            //all data is valid
            updatecontact();
        }

    }

    private void updatecontact() {
        //show progess
        progressDialog.setMessage("updating Contact info...");
        progressDialog.show();

        //setup info to update in firebase
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Fname", Fname);
        hashMap.put("Lname", Lname);
        hashMap.put("phone", phone);
        hashMap.put("categoryId", selectedCategoryId);

        //start updating
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("contacts");
        ref.child(contactId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(ContactEditActivity.this,"Contact updated successfully...",Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ContactEditActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadContactInfo() {
        DatabaseReference refcontacts = FirebaseDatabase.getInstance().getReference("contacts");
        refcontacts.child(contactId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get contact info

                        selectedCategoryId = ""+snapshot.child("categoryId").getValue();
                        String Fname= ""+snapshot.child("Fname").getValue();
                        String Lname= ""+snapshot.child("Lname").getValue();
                        String phone= ""+snapshot.child("phone").getValue();

                        //set to views
                        binding.FnameEt.setText(Fname);
                        binding.LnameEt.setText(Lname);
                        binding.phoneEt.setText(phone);

                        DatabaseReference refContactCategory = FirebaseDatabase.getInstance().getReference("categories");
                        refContactCategory.child(selectedCategoryId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //get category
                                        String category = ""+snapshot.child("category").getValue();
                                        //set to category text view
                                        binding.categoryTv.setText(category);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private String selectedCategoryId = "", selectedCategoryTitle = "";

    private void categoryDialog(){
        //make string array from arraylist of stiring
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for(int i=0 ; i<categoryTitleArrayList.size();i++){
            categoriesArray[i] = categoryTitleArrayList.get(i);
        }

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("choose Category")
                .setItems(categoriesArray, (dialog, which) -> {
                    //handle item click
                    //get clicked item from list
                    selectedCategoryTitle = String.valueOf(categoryTitleArrayList.get(which));
                    selectedCategoryId = String.valueOf(categoryIdArrayList.get(which));
                    //set to category textView
                    binding.categoryTv.setText(selectedCategoryTitle);
                });
        builder.show();
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
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String id = "" + ds.child("id").getValue();
                    String category = "" + ds.child("category").getValue();


                    //add to respective arraylists
                    categoryTitleArrayList.add(category);
                    categoryIdArrayList.add(id);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
