package com.cs.meetingbridge;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends PermissionClass {


    private static final int REQUEST_PERMISSION = 10;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnsignup;
    private Button btnlogin;
    private Button btnForgot;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestAppPermission(new String[]
                        {android.Manifest.permission.READ_CONTACTS,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,},
                R.string.permission_msg, REQUEST_PERMISSION);

        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);
        btnlogin = (Button) findViewById(R.id.btnLogin);
        btnsignup = (Button) findViewById(R.id.btnSignup);
        btnForgot = (Button) findViewById(R.id.btnForgot);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Enter email address!");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Enter password!");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (password.length() < 6) {
                                etPassword.setError("Password too short, enter minimum 8 characters");
                            } else {
                                Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }


    @Override
    public void onPermissionGranted(int requestCode) {
        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
    }

}

