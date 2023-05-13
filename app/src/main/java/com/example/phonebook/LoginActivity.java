package com.example.phonebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.phonebook.databinding.ActivityLoginBinding;
import com.example.phonebook.databinding.ActivityMainBinding;
import com.example.phonebook.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    // view binding
    private ActivityLoginBinding binding ;

    //firebase auth
    private FirebaseAuth firebaseAuth ;

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);



        //handle click, go to register screen
        binding.noAccountTv.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,RegisterActivity.class)));

        //handle click, begin login
        binding.loginBtn.setOnClickListener(v -> validateData());
    }

    private String email="", password="";
    private void validateData() {
        /* before logging, let's do some data validation */

        //get data
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();



        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, " Invalid email pattern ... ! :", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password ...", Toast.LENGTH_SHORT).show();
        }else {
            //data is validated, begin login
            loginUser();
        }

    }

    private void loginUser() {
        //show progress
        progressDialog.setMessage("logging in ...");
        progressDialog.show();

        //login user
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(authResult -> {
                    //login success, check if user is user or admin
                    checkUser();
                })
                .addOnFailureListener(e -> {
                    //login failed
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"" +e.getMessage(), Toast.LENGTH_SHORT).show();


                });
    }

    private void checkUser() {
        progressDialog.setMessage("checking user...");
        //check if user is user or admin from database
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        //check in db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        // get user type
                        String userType = "" + snapshot.child("userType").getValue();

                        //check user type
                        if (userType.equals("user")) {
                            //this is a simple user, open user dashboard
                            startActivity(new Intent(LoginActivity.this, DashboearUserActivity.class));
                            finish();
                        } else if (userType.equals("admin")) {
                            //this is an admin, open admin dashboad
                            startActivity(new Intent(LoginActivity.this, DashboearAdminActivity.class));
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });

}
}