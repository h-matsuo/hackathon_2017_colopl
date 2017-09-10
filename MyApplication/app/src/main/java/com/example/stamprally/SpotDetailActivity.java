package com.example.stamprally;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by mb-347 on 2017/09/10.
 */

public class SpotDetailActivity extends Activity {
    TextView textUserName,textDescription,textSpotName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotdetail);
        textUserName=findViewById(R.id.text_username);
        textDescription=findViewById(R.id.text_description);
        textSpotName=findViewById(R.id.text_spotname);

    }
}
