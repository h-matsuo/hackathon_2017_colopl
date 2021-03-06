package com.example.stamprally;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by mb-347 on 2017/09/10.
 */

public  class Placeholder2Fragment extends  Fragment implements AdapterView.OnItemSelectedListener{

    ArrayList<StampCard>stampCards;
    private RecyclerView mRecyclerView2;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Spinner spinner_prefecture2, spinner_city2,spinner_difficult;
    TextView textPrefecture,textCity,textDifficult;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public Placeholder2Fragment() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Placeholder2Fragment newInstance(int sectionNumber) {
        Placeholder2Fragment fragment = new Placeholder2Fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    //return view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment2_main, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.textView1);
        int number=getArguments().getInt(ARG_SECTION_NUMBER);


        stampCards=new ArrayList<StampCard>();
        stampCards=initStampCard();



        spinner_prefecture2 = (Spinner) rootView.findViewById(R.id.spinner3);
        spinner_city2 = (Spinner) rootView.findViewById(R.id.spinner4);
        spinner_difficult=(Spinner)rootView.findViewById(R.id.spinner5);

        spinner_prefecture2.setOnItemSelectedListener(this);
        spinner_city2.setOnItemSelectedListener(this);
        spinner_difficult.setOnItemSelectedListener(this);

        mRecyclerView2 = (RecyclerView) rootView.findViewById(R.id.recyclerView2);
        mRecyclerView2.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView2.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter2(stampCards,this);
        mRecyclerView2.setAdapter(mAdapter);

        textPrefecture=rootView.findViewById(R.id.textView2);
        textCity=rootView.findViewById(R.id.textView4);
        textDifficult=rootView.findViewById(R.id.textView5);

        return rootView;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = (String) adapterView.getSelectedItem();

        if(adapterView.getId()==R.id.spinner3){
            textPrefecture.setText(item);
        }
        else if(adapterView.getId()==R.id.spinner4){
            textCity.setText(item);
        }
        else if(adapterView.getId()==R.id.spinner5){
            textDifficult.setText(item);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {


    }
    public ArrayList<StampCard> initStampCard(){

        String names[]={"富士山ラリー","箱根ラリー","秘境巡り"};

        String persons[]={"村人A","箱根彦太郎","山田花子"};

        String descriptions[]={"富士山の穴場をまとめました!","箱根の秘境を巡ってね","遭難には十分気をつけてください"};


        ArrayList<StampCard>stampCards=new ArrayList<StampCard>();
        Random random=new Random();
        int t[]=new int[10];
        for(int i=0;i<10;i++){
            if(random.nextInt(2)==0)t[i]=R.drawable.download_3;
            else t[i]=R.drawable.download_4;
        }
        for(int i=0;i<3;i++){

            int num=random.nextInt(5)+1;
            StampCard stampCard=new StampCard(names[i],num,t,persons[i],descriptions[i]);
            stampCards.add(stampCard);
        }
        return stampCards;
    }

    public void changeActivity(int position){
        Intent intent=new Intent();
        intent.setClassName("com.example.stamprally","com.example.stamprally.CardDetailActivity");
        //ssintent.putExtra("setuden2.cardName", stampCards.get(position).cardName);
        intent.putExtra("setuden2.userName","村人B");
        //intent.putExtra("setuden2.description",stampCards.get(position).);
        intent.putExtra("setuden2.num",stampCards.get(position).num);
        intent.putExtra("setuden2.ids",stampCards.get(position).cardId);

        startActivity(intent);
    }

}