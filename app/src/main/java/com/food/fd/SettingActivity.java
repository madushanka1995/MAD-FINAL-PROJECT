package com.food.fd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.food.fd.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {

    private ImageView profile;
    private EditText nameEdit, phoneEdit,addressEdit;
    private TextView changeProfile, closeTextButton,saveTextButton;

    private Uri imageUri;
    private String myUri = "";
    private StorageReference storageProfile;
    private String checker = "";
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        storageProfile = FirebaseStorage.getInstance().getReference().child("profile pictures");


        profile = (ImageView) findViewById(R.id.profile_image);
        nameEdit = (EditText) findViewById(R.id.setting_phone_number);
        phoneEdit = (EditText) findViewById(R.id.setting_phone_name);
        addressEdit = (EditText) findViewById(R.id.setting_phone_Address);

         saveTextButton = (TextView)findViewById(R.id.update_setting);
        closeTextButton  = (TextView) findViewById(R.id.close_setting);
        changeProfile = (TextView) findViewById(R.id.change_profile);

        userDisplayInfo(profile,nameEdit,phoneEdit,addressEdit);
        closeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked")){
                    UserInfoSave();
                }
                else{
                    updateUserInfor();
                }
            }
        });

}

    private void updateUserInfor()
    {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", nameEdit.getText().toString());
        userMap.put("address",addressEdit.getText().toString());
        userMap.put("phone",phoneEdit.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingActivity.this,Home.class));
        Toast.makeText(this, "update successful" , Toast.LENGTH_SHORT).show();
        finish();
    }

    private void UserInfoSave() {

        if(TextUtils.isEmpty(nameEdit.getText().toString()))
        {
            Toast.makeText(this, "Name Mandatory" , Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneEdit.getText().toString()))
        {
            Toast.makeText(this, "Phone Mandatory" , Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressEdit.getText().toString())){
            Toast.makeText(this, "Address Mandatory" , Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Update Account");
            loadingBar.setMessage("Please wait....");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
        }

    }

    private void userDisplayInfo(final ImageView profile, final EditText nameEdit, final EditText phoneEdit, final EditText addressEdit)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("image").exists() )
                    {
                      String image = dataSnapshot.child("image").getValue().toString();
                      String name = dataSnapshot.child("name").getValue().toString();
                      String phone = dataSnapshot.child("password").getValue().toString();
                      String add = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profile);
                        nameEdit.setText(name);
                        phoneEdit.setText(phone);
                        addressEdit.setText(add);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    }
