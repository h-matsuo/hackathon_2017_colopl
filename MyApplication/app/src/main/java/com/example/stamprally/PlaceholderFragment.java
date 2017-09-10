package com.example.stamprally;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;

import cz.msebera.android.httpclient.Header;

import static android.R.attr.button;
import static com.example.stamprally.R.id.spinner;

/**
 * Created by mb-347 on 2017/09/10.
 */

public  class PlaceholderFragment extends Fragment implements AdapterView.OnItemSelectedListener {

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

    ProgressDialog loadingDialog;

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

        /* ローディングダイアログ */
        loadingDialog = new ProgressDialog(getActivity());
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setMessage("Loading...");
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        initImage();//画像を表示する前に、読み込みを行う
        spots = new ArrayList<Spot>();
        spots = initSpots();

        spinner_prefecture = (Spinner) rootView.findViewById(R.id.spinner);
        spinner_city = (Spinner) rootView.findViewById(R.id.spinner2);

        ArrayList<String> spinnerPrefecture = getExistPrefecture();
        //Toast.makeText(getActivity(), spinnerPrefecture.get(0), Toast.LENGTH_SHORT).show();
        // String[] spinnerCity = getExistCity();

        spinner_prefecture.setOnItemSelectedListener(this);
        spinner_city.setOnItemSelectedListener(this);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView1);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(spots,this);
        mRecyclerView.setAdapter(mAdapter);





        //textView.setText("こんにちは"+number);

        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

    public void changeActivity(int position){
        Intent intent=new Intent();
        intent.setClassName("com.example.stamprally","com.example.stamprally.SpotDetailActivity");
        intent.putExtra("setuden.spotName", spots.get(position).spotName);
        intent.putExtra("setuden.userName","村人A");
        intent.putExtra("setuden.images",spots.get(position).images);
        intent.putExtra("setuden.description",spots.get(position).description);

        startActivity(intent);
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



    public ArrayList<Spot> initSpots() {//ここで、本来はスポットの情報をサーバから得る
        Random random = new Random();
        final ArrayList<Spot> mspots = new ArrayList<Spot>();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://1ffd1c85.ngrok.io/api/spot/list/prefecture", null, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                loadingDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                String str = "";
                try {
                    JSONArray spot_ids = response.getJSONArray("spot_ids");
                    for (int i = 0; i < spot_ids.length(); i++) {
                        String url = "http://1ffd1c85.ngrok.io/api/spot/?" + spot_ids.getString(i);
                        AsyncHttpClient clientI = new AsyncHttpClient();
                        clientI.get(url, null, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                // called when response HTTP status is "200 OK"
                                try {
                                    Spot spot = new Spot();
                                    spot.spotName = response.getString("spot_name");
                                    spot.latitude = response.getDouble("latitude");
                                    spot.longitude = response.getDouble("longitude");
                                    spot.prefecture = response.getString("prefecture");
                                    spot.city = response.getString("city");
                                    spot.description = response.getString("desc");
                                    spot.hint = response.getString("hint");
                                    mspots.add(spot);
                                } catch (JSONException e) {

                                }
                            }
                        });

                    }
                } catch (JSONException e) {

                }
                Toast.makeText(getActivity(), "OK!", Toast.LENGTH_SHORT).show();
                loadingDialog.hide();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(getActivity(), "FAIL!", Toast.LENGTH_SHORT).show();
                loadingDialog.hide();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(getActivity(), "RETRY!", Toast.LENGTH_SHORT).show();
            }
        });

        return mspots;
    }

    public ArrayList<String> getExistPrefecture(){
        final ArrayList<String> retStrs = new ArrayList<String>();
        final int[] size = new int[1]; //FIXME onSuccessから値を渡すための良くない方法
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://1ffd1c85.ngrok.io/api/spot/list/prefecture", null, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                loadingDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                try {
                    JSONArray prefectures = response.getJSONArray("prefectures");
                    for (int i = 0; i < prefectures.length(); i++) {
                        retStrs.add(prefectures.getString(i));
                    }
                } catch (JSONException e) {

                }
                Toast.makeText(getActivity(), retStrs.get(0), Toast.LENGTH_SHORT).show();
                // あってる？
                ArrayAdapter<String> preAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,
                        retStrs);
                preAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_prefecture.setAdapter(preAdapter);

                loadingDialog.hide();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(getActivity(), "FAIL!", Toast.LENGTH_SHORT).show();
                loadingDialog.hide();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(getActivity(), "RETRY!", Toast.LENGTH_SHORT).show();
            }
        });

        return retStrs;
    }
}
