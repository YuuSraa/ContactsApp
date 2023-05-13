package com.example.phonebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    //firebase auth
    private FirebaseAuth firebaseAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // start main screen after 2 sec
        new Handler().postDelayed(this::checkUser,2000);
    }

    private void checkUser() {
        //get current user, if logged in
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            //user not logged in
            //start main screen
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }else {
            //user logged in check user type, same as in loginactivity
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // get user type
                            String userType = "" + snapshot.child("userType").getValue();

                            //check user type
                            if (userType.equals("user")) {
                                //this is a simple user, open user dashboad
                                startActivity(new Intent(SplashActivity.this, DashboearUserActivity.class));
                                finish();
                            } else if (userType.equals("admin")) {
                                //this is an admin, open admin dashboad
                                startActivity(new Intent(SplashActivity.this, DashboearAdminActivity.class));
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {


                        }
                    });
        }
    }
}

