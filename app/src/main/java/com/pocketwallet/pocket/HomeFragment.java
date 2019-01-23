package com.pocketwallet.pocket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HomeFragment extends Fragment {

    private String GETBALANCE_URL;
    private String GETAUTHCODE_URL;
    private String userId;

    private TextView lastUpdatedTxt;
    private TextView balanceTxt;

    private ConstraintLayout myPocketButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        getActivity().setTitle("Home");

        //<--Setup buttons-->
        Button quickQrBtn = (Button) view.findViewById(R.id.quickQRBtn);
        quickQrBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), QuickQrActivity.class);
                startActivity(newIntent);
            }
        });

        Button scanQrBtn = (Button) view.findViewById(R.id.scanQRBtn);
        scanQrBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), ScanQRActivity.class);
                newIntent.putExtra("userId",userId);
                startActivity(newIntent);
            }
        });

        Button requestBtn = (Button) view.findViewById(R.id.reqBtn);
        requestBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), RequestActivity.class);
                newIntent.putExtra("userId",userId);
                startActivity(newIntent);
            }
        });

        myPocketButton = (ConstraintLayout) view.findViewById(R.id.myPocketButton);
        myPocketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBalance();
            }
        });
        //<--End of setup buttons-->

        lastUpdatedTxt = (TextView)view.findViewById(R.id.lastUpdated);
        balanceTxt = (TextView)view.findViewById(R.id.balance);

        Button testBtn = (Button) view.findViewById(R.id.testButton);
        testBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent dynamicIntent = new Intent(getActivity(), ScanQRDynamicActivity.class);
                startActivity(dynamicIntent);
            }
        });

        Bundle extras = new Bundle();
        extras = getArguments();
        if (extras != null) {
            userId = extras.getString("userId");
        }
        //Update Balance
        GETBALANCE_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/"+ userId + "/balance";
        updateBalance();
        return view;
    }

    public void updateBalance(){
        RequestQueue requestQueue = Volley.newRequestQueue(this.getActivity());
        JsonObjectRequest requestJsonObject = new JsonObjectRequest(Request.Method.GET, GETBALANCE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    final String balance = response.getString("balance");
                    final String updatedAsOf;
                    System.out.println(response.getString("balance"));
                    updatedAsOf = response.getString("AS_OF").substring(0, response.getString("AS_OF").length() - 5);
                    SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:a");
                    localDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
                    Date updatedTime = localDateFormat.parse(updatedAsOf);
                    Date currentTime = localDateFormat.parse(localDateFormat.format(new Date()));
                    long difference = currentTime.getTime() - updatedTime.getTime();
                    System.out.println("Current Time = " + currentTime.toString() + " | Updated Time = " + updatedTime.toString() + " | Difference = " + difference/1000);

                    final String lastUpdatedText;
                    if (difference < 60) {
                         lastUpdatedText = "Now";
                    } else {
                        lastUpdatedText = difference/60 + " minutes ago";
                    }

                    lastUpdatedTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            balanceTxt.setText("$"+balance);
                            lastUpdatedTxt.setText(lastUpdatedText);
                        }
                    });
                }catch(JSONException e){
                    e.printStackTrace();
                } catch (ParseException p) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("", "Error: " + error.toString());
            }
        });
        requestQueue.add(requestJsonObject);
    }

}
