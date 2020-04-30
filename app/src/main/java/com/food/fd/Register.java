package com.food.fd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    Button register;
    EditText name,phone,password;
    ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = (Button) findViewById(R.id.btn_register);
        name = (EditText) findViewById(R.id.user_name);
        phone = (EditText) findViewById(R.id.user_phone);
        password = (EditText) findViewById(R.id.user_password);
        loadingBar = new ProgressDialog(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                craeteAccount();
            }
        });
    }

    private void craeteAccount() {
        String user_name = name.getText().toString();
        String user_phone = phone.getText().toString();
        String user_password = password.getText().toString();

        if(TextUtils.isEmpty(user_name)){
            Toast.makeText(this,"Please enter name... ",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(user_phone)){
            Toast.makeText(this,"Please enter phone...",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(user_password)){
            Toast.makeText(this,"Please enter password...",Toast.LENGTH_LONG).show();
        }
        else{
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait....");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhoneNumber(user_name,user_phone,user_password);
        }
    }

    private void ValidatePhoneNumber(final String user_name, final String user_phone, final String user_password)
    {
        final DatabaseReference myref;
        myref = FirebaseDatabase.getInstance().getReference();

        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Users").child(user_phone).exists()))
                {
                    HashMap <String, Object> userData = new HashMap<>();
                    userData.put("phone",user_phone);
                    userData.put("name",user_name);
                    userData.put("password",user_password);

                    myref.child("Users").child(user_phone).updateChildren(userData)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Register.this,"Congratulation New Account created...",Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(Register.this,UserLogin.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        loadingBar.dismiss();
                                        Toast.makeText(Register.this,"Network Error try again...",Toast.LENGTH_LONG).show();

                                    }

                                }
                            });


                }
                else{

                    Toast.makeText(Register.this,"Already has account...",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(Register.this,"Try agian...",Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(Register.this,MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
