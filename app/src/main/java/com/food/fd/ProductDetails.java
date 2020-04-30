package com.food.fd;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.food.fd.Model.Products;
import com.food.fd.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProductDetails extends AppCompatActivity {


    TextView productName,productDes,productPrice;
     ImageView productImage;
     ElegantNumberButton numberButton;
     private String productID = "";
     private Button addToCart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        addToCart = (Button) findViewById(R.id.btn_addToCart);

        productName = (TextView) findViewById(R.id.product_details_name);
        productDes = (TextView) findViewById(R.id.product_details_des);
        productPrice = (TextView) findViewById(R.id.product_details_price);
        productImage = (ImageView) findViewById(R.id.product_details_image);
        numberButton = (ElegantNumberButton) findViewById(R.id.quantity);

        productID = getIntent().getStringExtra("pid");

        getProductDetails(productID);

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCartList();
            }
        });


    }

    private void addToCartList() {

        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("cartList");
        final HashMap<String, Object> cart = new HashMap<>();
        cart.put("pid",productID);
        cart.put("cartName",productName.getText().toString());
        cart.put("Des",productDes.getText().toString());
        cart.put("price",productPrice.getText().toString());
        cart.put("quantity",numberButton.getNumber());

        cartRef.child("User view").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(productID)
                .updateChildren(cart)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            cartRef.child("Admin view").child(Prevalent.currentOnlineUser.getPhone())
                                    .child("Products").child(productID)
                                    .updateChildren(cart)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                            }
                                        }
                                    });

                        }
                    }
                });




    }

    private void getProductDetails(String productID) {

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("products");
        productRef.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Products products = dataSnapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productDes.setText(products.getDescription());
                    productPrice.setText(products.getPrice());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
