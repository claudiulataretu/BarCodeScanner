package com.cilatare.barcodescanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cilatare.barcodescanner.AsyncTasks.MyProfilePhotoTask;
import com.cilatare.barcodescanner.AsyncTasks.SearchProductTask;
import com.cilatare.barcodescanner.Constants;
import com.cilatare.barcodescanner.NavigationDrawerFragment;
import com.cilatare.barcodescanner.R;
import com.cilatare.barcodescanner.utils.GenerateToolbar;
import com.cilatare.barcodescanner.utils.MySharedPreferences;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanProductsActivity extends AppCompatActivity {

    private Toolbar toolbar = null;
    private ProgressBar progressBar = null;

    private Button scanButton = null;

    MySharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_products);

        Log.i(Constants.TAG, "onCreate: ");

        mySharedPreferences = new MySharedPreferences(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        scanButton = (Button) findViewById(R.id.scan_product);

        GenerateToolbar generateToolbar = new GenerateToolbar(this, toolbar);
        generateToolbar.inflateToolbar();

        setUpDrawer();

        scanButton.setOnClickListener(new ScanBarcode());
    }

    private void setUpDrawer() {
        TextView profileNameTextView = (TextView) findViewById(R.id.profile_name);
        ImageView profileImageView = (ImageView) findViewById(R.id.profile_picture);

        profileNameTextView.setText(mySharedPreferences.getProfileName());
        new MyProfilePhotoTask(profileImageView).execute(mySharedPreferences.getProfilePhoto());

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drwr_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUpDrawer(R.id.nav_drwr_fragment, drawerLayout, toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case Constants.REQUEST_SCAN_BARCODE:

                IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

                if (scanningResult != null) {
                    String scanContent = scanningResult.getContents();
                    Log.i(Constants.TAG, "onActivityResult: " + scanContent);
                    new SearchProductTask(ScanProductsActivity.this, Constants.mCredential, progressBar,
                            Constants.SEARCH_BY_BARCODE_PRODUCT_CODE).execute(scanContent);
                }
                else {
                    Toast.makeText(this, "No scan data received", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    class ScanBarcode implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(ScanProductsActivity.this);
            scanIntegrator.initiateScan();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(Constants.TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(Constants.TAG, "onResume: ");
    }
}
