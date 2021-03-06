package com.pocketwallet.pocket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class HomeFragment extends Fragment {

    private String GETBALANCE_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/";
    private String userId;

    private TextView lastUpdatedTxt;
    private TextView balanceTxt;

    private ConstraintLayout myPocketButton;

    //Session Token
    private String sessionToken;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Pocket");
        ((MainActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this.getActivity(), R.color.colorPrimary)));
        ((MainActivity)getActivity()).getSupportActionBar().setElevation(0);

        //Retrieve bundle information
        Bundle extras = new Bundle();
        extras = getArguments();
        if (extras != null) {
            userId = extras.getString("userId");
            //SET URL
            if(!GETBALANCE_URL.contains("/balance")) {
                GETBALANCE_URL = GETBALANCE_URL + userId + "/balance";
            }
        }
        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        sessionToken = userPreferences.getString("sessionToken", "");

        //<--Setup buttons-->
        Button quickQrBtn = view.findViewById(R.id.quickQRBtn);
        quickQrBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), QuickQrActivity.class);
                newIntent.putExtra("userId",userId);
                startActivity(newIntent);
            }
        });

        Button scanQrBtn = view.findViewById(R.id.scanQRBtn);
        scanQrBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), ScanQRActivity.class);
                newIntent.putExtra("userId",userId);
                startActivity(newIntent);
            }
        });

        Button requestBtn = view.findViewById(R.id.reqBtn);
        requestBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), RequestActivity.class);
                newIntent.putExtra("userId",userId);
                startActivity(newIntent);
            }
        });

        Button payBtn = view.findViewById(R.id.payButton);
        payBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), TransferActivity.class);
                newIntent.putExtra("userId",userId);
                startActivity(newIntent);
            }
        });

        myPocketButton = view.findViewById(R.id.myPocketButton);
        myPocketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBalance();
            }
        });
        //<--End of setup buttons-->

        lastUpdatedTxt = view.findViewById(R.id.lastUpdated);
        balanceTxt = view.findViewById(R.id.balance);

        //Update balance
        //updateBalance();

        return view;
    }

    public void updateBalance(){
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        final RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        JsonObjectRequest requestJsonObject = new JsonObjectRequest(Request.Method.GET, GETBALANCE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    final String balance = response.getString("balance");
                    final String updatedAsOf;
                    updatedAsOf = response.getString("AS_OF").substring(0, response.getString("AS_OF").length() - 5);
                    SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:a",Locale.US);
                    localDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
                    Date updatedTime = localDateFormat.parse(updatedAsOf);
                    Date currentTime = localDateFormat.parse(localDateFormat.format(new Date()));
                    long difference = currentTime.getTime() - updatedTime.getTime();

                    final String lastUpdatedText;
                    if (difference < 60) {
                         lastUpdatedText = "Now";
                    } else {
                        lastUpdatedText = difference/60 + " minutes ago";
                    }

                    balanceTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                balanceTxt.setText("$" + balance);
                                lastUpdatedTxt.setText(lastUpdatedText);
                                requestQueue.stop();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }catch(JSONException e){
                    e.printStackTrace();
                }catch (ParseException p) {
                    p.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("", "Error: " + error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + sessionToken);
                System.out.println("Header: " + headers.values());
                return headers;
            }
        };
        requestQueue.add(requestJsonObject);
    }
    @Override
    public void onResume() {
        super.onResume();
        updateBalance();
    }
}
