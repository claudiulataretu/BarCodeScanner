package com.cilatare.barcodescanner.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.cilatare.barcodescanner.Constants;
import com.cilatare.barcodescanner.R;
import com.cilatare.barcodescanner.activities.LaunchActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by LightSpark on 7/28/2016.
 */
public class GenerateToolbar {

    private Context context;
    private Toolbar toolbar;

    public GenerateToolbar(Context context, Toolbar toolbar) {
        this.context = context;
        this.toolbar = toolbar;
    }

    public void inflateToolbar() {
        toolbar.inflateMenu(R.menu.menu_main);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        if (Constants.mGoogleApiClient.isConnected()) {
                            Auth.GoogleSignInApi.signOut(Constants.mGoogleApiClient).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(@NonNull Status status) {
                                            Log.i(Constants.TAG, "Signed Out");
                                            Constants.mCredential.setSelectedAccountName(null);
                                            if (context.getClass() != LaunchActivity.class) {
                                                Intent intent = new Intent(context, LaunchActivity.class);
                                                context.startActivity(intent);
                                            }
                                        }
                                    }
                            );
                        }
                        break;
                    case R.id.settings:
                        Toast.makeText(context, "Settings", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.Exit:
                        Toast.makeText(context, "Exit", Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });
    }
}
