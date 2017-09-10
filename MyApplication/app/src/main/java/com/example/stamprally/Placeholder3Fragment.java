package com.example.stamprally;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by mb-347 on 2017/09/10.
 */

public  class Placeholder3Fragment extends  Fragment{

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public Placeholder3Fragment() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Placeholder3Fragment newInstance(int sectionNumber) {
        Placeholder3Fragment fragment = new Placeholder3Fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    //return view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment3_main, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.textView1);
        int number=getArguments().getInt(ARG_SECTION_NUMBER);

        //textView.setText("こんにちは"+number);

        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }
}