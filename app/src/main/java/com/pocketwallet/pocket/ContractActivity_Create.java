package com.pocketwallet.pocket;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class ContractActivity_Create extends AppCompatActivity {

    private static final String TAG = "ContractActivity_Create";

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract__create);

        // Calendar dropdown
        mDisplayDate = (TextView) findViewById(R.id.tvDate);

        mDisplayDate.setOnClickListener(new View.OnClickListener(){
            @Override
                public void onClick(View view){
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ContractActivity_Create.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                Log.d(TAG, "onDateSet: dd/mm/yy: " + dayOfMonth + "/" + month + "/" + year);
                String monthWord = "";

                if (month == 1){
                    monthWord = "Jan";
                }
                else if (month == 2){
                    monthWord = "Feb";
                }
                else if (month == 3){
                    monthWord = "Mar";
                }
                else if (month == 4){
                    monthWord = "Apr";
                }
                else if (month == 5){
                    monthWord = "May";
                }
                else if (month == 6){
                    monthWord = "Jun";
                }
                else if (month == 7){
                    monthWord = "Jul";
                }
                else if (month == 8){
                    monthWord = "Aug";
                }
                else if (month == 9){
                    monthWord = "Sep";
                }
                else if (month == 10){
                    monthWord = "Oct";
                }
                else if (month == 11){
                    monthWord = "Nov";
                }
                else if (month == 12){
                    monthWord = "Dec";
                }

                String date = "Ends: " + dayOfMonth + " " + monthWord + " " + year;
                mDisplayDate.setText(date);
            }
        };

        // Frequency
        Spinner contractFrequency = (Spinner) findViewById(R.id.frequency);

        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<String>(ContractActivity_Create.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.frequencies));

        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contractFrequency.setAdapter(frequencyAdapter);
    }
}
