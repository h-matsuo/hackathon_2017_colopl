package com.example.stamprally;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.stamprally.R.id.image;
import static java.security.AccessController.getContext;

/**
 * Created by mb-347 on 2017/09/10.
 */

public class MyAdapter2     extends RecyclerView.Adapter<MyAdapter2.ItemViewHolder>{
    ArrayList<StampCard> stampCards;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        public ImageView mImageView[]=new ImageView[8];

        public ItemViewHolder(View v){
            super(v);
            mTextView = (TextView)v.findViewById(R.id.list_item_text2);
            mImageView[0]=(ImageView)v.findViewById(R.id.image1);
            mImageView[1]=(ImageView)v.findViewById(R.id.image2);
            mImageView[2]=(ImageView)v.findViewById(R.id.image3);
            mImageView[3]=(ImageView)v.findViewById(R.id.image4);
            mImageView[4]=(ImageView)v.findViewById(R.id.image5);
            mImageView[5]=(ImageView)v.findViewById(R.id.image6);
            mImageView[6]=(ImageView)v.findViewById(R.id.image7);
            mImageView[7]=(ImageView)v.findViewById(R.id.image8);
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

        for(int i=0;i<stampCards.get(position).num;i++){//スタンプの数だけ画像を設定
            Bitmap bitmap =((BitmapDrawable)holder.mImageView[i].getDrawable()).getBitmap();

        holder.mImageView[i].setImageResource(stampCards.get(position).cardId[i]);
        }

    }

    @Override
    public int getItemCount() {
        return stampCards.size();
    }
}
