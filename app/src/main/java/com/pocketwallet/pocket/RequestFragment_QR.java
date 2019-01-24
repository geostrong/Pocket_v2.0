package com.pocketwallet.pocket;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class RequestFragment_QR extends Fragment {
    @Nullable

    private String userId;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_fragment__qr, container, false);

        userId = ((RequestActivity_Tabs)getActivity()).getUserId();

        //<--Setup image view-->
        final ImageView generatedQR = (ImageView) view.findViewById(R.id.generatedDynamicQR);
        //<--End-->


        final EditText amountEntered = (EditText) view.findViewById(R.id.requestAmountQR);

        //<--Setup buttons-->
        final Button generateQrBtn = (Button) view.findViewById(R.id.generate_qr);
        generateQrBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    System.out.println(amountEntered.getText().toString());
                    String amount = amountEntered.getText().toString();
                    String toQR = userId + "|" + amount;
                    System.out.println("TOQR: " + toQR);
                    BitMatrix bitMatrix = multiFormatWriter.encode(toQR, BarcodeFormat.QR_CODE,generatedQR.getWidth(),generatedQR.getHeight());
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    generatedQR.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
        //<---->
        return view;
    }



}
