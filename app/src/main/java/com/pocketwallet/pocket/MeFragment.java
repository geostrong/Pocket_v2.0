package com.pocketwallet.pocket;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.github.omadahealth.lollipin.lib.managers.LockManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MeFragment extends Fragment {

    private static final int REQUEST_CODE_ENABLE = 11;
    private static final int REQUEST_CODE_UNLOCK = 12;

    private String userId;
    private SharedPreferences userPreferences;
    private TextView phoneNumber;
    private TextView profileName;
    private ImageView profileImage;
    private static final int REQUEST_IMAGE = 1;
    private static final int REQUEST_CAMERA = 2;
    Uri imageUri;

    public static class Item{
        public final String text;
        public final int icon;
        public Item(String text, Integer icon) {
            this.text = text;
            this.icon = icon;
        }
        @Override
        public String toString() {
            return text;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        ((MainActivity)getActivity()).getSupportActionBar().show();
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Me");
        ((MainActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        ((MainActivity)getActivity()).getSupportActionBar().setElevation(4);

        Bundle extras = new Bundle();
        extras = getArguments();
        if (extras != null) {
            userId = extras.getString("userId");
        }

        profileImage = (ImageView) view.findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        userPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String tempImageString = userPreferences.getString("profileImage", null);
        if(tempImageString != null) {
            Bitmap tempImage = decodeBase64(tempImageString);
            if (tempImage != null) {
                profileImage.setImageBitmap(tempImage);
            }
        }

        Button contractBtn = view.findViewById(R.id.contractBtn);
        contractBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), ContractActivity.class);
                newIntent.putExtra("userId",userId);
                startActivity(newIntent);
            }
        });

        Button logsBtn = (Button) view.findViewById(R.id.logsBtn);
        logsBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent testIntent = new Intent(getActivity(), TransactionLogsActivity.class);
                testIntent.putExtra("userId",userId);
                startActivity(testIntent);
            }
        });

        Button topUpBtn = (Button) view.findViewById(R.id.topUpBtn);
        topUpBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent dynamicIntent = new Intent(getActivity(), TopUpActivity.class);
                dynamicIntent.putExtra("userId",userId);
                startActivity(dynamicIntent);
            }
        });

        Button loyaltyBtn = (Button) view.findViewById(R.id.loyaltyCardsBtn);
        loyaltyBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent dynamicIntent = new Intent(getActivity(), LoyaltyActivity.class);
                dynamicIntent.putExtra("userId",userId);
                startActivity(dynamicIntent);
            }
        });

        Button dealsBtn = (Button) view.findViewById(R.id.dealBtn);
        dealsBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(LockManager.getInstance().isAppLockEnabled()) {
                    Intent intent = new Intent(getActivity(), CustomPinActivity.class);
                    if (!LockManager.getInstance().getAppLock().isPasscodeSet()) {
                        intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
                        startActivityForResult(intent, REQUEST_CODE_ENABLE);
                    } else {
                        intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                        startActivityForResult(intent, REQUEST_CODE_UNLOCK);
                    }
                }

            }
        });

        phoneNumber = view.findViewById(R.id.phoneNumber);
        profileName = view.findViewById(R.id.profileName);

        userPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        phoneNumber.setText(userPreferences.getString("PhoneNumber", "Phone Number"));
        profileName.setText(userPreferences.getString("user_name", "Name"));

        return view;
    }

    private void selectImage(){

        final Item[] items = {
                new Item("Camera", R.drawable.androidcameraicon),
                new Item("Gallery", R.drawable.androidgalleryicon),
                new Item("Cancel", 0),//no icon for this one
        };
        final ListAdapter adapter = new ArrayAdapter<Item>(
                getActivity(),
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                items){
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView)v.findViewById(android.R.id.text1);
                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                //Add margin between image and text (support various screen densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 1.0f);
                tv.setCompoundDrawablePadding(dp5);
                return v;
            }
        };

        new AlertDialog.Builder(getActivity())
                .setTitle("Select Profile Image")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if(item == 0){
                            Intent intent = new Intent(
                                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            // start the image capture Intent
                            startActivityForResult(intent, REQUEST_CAMERA);
                        }else if(item == 1){
                            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                            startActivityForResult(gallery,REQUEST_IMAGE);
                        }
                    }
                }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                bitmap = (Bitmap) data.getExtras().get("data");
                profileImage.setImageBitmap(bitmap);
            } else if (requestCode == REQUEST_IMAGE) {
                imageUri = data.getData();
                bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                    InputStream in;
                    try {
                        in = getActivity().getContentResolver().openInputStream(imageUri);
                        ExifInterface exifInterface = new ExifInterface(in);
                        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        Matrix matrix = new Matrix();
                        if (orientation == 6) {
                            matrix.postRotate(90);
                        }
                        else if (orientation == 3) {
                            matrix.postRotate(180);
                        }
                        else if (orientation == 8) {
                            matrix.postRotate(270);
                        }
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    profileImage.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("profileImage",encodeTobase64(bitmap));
                editor.commit();
            } else if (requestCode == REQUEST_CODE_ENABLE) {
                Toast.makeText(getActivity(), "PinCode enabled", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_CODE_UNLOCK) {
                Toast.makeText(getActivity(), "Unlocked", Toast.LENGTH_SHORT).show();
            }

        }
    }

    // method for bitmap to base64
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }
    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}

