package com.example.phonebook;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phonebook.databinding.RowFavoritecontactBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterFavorite extends RecyclerView.Adapter<AdapterFavorite.HoldercontactFavorite>{
    private Context context ;
    public ArrayList<modelContact> contactArrayList;

    private RowFavoritecontactBinding binding;


    //constructor
    public AdapterFavorite(Context context, ArrayList<modelContact> contactArrayList) {
        this.context = context;
        this.contactArrayList = contactArrayList;
    }

    @NonNull
    @Override
    public HoldercontactFavorite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowFavoritecontactBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HoldercontactFavorite(binding.getRoot());

    }

    @Override
    public void onBindViewHolder(@NonNull HoldercontactFavorite holder, int position) {


        modelContact model = contactArrayList.get(position);
        loadContactDetails(model,holder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ContactDetailsActivity.class);
                intent.putExtra("contactId",model.getId());
                context.startActivity(intent);

            }
        });

        //handle  click remove from favorite
        holder.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromFavorite(context,model.getId());

            }
        });
    }

    private void removeFromFavorite(Context context, String contactId) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            Toast.makeText(this.context,"You are not logged in ",Toast.LENGTH_SHORT).show();
        }else{

            //save to data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(contactId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AdapterFavorite.this.context,"Removed from your Favorite List ",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdapterFavorite.this.context,"Failed to Remove from your favorite list "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void loadContactDetails(modelContact model, HoldercontactFavorite holder) {
        String contactId = model.getId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("contacts");
        ref.child(contactId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String categoryId = ""+snapshot.child("categoryId").getValue();
                        String Fname= ""+snapshot.child("Fname").getValue();
                        String Lname= ""+snapshot.child("Lname").getValue();
                        String phone= ""+snapshot.child("phone").getValue();


                        //set data

                        model.setFavorite(true);
                        model.setFname(Fname);
                        model.setLname(Lname);
                        model.setPhone(phone);
                        model.setCategoryId(categoryId);



                        //set adata to viewz
                        holder.FnameTv.setText(Fname);
                        holder.LnameTv.setText(Lname);
                        holder.phoneTv.setText(phone);


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


    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }

    //view holder class
    class HoldercontactFavorite extends RecyclerView.ViewHolder{

        ProgressBar progressBar;
        TextView FnameTv ,LnameTv, phoneTv, categoryTv;
        ImageButton favoriteBtn;
        public HoldercontactFavorite(View itemView){
            super(itemView);

            //initn ui
            progressBar = binding.progressBar;
            FnameTv = binding.FnameTv;
            LnameTv = binding.LnameTv;
            phoneTv = binding.phoneTv;
            categoryTv = binding.categoryTv;
            favoriteBtn = binding.favriteBtn;

        }
    }
}
