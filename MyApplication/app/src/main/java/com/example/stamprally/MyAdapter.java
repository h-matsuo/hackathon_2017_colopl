package com.example.stamprally;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by mb-347 on 2017/09/10.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ItemViewHolder> {
    ArrayList<String> mDataset;
    ArrayList<Spot>spots;


    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        public ImageView mImageView;

        public ItemViewHolder(View v){
            super(v);
            mTextView = (TextView)v.findViewById(R.id.list_item_text);
            mImageView=(ImageView)v.findViewById(R.id.imageView);
        }
    }


    public MyAdapter(ArrayList<Spot>spots){
        this.spots=spots;

    }
    @Override
    public ItemViewHolder onCreateViewHolder(
            ViewGroup parent,
            int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item,
                        parent,
                        false);
        return new ItemViewHolder(v);
    }



    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        holder.mTextView.setText(spots.get(position).spotName);
        holder.mImageView.setImageResource(spots.get(position).images[0]);

        /*final String data;
        data = mDataset.get(position);
        holder.mTextView.setText(mDataset.get(position));
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromDataset(data);
            }
        });
        */

    }

    @Override
    public int getItemCount() {
        return spots.size();
    }

    protected void removeFromDataset(String data){
        for(int i=0; i<mDataset.size(); i++){
            if(mDataset.get(i).equals(data)){
                mDataset.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }
}

