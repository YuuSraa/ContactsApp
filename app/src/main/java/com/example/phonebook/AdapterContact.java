package com.example.phonebook;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phonebook.databinding.RowContactBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterContact extends RecyclerView.Adapter<AdapterContact.HolderContactList> implements Filterable {

    private Context context ;
    public ArrayList<modelContact> contactArrayList,filterList;

    // view binding
    private RowContactBinding binding;

    private FilterContact filter;

    //progress
    private ProgressDialog progressDialog;

    public AdapterContact(Context context, ArrayList<modelContact> contactArrayList) {
        this.context = context;
        this.contactArrayList = contactArrayList;
        this.filterList = contactArrayList;

        //init progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderContactList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind row_category.xml
        binding = RowContactBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderContactList(binding.getRoot());
    }


    @Override
    public void onBindViewHolder(@NonNull HolderContactList holder, int position) {

        modelContact model =contactArrayList.get(position);
        String Fname = model.getFname();
        String Lname = model.getLname();
        String phone = model.getPhone();
        String id = model.getId();
        String categoryId = model.getCategoryId();
        String uid = model.getUid();
        long timestamp = model.getTimestamp();

        //set data
        holder.FnameTv.setText(Fname);
        holder.LnameTv.setText(Lname);
        holder.phoneTv.setText(phone);
        holder.categoryTv.setText(categoryId);

        loadCategory(model,holder);

        //handle click ,show dialog with options edit and delete
        holder.moreBtn.setOnClickListener(v -> moreOptionsDialog(model,holder));

        //handle contact click, open contact details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContactDetailsActivity.class);
                intent.putExtra("contactId",id);
                context.startActivity(intent);
            }
        });
    }

    private void moreOptionsDialog(modelContact model,HolderContactList holder) {

        String contactId = model.getId();
        String Fname = model.getFname();
        String Lname = model.getLname();
        String phone = model.getPhone();
        String categoryId = model.getCategoryId();


        //options to show in dialog
        String[] options = {"Edit", "Delete"};


        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //handle dialog option click
                        if(which==0){
                            //edit option
                            Intent intent = new Intent(context,ContactEditActivity.class);
                            intent.putExtra("contactId",contactId);
                            context.startActivity(intent);

                        } else if (which==1) {
                            //delete option
                            deleteContact(model,holder);

                        }
                    }
                })
                .show();
    }


    private void deleteContact(modelContact model, HolderContactList holder) {

        String contactId = model.getId();
        String Fname = model.getFname();
        String Lname = model.getLname();
        String phone = model.getPhone();
        String categoryId = model.getCategoryId();

        Log.d(TAG,"Deleting..");
        progressDialog.setMessage("Deleting"+ Fname +" "+Lname);
        progressDialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("contacts");
        reference.child(contactId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(context,"Contact deleted successufully..",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void loadCategory(modelContact model, HolderContactList holder) {

        //get category using cetegory iD
        String categoryId = model.getCategoryId();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category
                        String category = ""+snapshot.child("category").getValue();

                        //set to category text view
                        holder.categoryTv.setText(category);
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

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterContact(filterList,this);
        }
        return filter;
    }

    class HolderContactList extends RecyclerView.ViewHolder{

        ProgressBar progressBar;
        TextView  FnameTv ,LnameTv, phoneTv, categoryTv;
        ImageButton moreBtn;
        public HolderContactList(@NonNull View itemView) {
            super(itemView);

            progressBar = binding.progressBar;
            FnameTv = binding.FnameTv;
            LnameTv = binding.LnameTv;
            phoneTv = binding.phoneTv;
            categoryTv = binding.categoryTv;
            moreBtn = binding.moreBtn;
        }
    }
}
