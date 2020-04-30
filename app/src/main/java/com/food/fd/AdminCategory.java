package com.food.fd;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminCategory extends AppCompatActivity {

    ImageView chicken;
    ImageView egg,pizza,bun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        chicken = (ImageView) findViewById(R.id.chicken);
        egg = (ImageView) findViewById(R.id.egg);
        pizza = (ImageView) findViewById(R.id.piza);
        bun = (ImageView) findViewById(R.id.bunn);



       chicken.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(AdminCategory.this,AdminAddProduct.class);
               intent.putExtra("category","chicken");
               startActivity(intent);
           }
       });

       egg.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(AdminCategory.this,AdminAddProduct.class);
               intent.putExtra("category","Egg");
               startActivity(intent);
           }
       });
       pizza.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(AdminCategory.this,AdminAddProduct.class);
               intent.putExtra("category","Pizza");
               startActivity(intent);
           }
       });
       bun.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(AdminCategory.this,AdminAddProduct.class);
               intent.putExtra("category","Bun");
               startActivity(intent);
           }
       });



    }
}
