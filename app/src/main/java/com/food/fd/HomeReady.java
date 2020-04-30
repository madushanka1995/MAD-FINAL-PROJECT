package com.food.fd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeReady extends AppCompatActivity {

     Button product;
     Button profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_ready);


        product = (Button) findViewById(R.id.btn_product);

        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProductDetails();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateProfile();
            }
        });


}

    private void openUpdateProfile() {
        Intent intent = new Intent(HomeReady.this,SettingActivity.class);
    }

    private void openProductDetails() {

        Intent intent = new Intent(HomeReady.this,ProductDetails.class);
        startActivity(intent);
    }
    }
