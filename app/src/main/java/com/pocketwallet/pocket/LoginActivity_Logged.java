package com.pocketwallet.pocket;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class LoginActivity_Logged extends AppCompatActivity{

    final String LOGIN_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/login";
    final String POSTFCM_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/fcmtoken";
    final String DIALOG_FRAGMENT_TAG = "fingerprintDialogFragment";

    private SharedPreferences userPreferences;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean useFingerprint = false;

    private Button fingerprintButton;
    private Button loginButton2;
    private TextView passwordInput;
    private TextInputLayout passwordInputLayout;
    private ImageView profileImage;
    Bitmap tempImage;
    String userId;
    String phoneNumber;
    String password;
    String userName;

    //SessionToken
    String sessionToken;
    String sessionTokenExpiry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_loggedin);

        loadPreferences();
        passwordInputLayout = findViewById(R.id.textInputLayout3);
        fingerprintButton = findViewById(R.id.fingerprintButton);
        fingerprintButton.setOnClickListener (new View.OnClickListener() {
            public void onClick(View view){
                RequestFingerprint();
            }
        });

        passwordInput = findViewById(R.id.loginPassword);
        passwordInput.addTextChangedListener(loginTextWatcher);

        loginButton2 = findViewById(R.id.loginButton2);
        loginButton2.setOnClickListener (new View.OnClickListener() {
            public void onClick(View view){
                password = passwordInput.getText().toString();
                password = SHA256.hashSHA256(password);
                login(phoneNumber,"-","0",password);
            }
        });

        TextView name = findViewById(R.id.signinName);
        name.setText(userName);

        profileImage = (ImageView) findViewById(R.id.profile_image);
        if (tempImage != null) {
            profileImage.setImageBitmap(tempImage);
        }
        if (useFingerprint) {
            RequestFingerprint();
        }

        new RSSPullService(getApplicationContext());

    }
    private void loadPreferences() {
        userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        useFingerprint = userPreferences.getBoolean("useFingerprint", false);
        KEY_NAME = userPreferences.getString("KEY_NAME", "DEFAULT");
        phoneNumber = userPreferences.getString("PhoneNumber", "DEFAULT");
        userName = userPreferences.getString("user_name", "Name");
        String tempImageString = userPreferences.getString("profileImage", null);
        if(tempImageString != null) {
            tempImage = decodeBase64(tempImageString);
        }
    }

    //POST LOGIN REQUEST
    private void login(String phoneNumber, String token, String mode,String password) {
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        final RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        try {
            JSONObject jsonBody = new JSONObject();
            if(mode.equals("1")) {
                jsonBody.put("mode", 1);
                jsonBody.put("phoneNumber", phoneNumber);
                jsonBody.put("token", token);
            }else{
                jsonBody.put("mode", 0);
                jsonBody.put("phoneNumber", phoneNumber);
                jsonBody.put("password", password);
            }
            System.out.println("Login Details: " + jsonBody);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        System.out.println("Response: " + response);
                        String result = response.getString("result");
                        String userId = response.getString("user_id");
                        JSONObject testToken = response.getJSONObject("session_token");
                        sessionToken = testToken.getString("token");
                        UpdateSharedPreference("per_transaction_limit",response.getString("per_transaction_limit"));
                        UpdateSharedPreference("daily_limit",response.getString("daily_limit"));

                        System.out.println("Results: " + result);
                        System.out.println("User: " + userId);
                        System.out.println("Session Token is :" + sessionToken);
                        if(!result.equals("failed")){
                            requestQueue.stop();
                            postFCMToken(userId);
                            launchMainActivity(userId);
                        }else{
                            System.out.println("===================Failed to Login===================");
                        }

                    }catch(JSONException e){

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if(error.networkResponse.statusCode == 500 || error.networkResponse.statusCode == 400){
                        passwordInputLayout.setError("Wrong password! Please ensure you enter the correct password");
                    }
                    requestQueue.stop();
                }
            });
            requestQueue.add(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //POST FCMTOKEN
    private void postFCMToken(String userId) {
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        final RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
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
                        requestQueue.stop();
                    } catch (JSONException e) {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestQueue.stop();
                    onBackPressed();
                }
            });
            requestQueue.add(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //LAUNCH MAIN ACTIVITY
    public void launchMainActivity(String userId){
        Intent intent = new Intent(LoginActivity_Logged.this, MainActivity.class);
        intent.putExtra("userId",userId);
        UpdateSharedPreference("sessionToken",sessionToken);
        startActivity(intent);
        finish();
    }

    //Fingerprint
    String KEY_NAME = "";
    KeyStore keyStore;{
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    };
    Cipher cipher;

    public void RequestFingerprint(){
        generateKey();
        boolean validCipher = initCipher();

        if(validCipher == true) {
            //FINGERPRINT DIALOG
            FingerprintAuthenticationDialogFragment fragment = new FingerprintAuthenticationDialogFragment();
            fragment.setCryptoObject(new FingerprintManager.CryptoObject(cipher));
            fragment.setStage(FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
            fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
        }
        /*
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
                        //updateStatus(String.valueOf(errString));
                        //biometricCallback.onAuthenticationError(errMsgId, errString);
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
                        login(phoneNumber,KEY_NAME,"1","-");

                        //dismissDialog();
                        //biometricCallback.onAuthenticationSuccessful();
                    }
                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Authentication Failed.",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        //updateStatus(context.getString(R.string.biometric_failed));
                        //biometricCallback.onAuthenticationFailed();
                    }
                }, null);

        */
    }

    public void onFingerprintCallback (){
        System.out.println("KEY_NAME: " + KEY_NAME);
        login(phoneNumber,KEY_NAME,"1","-");;
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
            //throw new RuntimeException("Failed to init Cipher", e);
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            dialog.cancel();
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity_Logged.this);
            builder.setMessage("1) Biometric does not work on this device, OR \n\n2)You do not have any registered fingerprints")
                    .setPositiveButton("Ok", dialogClickListener).show();
            return false;
        }
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

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            password = passwordInput.getText().toString().trim();
            loginButton2.setEnabled(!password.isEmpty());
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public void UpdateSharedPreference(String key, String value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }
}
