package com.example.ks.mobileverificationsystem;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class Adapter_spinner_country extends ArrayAdapter<Country>{
    private LayoutInflater layoutInflater;
    private ArrayList<Country> countryArrayList;

    private static class ViewHolder{
        TextView country_name,country_code;
    }

    Adapter_spinner_country(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        countryArrayList=new ArrayList<>();
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void add(@Nullable Country object) {
        super.add(object);
        countryArrayList.add(object);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }

    private View getCustomView(int position,View convertView,ViewGroup parent){
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView=layoutInflater.inflate(R.layout.row_spinner_code,parent,false);

            viewHolder.country_name= (TextView) convertView.findViewById(R.id.text_country_name);
            viewHolder.country_code=(TextView)convertView.findViewById(R.id.text_country_code);

            convertView.setTag(viewHolder);
        }else
            viewHolder=(ViewHolder)convertView.getTag();

        viewHolder.country_name.setText(countryArrayList.get(position).getName());
        viewHolder.country_code.setText(countryArrayList.get(position).getDial_code());

        return convertView;
    }
}
