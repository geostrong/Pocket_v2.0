package com.pocketwallet.pocket;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class RequestActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    Fragment selectedFragment;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            //No NFC
        }
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        System.out.println("User idddddddddd: " + userId);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            selectedFragment = new Fragment();
            switch (position) {
                case 0:
                    selectedFragment = new RequestFragment_QR();
                    break;
                case 1:
                    selectedFragment = new RequestFragment_NFC();
                    break;
            }

            return selectedFragment;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        getTagInfo(intent);
    }

    private void getTagInfo(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        processIntent(intent);
    }

    public void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        String payeeUserId = new String(msg.getRecords()[0].getPayload());
        payeeUserId = (String) payeeUserId.subSequence(0,36);
        String authCode = new String(msg.getRecords()[0].getPayload());
        authCode = (String) authCode.subSequence(37,authCode.length());

        // record 0 contains the MIME type, record 1 is the AAR, if present
        System.out.println("NFC MESSAGE RECEIVED: " + new String(msg.getRecords()[0].getPayload()));
        if(selectedFragment instanceof RequestFragment_NFC){
            RequestFragment_NFC nfcFragment = (RequestFragment_NFC) selectedFragment;
            nfcFragment.processNFC(userId, payeeUserId,authCode);
        }
    }
}
