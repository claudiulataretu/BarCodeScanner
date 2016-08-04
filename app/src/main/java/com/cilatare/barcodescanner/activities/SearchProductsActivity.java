package com.cilatare.barcodescanner.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cilatare.barcodescanner.AsyncTasks.MyProfilePhotoTask;
import com.cilatare.barcodescanner.AsyncTasks.ProductsNameTask;
import com.cilatare.barcodescanner.AsyncTasks.SearchProductTask;
import com.cilatare.barcodescanner.Constants;
import com.cilatare.barcodescanner.NavigationDrawerFragment;
import com.cilatare.barcodescanner.R;
import com.cilatare.barcodescanner.utils.GenerateToolbar;
import com.cilatare.barcodescanner.utils.MySharedPreferences;

public class SearchProductsActivity extends AppCompatActivity {

    private Toolbar toolbar = null;
    private ProgressBar progressBar = null;
    private AutoCompleteTextView productName = null;

    MySharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        Log.i(Constants.TAG, "onCreate: ");

        mySharedPreferences = new MySharedPreferences(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        productName = (AutoCompleteTextView) findViewById(R.id.search_product);

        GenerateToolbar generateToolbar = new GenerateToolbar(this, toolbar);
        generateToolbar.inflateToolbar();

        setUpDrawer();

        new ProductsNameTask(SearchProductsActivity.this, Constants.mCredential, progressBar, productName)
                .execute();

        productName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new SearchProductTask(SearchProductsActivity.this, Constants.mCredential, progressBar,
                        Constants.SEARCH_BY_NAME_PRODUCT_CODE)
                        .execute(adapterView.getItemAtPosition(i).toString());
            }
        });
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
