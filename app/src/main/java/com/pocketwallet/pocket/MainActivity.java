package com.pocketwallet.pocket;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity
        implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {

    private boolean doubleBackToExitPressedOnce = false;
    private TextView mTextMessage;
    private String userId;
    private Bundle extras;
    private Fragment homeFragment;
    private Fragment meFragment;
    private Fragment moreFragment;
    private Fragment settingsFragment;

    private String GETAUTHCODE_URL;
    private String authCode;

    private NfcAdapter nfcAdapter;
    private static final int MESSAGE_SENT = 1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = new Fragment();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = homeFragment;
                    break;
                case R.id.navigation_me:
                    selectedFragment = meFragment;
                    break;
                case R.id.navigation_more:
                    selectedFragment = moreFragment;
                    break;
            }
            return loadFragment(selectedFragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //SETUP FRAGMENTS
        homeFragment = new HomeFragment();
        meFragment = new MeFragment();
        moreFragment = new MoreFragment();
        settingsFragment = new SettingsFragment();

        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }

        GETAUTHCODE_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/"+ userId + "/auth-code";
        loadFragment(new HomeFragment());

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            //No NFC
        }else {
            nfcAdapter.setNdefPushMessageCallback(this, this);
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragment.setArguments(extras);
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            return true;
        }
        return false;
    }

    //Get Authentication Code
    private void getAuthCode(){
        StringRequest requestString;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestString = new StringRequest(Request.Method.GET, GETAUTHCODE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                authCode = response.toString();
                authCode = (String) authCode.subSequence(14,authCode.length()-2);
                System.out.println("AuthCode is: " + authCode);
                UpdateSharedPreference("authCode",authCode);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("", "Error: " + error.toString());
            }
        });
        requestQueue.add(requestString);
    }

    //NFC
    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        getAuthCode();
        String message = userId + "|" + authCode;
        System.out.println("The message is: " + message);
        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", message.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        return ndefMessage;
    }

    @Override
    public void onNdefPushComplete(NfcEvent arg0) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }

    /** This handler receives a message from onNdefPushComplete */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
                    System.out.println("MESSAGE SENT: " + msg);
                    break;
            }
        }
    };

    public void UpdateSharedPreference(String key, String value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
