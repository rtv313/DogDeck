package com.example.dogdeck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import static android.view.MotionEvent.*;

public class MyDogsActivity extends AppCompatActivity {

    FloatingActionButton addDogFab;
    private static int RESULT_LOAD_IMG = 1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dogs);
        addDogFab = (FloatingActionButton) findViewById(R.id.add_dog);

        addDogFab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    addDogFab.setBackgroundTintList(ContextCompat.getColorStateList(MyDogsActivity.this, R.color.redLight));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Toast.makeText(MyDogsActivity.this,"Hiciste Clic",Toast.LENGTH_SHORT).show();
                    addDogFab.setBackgroundTintList(ContextCompat.getColorStateList(MyDogsActivity.this, R.color.red));
                    loadImagefromGallery();

                }
                return true;
            }
        });
    }

    private void loadImagefromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                //selectedImage = data.getData();
                //imgView.setImageURI(selectedImage);
            } else {
                Toast.makeText(this, "No escogiste una imagen", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            //selectedImage = Uri.EMPTY;
        }
    }

}
