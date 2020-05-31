package com.example.dogdeck;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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
        dogsBreedsListeners();
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

        breedOne.setText(dogDataBreedOne.getName() + ": " + dog.getPercentageBreedOne() + " %");
        breedTwo.setText(dogDataBreedTwo.getName() + ": " + dog.getPercentageBreedTwo() + " %");
        breedThree.setText(dogDataBreedThree.getName() + ": " + dog.getPercentageBreedThree() + " %");

        height.setText("Height: " + dogDataSelected.getHeight());
        weight.setText("Weight: " + dogDataSelected.getWeight());
        origin.setText("Origin: " + dogDataSelected.getOrigin());
        lifeSpan.setText("Life Span: " + dogDataSelected.getLifeSpan());
        temperament.setText(dogDataSelected.getTemperament());
        health.setText(getHealthIssues(dogDataSelected.getHealth()));


        if(dog.getSelectedBreed() == dog.getBreedOneId()){
            breedOne.setTextColor(getResources().getColor(R.color.blue_dockdeck));
            breedTwo.setTextColor(getResources().getColor(R.color.black));
            breedThree.setTextColor(getResources().getColor(R.color.black));
        }

        if(dog.getSelectedBreed() == dog.getBreedTwoId()){
            breedOne.setTextColor(getResources().getColor(R.color.black));
            breedTwo.setTextColor(getResources().getColor(R.color.blue_dockdeck));
            breedThree.setTextColor(getResources().getColor(R.color.black));
        }

        if(dog.getSelectedBreed() == dog.getBreedThreeId()){
            breedOne.setTextColor(getResources().getColor(R.color.black));
            breedTwo.setTextColor(getResources().getColor(R.color.black));
            breedThree.setTextColor(getResources().getColor(R.color.blue_dockdeck));
        }
    }

    private void dogsBreedsListeners(){
        breedOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dog.setSelectedBreed(dog.getBreedOneId());
                updateSelectedBreed(idDog,dog.getBreedOneId());
                setDogData();
            }
        });

        breedTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dog.setSelectedBreed(dog.getBreedTwoId());
                updateSelectedBreed(idDog,dog.getBreedTwoId());
                setDogData();
            }
        });

        breedThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dog.setSelectedBreed(dog.getBreedThreeId());
                updateSelectedBreed(idDog,dog.getBreedThreeId());
                setDogData();
            }
        });
    }

    private void updateSelectedBreed(int dogId,int selectedBreedId){
        DBManager dbManager = new DBManager(this,this);
        dbManager.open();
        dbManager.updateDog(dogId,selectedBreedId);
        dbManager.close();
    }

    private Spanned getHealthIssues(String health){
        String[] arrOfStr = health.split("\\|");
        String result = "";
        for(int i = 1; i < arrOfStr.length; i++){
            if(i!=arrOfStr.length)
               result += "<li>" + arrOfStr[i] + "</li>\n";
            else
                result += "<li>" + arrOfStr[i] + "</li>";
        }

        return Html.fromHtml(result);
    }
}
