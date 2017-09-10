package com.example.stamprally;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mb-347 on 2017/09/10.
 */

public class MyAdapter2     extends RecyclerView.Adapter<MyAdapter2.ItemViewHolder>{
    ArrayList<StampCard> stampCards;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        public ImageView mImageView;

        public ItemViewHolder(View v){
            super(v);
            mTextView = (TextView)v.findViewById(R.id.list_item_text2);
            mImageView=(ImageView)v.findViewById(R.id.imageView2);
        }
    }

    public MyAdapter2(ArrayList<StampCard>stampCards){
        this.stampCards=stampCards;

    }


    @Override
    public ItemViewHolder onCreateViewHolder(
            ViewGroup parent,
            int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list2_item,
                        parent,
                        false);
        return new MyAdapter2.ItemViewHolder(v);
    }



    @Override
    public void onBindViewHolder(MyAdapter2.ItemViewHolder holder, int position) {
        holder.mTextView.setText(stampCards.get(position).cardName);
        holder.mImageView.setImageResource(stampCards.get(position).cardId[0]);

    }

    @Override
    public int getItemCount() {
        return stampCards.size();
    }
}
