package com.pocketwallet.pocket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity {
    private EditText phonenumberInput;
    private EditText passwordInput;
    private Button login;
    private TextView signup;
    private String phoneNumber;
    private String password;
    private boolean doubleBackToExitPressedOnce = false;
    private SharedPreferences logInPreferences;

    //LOGIN API URL
    final String LOGIN_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/login";
    final String POSTFCM_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/fcmtoken";
    //---TEST---
    private Button loginTest1;
    private Button loginTest2;
    //----------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //---TEST BUTTONS---
        loginTest1 = (Button)findViewById(R.id.loginTest1);
        loginTest2 = (Button)findViewById(R.id.loginTest2);
        //----------

        //SETUP BUTTONS AND EDITTEXT
        phonenumberInput = (EditText)findViewById(R.id.loginPhone);
        passwordInput = (EditText)findViewById(R.id.loginPassword);
        login = (Button)findViewById(R.id.loginButton2);
        signup = (TextView)findViewById(R.id.signupButton);

        phonenumberInput.addTextChangedListener(loginTextWatcher);
        passwordInput.addTextChangedListener(loginTextWatcher);

        login.setOnClickListener (new View.OnClickListener() {
            public void onClick(View view){
                login(phoneNumber, password);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginTest1.setOnClickListener (new View.OnClickListener() {
            public void onClick(View view){
                LoginTestUser1();
            }
        });

        loginTest2.setOnClickListener (new View.OnClickListener() {
            public void onClick(View view){
                LoginTestUser2();
            }
        });


    }

    //POST LOGIN REQUEST
    private void login(String phoneNumber, String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("mode",0);
            jsonBody.put("phoneNumber", phoneNumber);
            jsonBody.put("password", password);

            System.out.println("Login Details: " + jsonBody);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        System.out.println("Response: " + response);
                        String result = response.getString("result");
                        String userId = response.getString("user_id");
                        System.out.println("Results: " + result);
                        System.out.println("User: " + userId);
                        if(!userId.equals("failed")){
                            updateToken();
                            postFCMToken(userId);
                            launchMainActivity(userId);
                        }else{
                            System.out.println("===================Failed to Login===================");
                        }

                    }catch(JSONException e){

                    }
                    //String userId;
                    //launchMainActivity(userId);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onBackPressed();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    //headers.put("Authorization", "Basic " + "c2FnYXJAa2FydHBheS5jb206cnMwM2UxQUp5RnQzNkQ5NDBxbjNmUDgzNVE3STAyNzI=");//put your token here
                    return headers;
                }
            };

            requestQueue.add(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateToken(){
        KEY_NAME = phoneNumber + "|" + password;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance( "SHA-256" );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // Change this to UTF-16 if needed
        md.update(KEY_NAME.getBytes( StandardCharsets.UTF_8 ) );
        byte[] digest = md.digest();
        KEY_NAME = String.format( "%064x", new BigInteger( 1, digest ));
        System.out.println("KEY_NAME: " + KEY_NAME);

        logInPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = logInPreferences.edit();
        editor.putString("KEY_NAME", KEY_NAME);
        editor.commit();
    }

    //POST FCMTOKEN
    private void postFCMToken(String userId) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String fcmToken = prefs.getString("FCM_TOKEN", "DEFAULT");
        System.out.println(fcmToken);
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", userId);
            jsonBody.put("fcm_token", fcmToken);

            System.out.println("Login Details: " + jsonBody);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, POSTFCM_URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.getString("result");
                        System.out.println("Results: " + result);
                        if (result.equals("success")) {
                            System.out.println("Post FCM Token Success!");
                        } else {
                            System.out.println("Post FCM Token Failed :(");
                        }
                    } catch (JSONException e) {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onBackPressed();
                }
            });
            requestQueue.add(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            phoneNumber = phonenumberInput.getText().toString().trim();
            password = passwordInput.getText().toString().trim();

            login.setEnabled(!phoneNumber.isEmpty() && !password.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //LAUNCH MAIN ACTIVITY
    public void launchMainActivity(String userId){
        //Update logged in
        logInPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        System.out.println("isLoggedIn = " + logInPreferences.getBoolean("isLoggedIn", false));
        SharedPreferences.Editor editor = logInPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("PhoneNumber", phoneNumber);
        editor.commit();

        System.out.println("Phone Number login = " + logInPreferences.getString("PhoneNumber", "DEFAULT"));
        System.out.println("isLoggedIn = " + logInPreferences.getBoolean("isLoggedIn", false));

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
        finish();
    }

    //Fingerprint
    String KEY_NAME = "FINGERPRINTAUTH";
    KeyStore keyStore;
    Cipher cipher;

    public void RequestFingerprint(final String userId){
        KEY_NAME = phoneNumber + "|" + password;

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance( "SHA-256" );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // Change this to UTF-16 if needed
        md.update(KEY_NAME.getBytes( StandardCharsets.UTF_8 ) );
        byte[] digest = md.digest();
        KEY_NAME = String.format( "%064x", new BigInteger( 1, digest ));
        System.out.println("KEY_NAME: " + KEY_NAME);

        logInPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = logInPreferences.edit();
        editor.putString("KEY_NAME", KEY_NAME);
        editor.commit();

        generateKey();
        initCipher();

        final FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(this);
        if (!fingerprintManagerCompat.isHardwareDetected()) {
            System.out.println("Device does not have fingerprint scanner");
            return;
        } else if (!fingerprintManagerCompat.hasEnrolledFingerprints()) {
            // User hasn't enrolled any fingerprints to authenticate with
            System.out.println("Devices does not have enrolled fingerprints");
            return;
        }

        Toast toast = Toast.makeText(getApplicationContext(),
                "Please scan your Fingerprint",
                Toast.LENGTH_SHORT);
        toast.show();

        FingerprintManagerCompat.CryptoObject cryptoObject = new FingerprintManagerCompat.CryptoObject(cipher);
        fingerprintManagerCompat.authenticate(cryptoObject, 0, new CancellationSignal(),
                new FingerprintManagerCompat.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errMsgId, CharSequence errString) {
                        super.onAuthenticationError(errMsgId, errString);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Fingerprint NOT RECOGNIZED",
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    @Override
                    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                        super.onAuthenticationHelp(helpMsgId, helpString);
                        //updateStatus(String.valueOf(helpString));
                        //
                        Toast toast = Toast.makeText(getApplicationContext(),
                                String.valueOf(helpString),
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    @Override
                    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Authentication Success!",
                                Toast.LENGTH_SHORT);
                        toast.show();

                        launchMainActivity(userId);
                    }
                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Authentication Failed.",
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }, null);
    }
    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
        }
    }
    private boolean initCipher() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);

        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    /*
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
    */

    //---TEST---
    private void LoginTestUser1(){
        String userId = "1cdd62b4-9f59-47c7-a7a5-919b6574bae3";
        RequestFingerprint(userId);
    }

    private void LoginTestUser2(){
        String userId = "b814cb32-eebf-4490-bdde-9695a7ef23a8";
        launchMainActivity(userId);
    }
    //----------
}
