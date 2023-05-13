package com.example.phonebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phonebook.databinding.ActivityContactDetailsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class ContactDetailsActivity extends AppCompatActivity {

    //setup view binding
    private ActivityContactDetailsBinding binding ;
    //contact id get form intent
    String contactId ;
    boolean isMyFavorite = false ;
    private FirebaseAuth firebaseAuth ;

    //array list to handle favorite a=contact
    private ArrayList<modelContact> contactArrayList;

    private AdapterFavorite adapterFavorite ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        contactId = intent.getStringExtra("contactId");

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            checkIsFavorite();
        }
        loadContactDetails();

        //handle click favorite Btn
        binding.favorisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(ContactDetailsActivity.this, "You are logged in ", Toast.LENGTH_SHORT).show();
                } else {
                    if (isMyFavorite) {
                        removeFromFavorite();
                    } else {
                        addToFavorite(ContactDetailsActivity.this, contactId);
                    }
                }
            }
        });
        


        //handle backBtn
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




        //the call button
        binding.callBtn.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {

                                                   DatabaseReference refcontacts = FirebaseDatabase.getInstance().getReference("contacts");
                                                   refcontacts.child(contactId)
                                                           .addListenerForSingleValueEvent(new ValueEventListener() {
                                                               @Override
                                                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                   //get contact info

                                                                   String phone = "" + snapshot.child("phone").getValue();

                                                                   Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                                                                   dialIntent.setData(Uri.parse("tel:" + phone));
                                                                   startActivity(dialIntent);
                                                               }

                                                               @Override
                                                               public void onCancelled(@NonNull DatabaseError error) {

                                                               }
                                                           });



                                               }
                                           }
        );


        //the msg button
        binding.smsBtn.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {

                                                   DatabaseReference refcontacts = FirebaseDatabase.getInstance().getReference("contacts");
                                                   refcontacts.child(contactId)
                                                           .addListenerForSingleValueEvent(new ValueEventListener() {
                                                               @Override
                                                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                   //get contact info

                                                                   String phone = "" + snapshot.child("phone").getValue();


                                                                   Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                                                                   smsIntent.setData(Uri.parse("smsto:" + Uri.encode(phone)));
                                                                   smsIntent.putExtra("sms_body", "Hello, this is a test message!");
                                                                   startActivity(smsIntent);


                                                               }

                                                               @Override
                                                               public void onCancelled(@NonNull DatabaseError error) {

                                                               }
                                                           });



                                               }
                                           }
        );


        //the email button
        binding.emailBtn.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {

                                                  DatabaseReference refcontacts = FirebaseDatabase.getInstance().getReference("contacts");
                                                  refcontacts.child(contactId)
                                                          .addListenerForSingleValueEvent(new ValueEventListener() {
                                                              @Override
                                                              public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                  //get contact info

                                                                  String phone = "" + snapshot.child("phone").getValue();

                                                                  String message = "Hello, this is a test message!"; // replace with the message you want to send (optional)
                                                                  String recipient = "" + snapshot.child("phone").getValue()+"@exemple.com";; // replace with the recipient's email address
                                                                  String subject = "Email subject"; // replace with the email subject
                                                                  String body = "Email body"; // replace with the email body
                                                                  Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", recipient, null));
                                                                  intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                                                  intent.putExtra(Intent.EXTRA_TEXT, body);
                                                                  startActivity(Intent.createChooser(intent, "Send email"));

                                                              }

                                                              @Override
                                                              public void onCancelled(@NonNull DatabaseError error) {

                                                              }
                                                          });

                                              }
                                          }
        );
    }



    private void addToFavorite(ContactDetailsActivity contactDetailsActivity, String contactId) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            Toast.makeText(ContactDetailsActivity.this,"You are not logged in ",Toast.LENGTH_SHORT).show();
        }else{
            long timestamp = System.currentTimeMillis();
            //set up data to add in firebase db of current user for favorite contact

            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("contactId",""+contactId);
            hashMap.put("timestamp",""+timestamp);

            //save to data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(contactId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ContactDetailsActivity.this,"Added to your Favorite List ",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ContactDetailsActivity.this,"Failed to add to your favorite list "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }



    private void removeFromFavorite() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            Toast.makeText(ContactDetailsActivity.this,"You are not logged in ",Toast.LENGTH_SHORT).show();
        }else{


            //save to data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(contactId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ContactDetailsActivity.this,"Removed from your Favorite List ",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ContactDetailsActivity.this,"Failed to Remove from your favorite list "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void loadContactDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("contacts");
        ref.child(contactId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String categoryId = ""+snapshot.child("categoryId").getValue();
                        String Fname= ""+snapshot.child("Fname").getValue();
                        String Lname= ""+snapshot.child("Lname").getValue();
                        String phone= ""+snapshot.child("phone").getValue();

                        loadCategory(""+categoryId,binding.categoryTv);

                        //set data
                        binding.FnameEt.setText(Fname);
                        binding.LnameEt.setText(Lname);
                        binding.phoneEt.setText(phone);

                        DatabaseReference refContactCategory = FirebaseDatabase.getInstance().getReference("categories");
                        refContactCategory.child(categoryId)
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


    void checkIsFavorite(){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(contactId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            isMyFavorite = snapshot.exists();
                            if (isMyFavorite) {
                                binding.favorisBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.baseline_favorite_24,0,0);
                                binding.favorisBtn.setText("Remove Favorite");
                            } else {
                                binding.favorisBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.baseline_favorite_border_24,0,0);
                                binding.favorisBtn.setText("Add Favorite");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }

    private void loadCategory(String categoryId, TextView categoryTv) {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category
                        String category = ""+snapshot.child("category").getValue();

                        //set to category text view
                        categoryTv.setText(category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}