package com.example.phonebook;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterContact extends Filter {

    //arraylist in which we want to search
    ArrayList<modelContact> filterList ;

    //adapter in which filter need to be implemented
    AdapterContact adapterContact;

    //constructor


    public FilterContact(ArrayList<modelContact> filterList, AdapterContact adapterContact) {
        this.filterList = filterList;
        this.adapterContact = adapterContact;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        //value should not be null and empty
        if(constraint != null && constraint.length() > 0 ){
            //change to upper case or lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<modelContact> filterModels = new ArrayList<>();

            for(int i=0 ; i < filterList.size();i++){
                //validate
                if (filterList.get(i).getFname().toUpperCase().contains(constraint)){
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
        adapterContact.contactArrayList = (ArrayList<modelContact>)results.values;

        adapterContact.notifyDataSetChanged();
    }
}
