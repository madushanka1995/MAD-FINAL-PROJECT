package com.food.fd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddProduct extends AppCompatActivity {

    private String categoryName, description,price,pname;
    private String saveCurrentDate,saveCurrentTime;
    private Button AddNewProduct;
    private EditText InputProductName,InputProductDes,InputProductPrice;
    private ImageView InputProduct_image;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String downloadImageUri;
    private String pRandomKey;
    private StorageReference ImageRef;
    private DatabaseReference productRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_add_product);

        categoryName = getIntent().getExtras().get("category").toString();
        Toast.makeText(this, categoryName , Toast.LENGTH_SHORT).show();

        ImageRef = FirebaseStorage.getInstance().getReference().child("Product_Image");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        AddNewProduct     = (Button) findViewById(R.id.btn_addProduct);
        InputProduct_image = (ImageView) findViewById(R.id.product_image);
        InputProductName  = (EditText)findViewById(R.id.product_name);
        InputProductDes   = (EditText) findViewById(R.id.product_description);
        InputProductPrice = (EditText) findViewById(R.id.product_price);
        loadingBar = new ProgressDialog(this);

        InputProduct_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        AddNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });

    }

    private void openGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){

            ImageUri = data.getData();
            InputProduct_image.setImageURI(ImageUri);


        }
    }

    private void ValidateProductData() {
        description = InputProductDes.getText().toString();
        price  = InputProductPrice.getText().toString();
        pname= InputProductName.getText().toString();

        if(ImageUri==null){
            Toast.makeText(this, "Product Image Mandatory" , Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Product Name Mandatory" , Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(price)){
            Toast.makeText(this, "Product Price Mandatory" , Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pname)){
            Toast.makeText(this, "Product Name Mandatory" , Toast.LENGTH_SHORT).show();
        }
        else{
            StoreProductDetails();
        }

    }

    private void StoreProductDetails()
    {

        loadingBar.setTitle("Add new Product");
        loadingBar.setMessage("Please wait....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        pRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filepath = ImageRef.child(ImageUri.getLastPathSegment() + pRandomKey + ".jpg");
        final UploadTask uploadTask = filepath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(AdminAddProduct.this, "Error : " + message , Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddProduct.this, "Image Upload Successfully"  , Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }

                        downloadImageUri = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadImageUri = filepath.getDownloadUrl().toString();

                            Toast.makeText(AdminAddProduct.this, "got the image uri successfully" , Toast.LENGTH_SHORT).show();
                            saveProductInfoDatabase();
                        }

                    }
                });
            }
        });

    }

    private void saveProductInfoDatabase()
    {
        HashMap<String, Object> Food = new HashMap<>();
        Food.put("pid",pRandomKey);
        Food.put("date",saveCurrentDate);
        Food.put("time",saveCurrentTime);
        Food.put("description",description);
        Food.put("image",downloadImageUri);
        Food.put("category",categoryName);
        Food.put("pname",pname);

        productRef.child(pRandomKey).updateChildren(Food)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(AdminAddProduct.this,AdminCategory.class);
                            startActivity(intent);

                            Toast.makeText(AdminAddProduct.this, "Product added successfully" , Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                        else{
                            Exception message = task.getException();
                            Toast.makeText(AdminAddProduct.this, "Error " + message , Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }

                    }
                });

    }


}
