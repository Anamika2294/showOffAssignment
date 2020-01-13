package com.example.showoffassignment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private InstagramApp mApp;
    private Button btnConnect, btnViewInfo, btnGetAllImages, btnFollowers,
            btnFollwing;
    InstagramSession mSession;
    String mAccessToken;
    Context context;

    private LinearLayout llAfterLoginView;
    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                String username = mApp.getUserName();
                Log.v("Starting","Handle message Activity"+username);
                Intent i=new Intent(MainActivity.this, UserMedia.class);
                Bundle bundle = new Bundle();
                //Add your data from getFactualResults method to bundle
                bundle.putString("Username", username);
                //Add the bundle to the intent
                i.putExtras(bundle);
                startActivity(i);
//                startActivity(new Intent(MainActivity.this, UserMedia.class)
//                        .putExtra("Username",username));
            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Toast.makeText(MainActivity.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        mSession = new InstagramSession(context);
        mAccessToken = mSession.getAccessToken();

       if(mAccessToken == null){
            mApp = new InstagramApp(this, ApplicationData.CLIENT_ID,
                    ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
            mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

                @Override
                public void onSuccess() {

                     mApp.fetchUserName(handler);
                    //mApp.getAllMediaImages(handler);



                }

                @Override
                public void onFail(String error) {
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT)
                            .show();
                }
            });
       }
       else{
           Log.v("Starting","Access toke activity");
           startActivity(new Intent(MainActivity.this, UserMedia.class)
                   .putExtra("userInfo",userInfoHashmap ));
       }



        setWidgetReference();



        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.authorize();
            }
        });

        // OAuthAuthenticationListener listener ;

    }




    private void setWidgetReference() {

        btnConnect = (Button) findViewById(R.id.btnConnect);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mApp = new InstagramApp(this, ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);

        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {

                 mApp.fetchUserName(handler);

            }

            @Override
            public void onFail(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}
