package com.example.showoffassignment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.showoffassignment.lazyload.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class UserMedia extends AppCompatActivity {
    private GridView gvAllImages;
    private HashMap<String, String> userInfo;
    private ArrayList<String> imageThumbList = new ArrayList<String>();
    private Context context;
    private int WHAT_FINALIZE = 0;
    private static int WHAT_ERROR = 1;
    private ProgressDialog mProgress;

    public static final String TAG = "UserMedia";
    private static final String API_URL_Media = "https://graph.instagram.com/me/media";
    private static final String API_URL = "https://graph.instagram.com/me";

    private InstagramApp mApp;
    InstagramSession mSession;
    String mAccessToken;
    GridView gridView;
    TextView username;



    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (mProgress != null && mProgress.isShowing())
                mProgress.dismiss();
            Log.v("Error msg",""+msg.what);
            if (msg.what == WHAT_FINALIZE) {
                //userInfo = mApp.getUserInfo();
                //setImageGridAdapter();
            } else {

                        Toast.makeText(context, "Session has expired...logging out", Toast.LENGTH_SHORT).show();
//                        mSession.resetAccessToken();
//                        finish();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_media);
        gridView = (GridView) findViewById(R.id.gridview); // init GridView
        username = (TextView) findViewById(R.id.userName);



        //getAllMediaImages(handler);
        context = UserMedia.this;
        mSession = new InstagramSession(context);
        mAccessToken = mSession.getAccessToken();

        Bundle bundle = getIntent().getExtras();

//Extract the data…
        String name = bundle.getString("Username");


        userInfo = (HashMap<String, String>) getIntent().getSerializableExtra(
                "userInfo");
        //String name = getIntent().getStringExtra("Username");

        Log.v("Username",""+name);

        username.setText(name);



       // fetchUserName(handler);
        getAllMediaImages(handler);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_name) {
            disconnectUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void getAllMediaImages(final Handler handler) {
        mProgress = new ProgressDialog(context);
        mProgress.setMessage("Loading ...");
        mProgress.show();

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Fetching media info");
                int what = WHAT_FINALIZE;
                try {
//					URL url = new URL(API_URL + "/users/" + mSession.getId()
//							+ "/?access_token=" + mAccessToken);

                    URL url = new URL(API_URL_Media+"?fields=id,caption,media_type,media_url,thumbnail_url"
                            + "&access_token="+mAccessToken);

                    Log.d(TAG, "Opening URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.connect();
                    String response = Utils.streamToString(urlConnection
                            .getInputStream());
                    System.out.println(response);
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response)
                            .nextValue();
                    JSONArray data=jsonObj.getJSONArray("data");
                    Log.v("JsonArray",""+data);
                    final ArrayList <MediaData> array= new ArrayList <MediaData> ();
                    for (int i = 0; i < data.length(); i++)
                    {
                        MediaData objectClass = new MediaData();
                        JSONObject json_data = data.getJSONObject(i);
                        objectClass.setId(json_data.getString("id"));
                        objectClass.setMediaType(json_data.getString("media_type"));
                        objectClass.setMediaUrl(json_data.getString("media_url"));
                        array.add(objectClass);
                    }

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            MediaAdapter mediaAdapter= new MediaAdapter(context,array);
                            gridView.setAdapter(mediaAdapter);
                            // Stuff that updates the UI

                        }
                    });


                } catch (Exception ex) {
                    what = WHAT_ERROR;
                    ex.printStackTrace();
                }
                mProgress.dismiss();
                handler.sendMessage(handler.obtainMessage(what, 2, 0));
            }
        }.start();

    }


    private void disconnectUser() {

            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    UserMedia.this);
            builder.setMessage("Disconnect from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    mSession.resetAccessToken();
                                    finish();
                                    // btnConnect.setVisibility(View.VISIBLE);

                                    // tvSummary.setText("Not connected");
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();

    }



}
