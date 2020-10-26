package com.pinniboina.storeadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView mImage;
    private EditText mId, mName, mDescription, mCost;
    private Button mSave;

    private Uri mImageUri;

    private DatabaseReference mDataRef;
    private StorageReference mStorageRef;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImage = findViewById(R.id.product_image);
        mId = findViewById(R.id.product_id);
        mName = findViewById(R.id.product_name);
        mDescription = findViewById(R.id.product_desc);
        mCost = findViewById(R.id.product_cost);
        mSave = findViewById(R.id.btn_save);
        mDataRef = FirebaseDatabase.getInstance().getReference("Products");
        mStorageRef = FirebaseStorage.getInstance().getReference("Products");

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddProduct();
            }
        });
    }

    private String getFileExtention(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void AddProduct() {
        final String id = mId.getText().toString().trim();
        final String name = mName.getText().toString().trim();
        final String description = mDescription.getText().toString().trim();
        final String cost = mCost.getText().toString().trim();

        if(mImageUri == null || id.isEmpty() || name.isEmpty() || description.isEmpty() || cost.isEmpty()){
            Toast.makeText(MainActivity.this, "Please fill all details", Toast.LENGTH_SHORT ).show();
        }else{
            StorageReference fileRef = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtention(mImageUri));
            fileRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Upload upload = new Upload(id,name,description,cost, Objects.requireNonNull(taskSnapshot.getUploadSessionUri()).toString());
                    String uploadId = mDataRef.push().getKey();
                    assert uploadId != null;
                    mDataRef.child(uploadId).setValue(upload);
                    Toast.makeText(MainActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    dialog = ProgressDialog.show(MainActivity.this, "Uploading..", "Please wait...",true);
                }
            });
        }
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();

            mImage.setImageURI(mImageUri);
        }
    }
}