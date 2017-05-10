package com.android.meetingbridge;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current date as the default date in the date picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //Create a new DatePickerDialog instance and return it
        /*
            DatePickerDialog Public Constructors - Here we uses first one
            public DatePickerDialog (Context context, DatePickerDialog.OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth)
            public DatePickerDialog (Context context, int theme, DatePickerDialog.OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth)
         */
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        //Do something with the date chosen by the user

        TextView tv = (TextView) getActivity().findViewById(R.id.postDateTV);
        String mon = "";
        String d = String.valueOf(day);
        // tv.setText(tv.setText() + "\nYear: " + year);
        if (day < 10) {
            d = "0" + String.valueOf(day);
        }
        switch (month) {
            case 0:
                mon = "01";
                break;
            case 1:
                mon = "02";
                break;
            case 2:
                mon = "03";
                break;
            case 3:
                mon = "04";
                break;
            case 4:
                mon = "05";
                break;
            case 5:
                mon = "06";
                break;
            case 6:
                mon = "07";
                break;
            case 7:
                mon = "08";
                break;
            case 8:
                mon = "09";
                break;
            case 9:
                mon = "10";
                break;
            case 10:
                mon = "11";
                break;
            case 11:
                mon = "12";
                break;
        }

        tv.setText(d + " " + mon + " " + year);
    }
}