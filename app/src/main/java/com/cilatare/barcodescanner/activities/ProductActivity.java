package com.cilatare.barcodescanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cilatare.barcodescanner.AsyncTasks.AddNewProductTask;
import com.cilatare.barcodescanner.AsyncTasks.MyProfilePhotoTask;
import com.cilatare.barcodescanner.AsyncTasks.UpdateProductTask;
import com.cilatare.barcodescanner.Constants;
import com.cilatare.barcodescanner.NavigationDrawerFragment;
import com.cilatare.barcodescanner.R;
import com.cilatare.barcodescanner.model.Product;
import com.cilatare.barcodescanner.utils.GenerateToolbar;
import com.cilatare.barcodescanner.utils.MySharedPreferences;

public class ProductActivity extends AppCompatActivity {

    private Toolbar toolbar = null;
    private ProgressBar progressBar = null;

    private EditText barcodeEditText = null;
    private EditText nameEditText = null;
    private EditText priceEditText = null;
    private EditText quantityEditText = null;

    private Product product = null;

    private Button productButton = null;

    MySharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        mySharedPreferences = new MySharedPreferences(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        barcodeEditText = (EditText) findViewById(R.id.product_barcode);
        nameEditText = (EditText) findViewById(R.id.product_name);
        priceEditText = (EditText) findViewById(R.id.product_price);
        quantityEditText = (EditText) findViewById(R.id.product_quantity);

        productButton = (Button) findViewById(R.id.product_button);
        productButton.setText(R.string.add_product);

        GenerateToolbar generateToolbar = new GenerateToolbar(this, toolbar);
        generateToolbar.inflateToolbar();

        Intent intent = getIntent();
        product = intent.getParcelableExtra(Constants.EXTRA_PRODUCT);

        setUpFields(product);

        setUpDrawer();

        productButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product modifiedProduct = new Product(
                        barcodeEditText.getText().toString(),
                        nameEditText.getText().toString(),
                        Double.valueOf(priceEditText.getText().toString()),
                        Double.valueOf(quantityEditText.getText().toString())
                );

                if (productButton.getText().equals("ADD PRODUCT")) {
                    new AddNewProductTask(ProductActivity.this, Constants.mCredential, progressBar).execute(modifiedProduct);
                }
                else if (productButton.getText().equals("MODIFY PRODUCT")) {
                    new UpdateProductTask(ProductActivity.this, Constants.mCredential, progressBar).execute(modifiedProduct);
                }
            }
        });
    }

    private void setUpFields(Product product) {

        if (!product.getBarcode().isEmpty() && !product.getName().isEmpty() &&
                !product.getPrice().isNaN() && !product.getQuantity().isNaN()) {
            productButton.setText(R.string.modify_product);
        }

        if (!product.getBarcode().isEmpty()) {
            barcodeEditText.setText(product.getBarcode());
        }

        if (!product.getName().isEmpty()) {
            nameEditText.setText(product.getName());
        }

        if (!product.getPrice().isNaN()) {
            priceEditText.setText(product.getPrice().toString());
        }

        if (!product.getQuantity().isNaN()) {
            quantityEditText.setText(product.getQuantity().toString());
        }
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
}
