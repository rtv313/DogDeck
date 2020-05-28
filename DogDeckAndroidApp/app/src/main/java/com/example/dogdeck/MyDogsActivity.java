package com.example.dogdeck;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import DataBase.DBManager;
import Models.Dog;

public class MyDogsActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private FloatingActionButton addDogFab;
    private ListView dogsListView;
    private String newDogPhotoPath;
    private LinkedList<Dog> dogsList;
    private DogsListAdapter dogsListAdapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dogs);

        dogsListView = findViewById(R.id.dogsList);
        addDogFab =  findViewById(R.id.add_dog);
        loadDogs();

        addDogFab.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    addDogFab.setBackgroundTintList(ContextCompat.getColorStateList(MyDogsActivity.this, R.color.redLight));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    addDogFab.setBackgroundTintList(ContextCompat.getColorStateList(MyDogsActivity.this, R.color.red));
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
        Button camera = openDialog.findViewById(R.id.cameraBtn);
        Button gallery = openDialog.findViewById(R.id.galleryBtn);

        gallery.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                loadImagefromGallery();
                openDialog.dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                callCameraPermissionAndValidation();
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
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("Fail create image",ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                Uri photoURI = FileProvider.getUriForFile(this, "com.example.dogdeck.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent,CAMERA_REQUEST);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked from gallery
            if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                newDogPhotoPath = copyFileFromGallery(selectedImage);
            }

            // When an Image is picked from camera
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && null != data) {
                // TODO Remove this comments
                //Bitmap imageCameraBitmap = BitmapFactory.decodeFile(newDogPhotoPath);
                //imgView.setImageBitmap(imageCameraBitmap);
            }

            // Start Dog Analysis Activity
            Intent intent = new Intent(MyDogsActivity.this, DogAnalysisActivity.class);
            Bundle b = new Bundle();
            b.putString("imageUri", newDogPhotoPath); //Your id
            intent.putExtras(b); //Put your id to your next Intent
            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void callCameraPermissionAndValidation(){
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

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        newDogPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String copyFileFromGallery( Uri uriImageGallery) throws IOException {
        // Use imageView for get the bitmap
        ImageView helperImgView  = new ImageView(this);
        helperImgView.setImageURI(uriImageGallery);
        String path = uriImageGallery.getPath();
        // Get Bitmap
        Bitmap bitmap = ((BitmapDrawable)helperImgView.getDrawable()).getBitmap();
        // Save Bitmap as JPG
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        //Write the bytes in file
        FileOutputStream fos = new FileOutputStream(image);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return image.getAbsolutePath();
    }

    private void loadDogs(){
        DBManager dbManager = new DBManager(this,this);
        dbManager.open();
        dogsList = dbManager.getDogs();
        dbManager.close();
        dogsListAdapter = new DogsListAdapter(getApplicationContext(),dogsList,this);
        dogsListView.setAdapter(dogsListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDogs();
    }
}
