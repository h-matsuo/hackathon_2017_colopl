package com.example.stamprally;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Debug;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;

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
    private String selectedPre;
    private boolean ok = false;

    private ArrayList<Spot> mSpots = new ArrayList<Spot>();

    private static final String ARG_SECTION_NUMBER = "section_number";

    ProgressDialog loadingDialog;

    private int remain;

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

        // initImage();//画像を表示する前に、読み込みを行う
        //spots = new ArrayList<Spot>();
        spinner_prefecture = (Spinner) rootView.findViewById(R.id.spinner);
        spinner_city = (Spinner) rootView.findViewById(R.id.spinner2);

        initSpots();

        ArrayList<String> spinnerPrefecture = getExistPrefecture(spinner_prefecture, null);
        //Toast.makeText(getActivity(), spinnerPrefecture.get(0), Toast.LENGTH_SHORT).show();
        // String[] spinnerCity = getExistCity();

        spinner_prefecture.setOnItemSelectedListener(this);
        spinner_city.setOnItemSelectedListener(this);
        //textView.setText("こんにちは"+number);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView1);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<Spot> spos = new ArrayList<Spot>();
        Spot spot = new Spot();
        spot.city = "aa";
        spos.add(spot);
        spos.add(spot);
        mAdapter = new MyAdapter(spos, this);
        mRecyclerView.setAdapter(mAdapter);

        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

    public void changeActivity(int position) {
        Intent intent = new Intent();
        intent.setClassName("com.example.stamprally", "com.example.stamprally.SpotDetailActivity");
        intent.putExtra("setuden.spotName", spots.get(position).spotName);
        intent.putExtra("setuden.userName", "村人A");
        //intent.putExtra("setuden.images",spots.get(position).images);
        intent.putExtra("setuden.description", spots.get(position).description);

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


    public void initSpots() {//ここで、本来はスポットの情報をサーバから得る
        Random random = new Random();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://1ffd1c85.ngrok.io/api/spot/list", null, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                loadingDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                //CountDownLatch latch = null;
                try {
                    JSONArray spots = response.getJSONArray("spots");
                    //latch = new CountDownLatch(spots.length());
                    for (int i = 0; i < spots.length(); i++) {
                        remain = spots.length();
                        JSONObject jsonSpot = spots.getJSONObject(i);
                        Spot spot = new Spot();
                        spot.spotName = jsonSpot.getString("spot_name");
                        spot.latitude = jsonSpot.getDouble("latitude");
                        spot.longitude = jsonSpot.getDouble("longitude");
                        spot.prefecture = jsonSpot.getString("prefecture");
                        spot.city = jsonSpot.getString("city");
                        spot.description = jsonSpot.getString("description");
                        spot.hint = jsonSpot.getString("hint");

                        JSONArray imageUrls = jsonSpot.getJSONArray("image_urls");
                        String url = imageUrls.getString(0);
                        //ImageGetTask task = new ImageGetTask(spot,latch);
                        ImageGetTask task = new ImageGetTask(spot);
                        task.execute(url);
                        //mSpots.add(spot);
                    }
                } catch (JSONException e) {
                    Log.e("!!!", e.getMessage());
                }

//                try {
//                    //task1とtask2の両方の処理が完了するまで待機
//                    latch.await();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

//                try {
//                    Thread.sleep(3000);
//                } catch (Exception e) {
//
//                }

                Log.d("?????", "" + mSpots.size());

                //Toast.makeText(getActivity(), mSpots.get(0).city, Toast.LENGTH_SHORT).show()
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
    }

    public ArrayList<String> getExistPrefecture(final Spinner spinner, final String prefecture) {
        final ArrayList<String> retStrs = new ArrayList<String>();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://1ffd1c85.ngrok.io/api/spot/list/";
        url += (prefecture != null) ? "city?prefecture=" + prefecture : "prefecture";
        client.get(url, null, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                loadingDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                try {
                    String attr = (prefecture != null) ? "cities" : "prefectures";
                    JSONArray prefectures = response.getJSONArray(attr);
                    selectedPre = prefectures.getString(0);
                    if (prefecture == null) {
                        ArrayList<String> spinnerCity = getExistPrefecture(spinner_city, selectedPre);
                    }
                    for (int i = 0; i < prefectures.length(); i++) {
                        retStrs.add(prefectures.getString(i));
                    }
                } catch (JSONException e) {
                    Log.e("!!!", e.getMessage());
                }
                Toast.makeText(getActivity(), retStrs.get(0), Toast.LENGTH_SHORT).show();
                // あってる？
                ArrayAdapter<String> Adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                        retStrs);
                Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(Adapter);

                //loadingDialog.hide();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(getActivity(), prefecture + ": FAIL!", Toast.LENGTH_SHORT).show();
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

    public void setSpots() {
        mAdapter = new MyAdapter(mSpots, this);
        mRecyclerView.setAdapter(mAdapter);
        Log.d("???", "setSpots!");
        ok = true;
        loadingDialog.hide();

    }

    class ImageGetTask extends AsyncTask<String, Void, Bitmap> {
        private CountDownLatch latch;
        private Spot spot;

        public ImageGetTask(Spot _spot) {
            spot = _spot;
            //latch = _latch;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap image;
            try {
                URL imageUrl = new URL(params[0]);
                InputStream imageIs;
                imageIs = imageUrl.openStream();
                image = BitmapFactory.decodeStream(imageIs);
                return image;
            } catch (MalformedURLException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // 取得した画像をImageViewに設定します。
            spot.imageBmp = result;
            Log.d("????", "" + spot.city);
            remain--;
            mSpots.add(spot);
            Log.d("???", "size: " + mSpots.size());
            Log.d("???", "remain: " + remain);
            if (remain == 0) {
                setSpots();
            }
            //latch.countDown();
        }
    }
}
