package com.cilatare.barcodescanner.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cilatare.barcodescanner.R;
import com.cilatare.barcodescanner.utils.GenerateToolbar;
import com.cilatare.barcodescanner.utils.MySharedPreferences;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RadioButton roRadio = null;
    private RadioButton enRadio = null;
    private RadioGroup radioGroup = null;

    private String language;
    private MySharedPreferences mySharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        roRadio = (RadioButton) findViewById(R.id.ro_radioButton);
        enRadio = (RadioButton) findViewById(R.id.en_radioButton);
        enRadio.setChecked(true);

        GenerateToolbar generateToolbar = new GenerateToolbar(this, toolbar);
        generateToolbar.inflateToolbar();

        mySharedPreferences = new MySharedPreferences(this);
        language = mySharedPreferences.getLanguage();

        if (language != null) {
            switch (language) {
                case "ro":
                    roRadio.setChecked(true);
                    break;
                case "en":
                    enRadio.setChecked(true);
                    break;
            }
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.ro_radioButton:
                if (checked) {
                    mySharedPreferences.setLanguage("ro");
                    setLocale("ro");
                }
                break;
            case R.id.en_radioButton:
                if (checked) {
                    mySharedPreferences.setLanguage("en");
                    setLocale("en");
                }
                break;
        }
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent intent = new Intent(SettingsActivity.this, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
