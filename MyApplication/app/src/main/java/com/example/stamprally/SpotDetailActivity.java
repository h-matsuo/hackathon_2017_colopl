package com.example.stamprally;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mb-347 on 2017/09/10.
 */

public class SpotDetailActivity extends Activity {
    TextView textUserName,textDescription,textSpotName;

    String userName,description,spotName;
    int[]images;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotdetail);
        textUserName=findViewById(R.id.text_username);
        textDescription=findViewById(R.id.text_description);
        textSpotName=findViewById(R.id.text_spotname);
        imageView=findViewById(R.id.imageView4);

        initInformation();

        textUserName.setText(userName);
        textSpotName.setText(spotName);
        textDescription.setText(description);

    }

    public void initInformation(){
        Intent intent=getIntent();
        if(intent!=null){
            spotName=intent.getStringExtra("setuden.spotName");
            userName=intent.getStringExtra("setuden.userName");
            description=intent.getStringExtra("setuden.description");
            images=intent.getIntArrayExtra("setuden.images");
        }
    }
}
