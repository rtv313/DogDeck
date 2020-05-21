package com.example.dogdeck;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MyDogsActivity extends AppCompatActivity {

    FloatingActionButton addDogFab;
    ImageView imgView;
    RelativeLayout relativeLayout;
    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dogs);
        relativeLayout = (RelativeLayout)findViewById(R.id.relative_layout);
        addDogFab = (FloatingActionButton) findViewById(R.id.add_dog);
        imgView = (ImageView) findViewById(R.id.imageView1);

        addDogFab.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    addDogFab.setBackgroundTintList(ContextCompat.getColorStateList(MyDogsActivity.this, R.color.redLight));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    addDogFab.setBackgroundTintList(ContextCompat.getColorStateList(MyDogsActivity.this, R.color.red));
                    //loadImagefromGallery();
                    //validateCameraPermission();
                    showPopUpMediaOptions();
                }
                return true;
            }
        });
    }

    private void showPopUpMediaOptions(){
        final Dialog openDialog = new Dialog(MyDogsActivity.this);
        openDialog.setContentView(R.layout.select_camera_or_gallery_dialog);
        openDialog.setTitle("Custom Dialog Box");
        Button camera = (Button) openDialog.findViewById(R.id.cameraBtn);
        Button gallery = (Button)openDialog.findViewById(R.id.galleryBtn);

        gallery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                openDialog.dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                openDialog.dismiss();
            }
        });

        openDialog.show();
    }

    private void loadImagefromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    private void takePicture(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked from gallery
            if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = Uri.EMPTY;
                selectedImage = data.getData();
                imgView.setImageURI(selectedImage);
                return;
            }

            // When an Image is picked from camera
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && null != data) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imgView.setImageBitmap(photo);
                return;
            }

            Toast.makeText(this, "No escogiste una imagen", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            //selectedImage = Uri.EMPTY;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void validateCameraPermission(){
        if(checkCallingOrSelfPermission(Manifest.permission.CAMERA) ==  PackageManager.PERMISSION_GRANTED){
            takePicture();
        }else{
            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                String messageCamera = "Camera permission is needed";
                Toast.makeText(MyDogsActivity.this,messageCamera,Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        if(requestCode == CAMERA_REQUEST){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                takePicture();
            }else{
                String messagePermissions = "Permission was not granted";
                Toast.makeText(MyDogsActivity.this,messagePermissions,Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }
}
