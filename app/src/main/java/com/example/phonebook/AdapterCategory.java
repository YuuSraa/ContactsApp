package com.example.phonebook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phonebook.databinding.ActivityCategoryAddBinding;
import com.example.phonebook.databinding.RowCategoryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory> implements Filterable {

    private Context context ;
    public ArrayList<modelCategory> categoryArraylist,filterList;

    // view binding
    private RowCategoryBinding binding;

    //instance of our filter clASS
    private FilterCategory filter;

    public AdapterCategory(Context context, ArrayList<modelCategory> categoryArraylist) {
        this.context = context;
        this.categoryArraylist = categoryArraylist;
        this.filterList = categoryArraylist;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind row_category.xml
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderCategory(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
        //get data
        modelCategory model = categoryArraylist.get(position);
        String id = model.getId();
        String category = model.getCategory();
        String uid = model.getUid();
        long timestamp = model.getTimestamp();

        //set data
        holder.categoryTv.setText(category);

        //handle click, delete category
        holder.deleteBtn.setOnClickListener(v -> {
            //confirm delete dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete")
                    .setMessage("Are you sure you want to delete this category ?")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Toast.makeText(context, "Deleting ... ! :", Toast.LENGTH_SHORT).show();
                            //begin delete
                            deleteCategory(model,holder);
                        }
                    })
                    .show();
        });

        //handle item click, go to contactlistactivity, also pass categoryid
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ContactListActivity.class);
                intent.putExtra("categoryID",id);
                intent.putExtra("categoryTitle",category);
                context.startActivity(intent);
            }
        });

    }

    private void deleteCategory(modelCategory model, HolderCategory holder) {
        //get id of category to delete
        String id = model.getId();
        //firebase DB -> categories -> categoryid
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Successfully deleted ... ! :", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return categoryArraylist.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterCategory(filterList,this);

        }
        return filter;
    }

    //view holder class to hold UI view for row_category.xml
    class HolderCategory extends RecyclerView.ViewHolder{
        //ui view of row_category.xml
        TextView categoryTv;
        ImageButton deleteBtn;

        public HolderCategory(@NonNull View itemView) {
            super(itemView);

            //init ui view
            categoryTv = binding.categoryTv;
            deleteBtn = binding.deleteBtn;
        }
    }
}
