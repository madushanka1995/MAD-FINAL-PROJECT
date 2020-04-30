package com.food.fd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CustomerDetailsForm extends AppCompatActivity {

    Button register;
    EditText name,phone,address,email;
    ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details_form);

        register = (Button) findViewById(R.id.btn_register);
        name = (EditText) findViewById(R.id.user_name);
        phone = (EditText) findViewById(R.id.user_phone);
        address = (EditText) findViewById(R.id.user_address);
        email = (EditText) findViewById(R.id.user_email);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustomer();
            }
        });
    }

    private void addCustomer()
    {
        String customer_name = name.getText().toString();
        String customer_phone = phone.getText().toString();
        String customer_address = address.getText().toString();
        String customer_email = phone.getText().toString();

        if(TextUtils.isEmpty(customer_name)){
            Toast.makeText(this,"Please enter name... ",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(customer_phone)){
            Toast.makeText(this,"Please enter phone...",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(customer_address)){
            Toast.makeText(this,"Please enter address...",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(customer_email)){
            Toast.makeText(this,"Please enter email...",Toast.LENGTH_LONG).show();
        }
        else{
            loadingBar.setTitle("Create Profile");
            loadingBar.setMessage("Please wait....");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhoneNumber(customer_name,customer_phone,customer_address,customer_email);
        }
    }

    private void ValidatePhoneNumber(final String customer_name, final String customer_phone, final String customer_address, final String customer_email)
    {

        final DatabaseReference myref;
        myref = FirebaseDatabase.getInstance().getReference();

        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Customer").child(customer_phone).exists()))
                {
                    HashMap<String, Object> CustomerData = new HashMap<>();
                    CustomerData.put("phone",customer_phone);
                    CustomerData.put("name",customer_name);
                    CustomerData.put("address",customer_address);
                    CustomerData.put("email",customer_email);

                    myref.child("Customer").child(customer_phone).updateChildren(CustomerData)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(CustomerDetailsForm.this,"Your data add successfully",Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(CustomerDetailsForm.this,Home.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        loadingBar.dismiss();
                                        Toast.makeText(CustomerDetailsForm.this,"Network Error try again...",Toast.LENGTH_LONG).show();

                                    }

                                }
                            });
            }


        }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            };

            });
    }
}
