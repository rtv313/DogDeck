package com.example.dogdeck;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class DogAnalysis extends AppCompatActivity {

    ImageView dogPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_analysis);

        dogPhoto = findViewById(R.id.dogPhoto);
        Bundle bundle = getIntent().getExtras();
        String uri = bundle.getString("imageUri");
        Bitmap imageCameraBitmap = BitmapFactory.decodeFile(uri);
        dogPhoto.setImageBitmap(imageCameraBitmap);
    }
}
