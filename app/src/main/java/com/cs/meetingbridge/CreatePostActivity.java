package com.cs.meetingbridge;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreatePostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PostInfo post = new PostInfo();
        EditText postTitle = (EditText) findViewById(R.id.postTitle);
        EditText postDescription = (EditText) findViewById(R.id.postDescriptionTV);
        Button setTimeButton = (Button) findViewById(R.id.setTime);
        Button setDateButton = (Button) findViewById(R.id.setDate);
        final TextView timeView = (TextView) findViewById(R.id.postTimeTV);
        final TextView dateView = (TextView) findViewById(R.id.postDateTV);
        Button postButton = (Button) findViewById(R.id.postButton);
        PostInfo p = new PostInfo();
        final PostTime pt = new PostTime();
        final PostDate pd = new PostDate();

        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "content_create_post");
                //(getFragmentManager(),"post_layout");
            }
        });
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "content_post_create");
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String timeTemp = timeView.getText().toString();
                String dateTemp = dateView.getText().toString();
                String[] timeArray = timeTemp.split(" ");
                String[] dateArray = dateTemp.split(" ");
                pt.setHours(Integer.parseInt(timeArray[0]));
                pt.setMinutes(Integer.parseInt(timeArray[1]));
                pt.setAmpm(timeArray[2]);
                pd.setDay(Integer.parseInt(dateArray[0]));
                pd.setMonth(dateArray[1]);
                pd.setYear(Integer.parseInt(dateArray[2]));
                Toast.makeText(getApplicationContext(), String.valueOf(pd.getYear()), Toast.LENGTH_SHORT).show();
            }
        });


//        Toast.makeText(this,pt.getHours(),Toast.LENGTH_SHORT).show();
        p.setDescription(postDescription.getText().toString().trim());
        p.setId(1);


    }

}
