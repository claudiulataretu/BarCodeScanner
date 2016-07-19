package com.cilatare.barcodescanner;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class LaunchActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Welcome !");
        toolbar.setSubtitle("Folks !");




        //Compatibility by Java

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            toolbar.setElevation(10f);
//        }

        toolbar.setLogo(R.drawable.good_day);
        toolbar.setNavigationIcon(R.drawable.navigation_back);

        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {



                return true;
            }
        });

    }
}
