package com.example.dogdeck;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import DataBase.DBManager;
import Models.Dog;
import Models.DogData;

public class UpdateDogActivity extends AppCompatActivity {

    ImageView dogPhoto;
    TextView breedOne,breedTwo,breedThree;
    TextView height,weight,origin,lifeSpan,temperament,health;
    int idDog;
    Dog dog;
    DogData dogData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_analysis);

        dogPhoto = findViewById(R.id.dogPhoto);
        breedOne = findViewById(R.id.breedOne);
        breedTwo = findViewById(R.id.breedTwo);
        breedThree = findViewById(R.id.breedThree);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        origin = findViewById(R.id.origin);
        lifeSpan = findViewById(R.id.lifeSpan);
        temperament = findViewById(R.id.temperament);
        health = findViewById(R.id.health);

        setDogData();
    }

    private void setDogData(){

        Bundle bundle = getIntent().getExtras();
        idDog = bundle.getInt("idDog");

        DBManager dbManager = new DBManager(this,this);
        dbManager.open();
        dog = dbManager.getDog(idDog);
        dogData = dbManager.getDogData(dog.getSelectedBreed());
        dbManager.close();

        Bitmap imageCameraBitmap = BitmapFactory.decodeFile(dog.getUriImage());
        dogPhoto.setImageBitmap(imageCameraBitmap);
        height.setText("Height:" + dogData.getHeight());
        weight.setText("Weight:" + dogData.getWeight());
        origin.setText("Origin:" + dogData.getOrigin());
        lifeSpan.setText("Life Span:" + dogData.getLifeSpan());
        temperament.setText(dogData.getTemperament());
        health.setText(dogData.getHealth());
    }
}
