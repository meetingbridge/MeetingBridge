package com.android.meetingbridge;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseIntArray;
import android.view.View;

public class PermissionClass extends Activity {
    private SparseIntArray errorString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        errorString = new SparseIntArray();
        super.onCreate(savedInstanceState);

    }

    public void onPermissionGranted(int requestCode) {

    }

    public void requestAppPermission(final String[] requestPermission, final int stringId, final int requestCode) {
        errorString.put(requestCode, stringId);
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        boolean ifPersmissionGranted = false;
        for (String permission : requestPermission) {
            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this, permission);
            ifPersmissionGranted = ifPersmissionGranted || ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ifPersmissionGranted) {
                Snackbar.make(findViewById(android.R.id.content), stringId, Snackbar.LENGTH_INDEFINITE).setAction("Grant", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(PermissionClass.this, requestPermission, requestCode);
                    }
                }).show();
            } else {
                ActivityCompat.requestPermissions(this, requestPermission, requestCode);
            }
        } else onPermissionGranted(requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int permission : grantResults) {
            permissionCheck = permissionCheck + permission;
        }
        if ((grantResults.length > 0) && PackageManager.PERMISSION_GRANTED == permissionCheck) {
            onPermissionGranted(requestCode);
        } else {
            Snackbar.make(findViewById(android.R.id.content), errorString.get(requestCode), Snackbar.LENGTH_INDEFINITE).setAction("Enable", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.setData(Uri.parse("package:" + getPackageName()));
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(i);
                }
            }).show();
        }

    }
}