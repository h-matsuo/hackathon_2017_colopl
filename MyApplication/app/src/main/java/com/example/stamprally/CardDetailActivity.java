package com.example.stamprally;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mb-347 on 2017/09/11.
 */

public class CardDetailActivity extends Activity {
    String cardName,userName,description;
    TextView textUserName,textDescription,textCardName;
    ImageView mImageView[]=new ImageView[8];
    int ids[];
    int num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        textCardName=findViewById(R.id.text_cardname);
        textUserName=findViewById(R.id.text_username2);
        textDescription=findViewById(R.id.text_description2);
        mImageView[0]=findViewById(R.id.imageView2_1);
        mImageView[1]=findViewById(R.id.imageView2_2);
        mImageView[2]=findViewById(R.id.imageView2_3);
        mImageView[3]=findViewById(R.id.imageView2_4);
        mImageView[4]=findViewById(R.id.imageView2_5);
        mImageView[5]=findViewById(R.id.imageView2_6);
        mImageView[6]=findViewById(R.id.imageView2_7);
        mImageView[7]=findViewById(R.id.imageView2_8);



        initInformation();

        textCardName.setText(cardName);
        textUserName.setText(userName);
        textDescription.setText(description);

        for(int i=0;i<num;i++){
            mImageView[i].setImageResource(ids[i]);
        }
    }

    public void initInformation(){
        Intent intent=getIntent();
        if(intent!=null) {
            cardName = intent.getStringExtra("setuden2.cardName");
            userName = intent.getStringExtra("setuden2.userName");
            description = intent.getStringExtra("setuden2.description");
            ids=intent.getIntArrayExtra("setuden2.ids");
            num=intent.getIntExtra("setuden2.num",0);
        }
    }
}
