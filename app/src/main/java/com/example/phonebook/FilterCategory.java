package com.example.phonebook;

import android.widget.Filter;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FilterCategory extends Filter {

    //arraylist in which we want to search
    ArrayList<modelCategory> filterList ;

    //adapter in which filter need to be implemented
    AdapterCategory adapterCategory;

    //constructor


    public FilterCategory(ArrayList<modelCategory> filterList, AdapterCategory adapterCategory) {
        this.filterList = filterList;
        this.adapterCategory = adapterCategory;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        //value should not be null and empty
        if(constraint != null && constraint.length() > 0 ){
            //change to upper case or lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<modelCategory> filterModels = new ArrayList<>();

            for(int i=0 ; i < filterList.size();i++){
                //validate
                if (filterList.get(i).getCategory().toUpperCase().contains(constraint)){
                    //add to filtered list
                    filterModels.add(filterList.get(i));
                }
            }

            results.count = filterModels.size();
            results.values =filterModels;

        }else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterCategory.categoryArraylist = (ArrayList<modelCategory>)results.values;

        adapterCategory.notifyDataSetChanged();
    }
}
