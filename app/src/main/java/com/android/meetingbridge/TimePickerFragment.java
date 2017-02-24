package com.android.meetingbridge;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //Do something with the user chosen time
        //Get reference of host activity (XML Layout File) TextView widget

        TextView tv = (TextView) getActivity().findViewById(R.id.postTimeTV);
        tv.setText("String");
        String min = String.valueOf(minute);
        //Set a message for user
        if (minute < 10) {
            min = "0" + min;
        }
        String aMpM = "AM";
        if (hourOfDay > 11) {
            aMpM = "PM";
        }

        //Make the 24 hour time format to 12 hour time format
        int currentHour;
        if (hourOfDay > 11) {
            currentHour = hourOfDay - 12;
        } else {
            currentHour = hourOfDay;
        }

        if (currentHour == 0) {
            currentHour = 12;
        }
        String currentH = String.valueOf(currentHour);
        if (currentHour < 10) {
            currentH = 0 + currentH;
        }
        //Display the user changed time on TextView
        tv.setText(currentH + " " + min + " " + aMpM);
        System.out.println(currentH + " " + min + " " + aMpM);
    }

}
