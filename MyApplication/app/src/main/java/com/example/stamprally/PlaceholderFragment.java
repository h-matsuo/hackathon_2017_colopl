package com.example.stamprally;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import static com.example.stamprally.R.id.spinner;

/**
 * Created by mb-347 on 2017/09/10.
 */

public  class PlaceholderFragment extends  Fragment implements AdapterView.OnItemSelectedListener {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    Spinner spinner_prefecture, spinner_city;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> mDataset;
    private ArrayList<Spot> spots;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public PlaceholderFragment() {


    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    //return view
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.textView1);
        int number = getArguments().getInt(ARG_SECTION_NUMBER);

        spots=new ArrayList<Spot>();
        spots = initSpots();


        spinner_prefecture = (Spinner) rootView.findViewById(R.id.spinner);
        spinner_city = (Spinner) rootView.findViewById(R.id.spinner2);

        spinner_prefecture.setOnItemSelectedListener(this);
        spinner_city.setOnItemSelectedListener(this);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView1);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);




        mAdapter = new MyAdapter(spots);
        mRecyclerView.setAdapter(mAdapter);

        //textView.setText("こんにちは"+number);

        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        TextView tv;
        String item = (String) adapterView.getSelectedItem();
        if (adapterView.getId() == R.id.spinner) {//県
            tv = (TextView) rootView.findViewById(R.id.text_prefecture);
            tv.setText(item);
        } else if (adapterView.getId() == R.id.spinner2) {//市町村
            tv = (TextView) rootView.findViewById(R.id.text_city);
            tv.setText(item);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void initImage() {

    }

    public ArrayList<Spot> initSpots() {//ここで、本来はスポットの情報をサーバから得る
        Random random = new Random();
        ArrayList<Spot> mspots = new ArrayList<Spot>();
        
        
        for (int i = 0; i < 25; i++) {
            Spot spot = null;

            switch (random.nextInt(2)) {
                case 0:
                    int images[] ={R.drawable.download_1};//画像の配列を受け取る
                    spot = new Spot("美しい滝", images, "北海道の山奥にあります");
                    break;
                case 1:
                    int images2[]= {R.drawable.download_2};
                    spot = new Spot("綺麗な城", images2, "湖に浮いています");
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            
            mspots.add(spot);
        }
        return mspots;
    }
}