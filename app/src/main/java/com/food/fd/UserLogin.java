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
import android.widget.TextView;
import android.widget.Toast;

import com.food.fd.Model.Users;
import com.food.fd.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserLogin extends AppCompatActivity {

    private EditText InputPhone,InputPassword;
    private Button login;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    private TextView adminLink,userLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        login = (Button) findViewById(R.id.btn_login);
        InputPhone = (EditText) findViewById(R.id.login_phone);
        InputPassword = (EditText) findViewById(R.id.login_password);
        adminLink = (TextView) findViewById(R.id.admin_link);
        userLink  = (TextView) findViewById(R.id.user_link);
        loadingBar = new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText("Login Admin");
                adminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Admins";
                LoginUser();
            }
        });
        userLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText("Login User");
                userLink.setVisibility(View.VISIBLE);
                parentDbName = "Users";
            }
        });


    }

    private void LoginUser() {

        String user_phone = InputPhone.getText().toString();
        String user_password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(user_phone)){
            Toast.makeText(this,"Please enter phone...",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(user_password)){
            Toast.makeText(this,"Please enter password...",Toast.LENGTH_LONG).show();
        }
        else{
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait....");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            AllowAccessToAccount(user_phone,user_password);
        }
    }

    private void AllowAccessToAccount(final String user_phone, final String user_password) {

        final DatabaseReference myref;
        myref = FirebaseDatabase.getInstance().getReference();

        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDbName).child(user_phone).exists())
                {

                    Users userData = dataSnapshot.child(parentDbName).child(user_phone).getValue(Users.class);
                    if(userData.getPhone().equals(user_phone)){

                        if(userData.getPassword().equals(user_password)){

                            if(parentDbName.equals("Admins")){

                                Toast.makeText(UserLogin.this, "Admin Login Successfully" , Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(UserLogin.this,AdminCategory.class);
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users")){
                                Toast.makeText(UserLogin.this, "Login Successfully" , Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(UserLogin.this,HomeReady.class);
                                Prevalent.currentOnlineUser = userData;
                                startActivity(intent);

                            }

                        }

                    }


                }
                else{
                    Toast.makeText(UserLogin.this, "Account not exit" , Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
