package com.android.meetingbridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static java.lang.Thread.sleep;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Button signup = (Button) findViewById(R.id.signup);
        Button login = (Button) findViewById(R.id.login);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            signup.setVisibility(View.GONE);
            login.setVisibility(View.GONE);
            Thread myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        sleep(3000);
                        startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            myThread.start();
        } else {
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SplashScreen.this, SignupActivity.class));
                    finish();
                }
            });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();

                }
            });
        }
    }
}