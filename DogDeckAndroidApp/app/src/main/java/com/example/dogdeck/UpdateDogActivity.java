package com.example.dogdeck;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
    DogData dogDataSelected,dogDataBreedOne,dogDataBreedTwo,dogDataBreedThree;

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
        dogDataSelected = dbManager.getDogData(dog.getSelectedBreed());
        dogDataBreedOne = dbManager.getDogData(dog.getBreedOneId());
        dogDataBreedTwo = dbManager.getDogData(dog.getBreedTwoId());
        dogDataBreedThree = dbManager.getDogData(dog.getBreedThreeId());
        dbManager.close();

        Bitmap imageCameraBitmap = BitmapFactory.decodeFile(dog.getUriImage());
        dogPhoto.setImageBitmap(imageCameraBitmap);

        breedOne.setText(dogDataBreedOne.getName() + " : " + dog.getPercentageBreedOne());
        breedTwo.setText(dogDataBreedTwo.getName() + " : " + dog.getPercentageBreedTwo());
        breedThree.setText(dogDataBreedThree.getName() + " : " + dog.getPercentageBreedThree());

        height.setText("Height:" + dogDataSelected.getHeight());
        weight.setText("Weight:" + dogDataSelected.getWeight());
        origin.setText("Origin:" + dogDataSelected.getOrigin());
        lifeSpan.setText("Life Span:" + dogDataSelected.getLifeSpan());
        temperament.setText(dogDataSelected.getTemperament());
        health.setText(dogDataSelected.getHealth());
    }
}
