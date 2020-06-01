package com.example.dogdeck;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class MyDogsActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{

    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private FloatingActionButton addDogFab;
    private String newDogPhotoPath;
    private TextView addDogText;
    private RecyclerView recyclerView;
    private LinkedList<Dog> dogsRVList;
    private DogRVListAdapter mAdapter;
    private LinearLayout coordinatorLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dogs);
        addDogFab =  findViewById(R.id.add_dog);
        addDogText = findViewById(R.id.addDogs);
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

        recyclerView = findViewById(R.id.recycler_view);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        dogsRVLoadData();
    }

    private void showPopUpMediaOptions(){
        final Dialog openDialog = new Dialog(MyDogsActivity.this);
        openDialog.setContentView(R.layout.select_camera_or_gallery_dialog);
        TextView camera = openDialog.findViewById(R.id.cameraBtn);
        TextView gallery = openDialog.findViewById(R.id.galleryBtn);

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

            if(data == null){
                // No photo selected
                return;
            }

            // When an Image is picked from gallery
            if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                newDogPhotoPath = copyFileFromGallery(selectedImage);
            }

            // When an Image is picked from camera
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && null != data) {
                // TODO Remove this comments
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

    @Override
    protected void onResume() {
        super.onResume();
        dogsRVLoadData();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof DogRVListAdapter.MyViewHolder) {
            // backup of removed item for undo purpose
            final Dog deletedDog= dogsRVList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            // remove the item from recycler view
            deleteDog(deletedDog,deletedIndex);
            mAdapter.removeItem(viewHolder.getAdapterPosition());
        }
    }

    private void dogsRVLoadData(){
        DBManager dbManager = new DBManager(this,this);
        dbManager.open();
        dogsRVList = dbManager.getDogs();
        dbManager.close();
        mAdapter = new DogRVListAdapter(this,this, dogsRVList);
        recyclerView.setAdapter(mAdapter);
        removeAddDogsMessage();
    }

    private void deleteDog(Dog deletedDog,int position){
        // Remove from database
        DBManager dbManager = new DBManager(this,this);
        dbManager.open();
        dbManager.deleteDog(deletedDog.getId());
        dbManager.close();
        // Remove Image File
        String filePath = deletedDog.getUriImage();
        File file = new File(filePath);
        file.delete();
    }

    protected void removeAddDogsMessage(){
        if(dogsRVList.size() == 0){
             addDogText.setVisibility(View.VISIBLE);
        }else{
            addDogText.setVisibility(View.INVISIBLE);
        }
    }
}
