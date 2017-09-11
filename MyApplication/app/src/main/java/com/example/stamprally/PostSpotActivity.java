package com.example.stamprally;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static java.security.AccessController.getContext;

/**
 * Created by akiyama on 2017/09/11.
 */
public class PostSpotActivity extends AppCompatActivity implements LocationListener {

    Button btnUpImg;
    Button btnPostSpot;

    int REQUEST_CHOOSER = 1000;
    Uri m_uri;

    ProgressDialog loadingDialog;

    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitya_post_spot);

        /* ローディングダイアログ */
        loadingDialog = new ProgressDialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setMessage("Loading...");
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        /*-----------*/

        /* GPS */
        // Fine か Coarseのいずれかのパーミッションが得られているかチェックする
        // 本来なら、Android6.0以上かそうでないかで実装を分ける必要がある
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            /** fine location のリクエストコード（値は他のパーミッションと被らなければ、なんでも良い）*/
            final int requestCode = 1;

            // いずれも得られていない場合はパーミッションのリクエストを要求する
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode );
            return;
        }

        // 位置情報を管理している LocationManager のインスタンスを生成する
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        String locationProvider = null;

        // GPSが利用可能になっているかどうかをチェック
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        }
        // GPSプロバイダーが有効になっていない場合は基地局情報が利用可能になっているかをチェック
        else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }
        // いずれも利用可能でない場合は、GPSを設定する画面に遷移する
        else {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            return;
        }

        /** 位置情報の通知するための最小時間間隔（ミリ秒） */
        final long minTime = 500;
        /** 位置情報を通知するための最小距離間隔（メートル）*/
        final long minDistance = 1;

        // 利用可能なロケーションプロバイダによる位置情報の取得の開始
        // FIXME 本来であれば、リスナが複数回登録されないようにチェックする必要がある
        locationManager.requestLocationUpdates(locationProvider, minTime, minDistance, this);
        // 最新の位置情報
        location = locationManager.getLastKnownLocation(locationProvider);

        if (location != null) {
            //TextView textView = (TextView) findViewById(R.id.tv_location);
            //textView.setText(String.valueOf( "onCreate() : " + location.getLatitude()) + "," + String.valueOf(location.getLongitude()));
        }
        /*-----------*/

        // init view
        btnUpImg = (Button)findViewById(R.id.btn_up_img);
        btnPostSpot = (Button)findViewById(R.id.btn_post_spot);

        btnUpImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //カメラの起動Intentの用意
                String photoName = System.currentTimeMillis() + ".jpg";
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.TITLE, photoName);
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                m_uri = getContentResolver()
                        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, m_uri);

                // ギャラリー用のIntent作成
                Intent intentGallery;
                if (Build.VERSION.SDK_INT < 19) {
                    intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
                    intentGallery.setType("image/*");
                } else {
                    intentGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intentGallery.addCategory(Intent.CATEGORY_OPENABLE);
                    intentGallery.setType("image/jpeg");
                }
                Intent intent = Intent.createChooser(intentCamera, "画像の選択");
                intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {intentGallery});
                startActivityForResult(intent, REQUEST_CHOOSER);
            }
        });
        btnPostSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingDialog.setMessage("Converting...");
                loadingDialog.show();

                // 画像をbase64にencode
                ImageView imageView = (ImageView)findViewById(R.id.img_view) ;
                Bitmap bmp = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String convertImageStr = Base64.encodeToString(byteArray, Base64.DEFAULT);

                StringEntity entity = null;
                try {
                    JSONObject jsonParams = new JSONObject();
                    jsonParams.put("user_id", "1");
                    jsonParams.put("spot_name", ((EditText)findViewById(R.id.et_spot_name)).getText().toString());
                    JSONArray imgAry = new JSONArray();
                    imgAry.put(convertImageStr);
                    jsonParams.put("images", imgAry);
//                    jsonParams.put("latitude", location.getLatitude());
//                    jsonParams.put("longitude", location.getLongitude());
                    jsonParams.put("latitude", location.getLatitude());
                    jsonParams.put("longitude",location.getLongitude());
                    jsonParams.put("description", ((EditText)findViewById(R.id.et_description)).getText().toString());
                    jsonParams.put("hint", ((EditText)findViewById(R.id.et_hint)).getText().toString());
                    entity = new StringEntity(jsonParams.toString(),"UTF-8");
                    // textView.setText(convertImageStr);
                } catch (Exception e) {

                }

                loadingDialog.hide();
                loadingDialog.setMessage("Posting...");
                loadingDialog.show();

                AsyncHttpClient client = new AsyncHttpClient();
                client.addHeader("Content-Type", "application/json");
                client.post(getBaseContext(), "http://1ffd1c85.ngrok.io/api/spot/post", entity, "application/json",new AsyncHttpResponseHandler() {
                    // client.post("172.31.30.52:1323", params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started
                        loadingDialog.show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        // called when response HTTP status is "200 OK"
                        String str = "";
                        Toast.makeText(getApplicationContext(), "OK!", Toast.LENGTH_SHORT).show();
                        loadingDialog.hide();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Toast.makeText(getApplicationContext(), "FAIL!", Toast.LENGTH_SHORT).show();
                        loadingDialog.hide();
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CHOOSER) {

            if(resultCode != RESULT_OK) {
                // キャンセル時
                return ;
            }

            Uri resultUri = (data != null ? data.getData() : m_uri);

            if(resultUri == null) {
                // 取得失敗
                return;
            }

//            // ギャラリーへスキャンを促す
//            MediaScannerConnection.scanFile(
//                    getContext(),
//                    new String[]{resultUri.getPath()},
//                    new String[]{"image/jpeg"},
//                    null
//            );

            // 画像を設定
            ImageView imageView = (ImageView)findViewById(R.id.img_view);
            imageView.setImageURI(resultUri);
        }
    }

    //位置情報が通知されるたびにコールバックされるメソッド
    @Override
    public void onLocationChanged(Location location){
//        this.location = location;
//        if (location == null || registerLocation == null) {
//            return;
//        }
//        TextView textView = (TextView) rootView.findViewById(R.id.location);
//        textView.setText(String.valueOf("now : " + location.getLatitude()) + ":" + String.valueOf(location.getLongitude()) + "\n" +
//                String.valueOf("reg : " + registerLocation.getLatitude()) + ":" + String.valueOf(registerLocation.getLongitude()) + "\n" +
//                String.valueOf("destance : " + getDistance(location, registerLocation)));
    }

    //ロケーションプロバイダが利用不可能になるとコールバックされるメソッド
    @Override
    public void onProviderDisabled(String provider) {
        //ロケーションプロバイダーが使われなくなったらリムーブする必要がある
    }

    //ロケーションプロバイダが利用可能になるとコールバックされるメソッド
    @Override
    public void onProviderEnabled(String provider) {
        //プロバイダが利用可能になったら呼ばれる
    }

    //ロケーションステータスが変わるとコールバックされるメソッド
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // 利用可能なプロバイダの利用状態が変化したときに呼ばれる
    }

}
