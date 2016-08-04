package com.cilatare.barcodescanner.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.Toast;

import com.cilatare.barcodescanner.R;
import com.cilatare.barcodescanner.activities.LaunchActivity;
import com.cilatare.barcodescanner.activities.SettingsActivity;

import java.util.Locale;

/**
 * Created by LightSpark on 7/28/2016.
 */
public class GenerateToolbar {

    private Context context;
    private Toolbar toolbar;
    private MySharedPreferences mySharedPreferences;

    public GenerateToolbar(Context context, Toolbar toolbar) {
        this.context = context;
        this.toolbar = toolbar;
        mySharedPreferences = new MySharedPreferences(context);
    }

    public void inflateToolbar() {
        toolbar.inflateMenu(R.menu.menu_main);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        if (mySharedPreferences.getConnected()) {
                            mySharedPreferences.setConnected(false);
                            if (context.getClass() != LaunchActivity.class) {
                                Intent intent = new Intent(context, LaunchActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                            }
                        }
                        break;
                    case R.id.settings:
                        Toast.makeText(context, "Settings", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, SettingsActivity.class);
                        context.startActivity(intent);
                        break;
                }
                return true;
            }
        });
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
