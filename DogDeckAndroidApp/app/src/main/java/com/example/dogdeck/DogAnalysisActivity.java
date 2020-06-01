package com.example.dogdeck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import DataBase.DBManager;
import Models.DogData;

public class DogAnalysisActivity extends AppCompatActivity {

    final Handler handler = new Handler();
    ProgressDialog progress;
    ImageView dogPhoto;
    TextView breedOne,breedTwo,breedThree;
    TextView height,weight,origin,lifeSpan,temperament;
    ImageButton share;
    LinearLayout dogHealthIssues;
    String uri = "";
    String strBreedOne,strBreedTwo,strBreedThree;
    float percentageBreedOne,percentageBreedTwo,percentageBreedThree;
    int selectedBreed;
    int idDogCreated;
    ArrayList<String> labels;
    HashMap<String,Float> resultsMap = new HashMap<String,Float>();
    HashMap<String,Integer> mapBreedToIndex = new HashMap<String, Integer>();

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
        dogHealthIssues = findViewById(R.id.dogHealthIssues);
        share = findViewById(R.id.shareButton);
        breedOne.setTextColor(getResources().getColor(R.color.blue_dockdeck));

        progress = new ProgressDialog(this);
        progress.setTitle("Analyzing Image");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        setDogPhoto();
        dogsBreedsListeners();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                analyzeImage();
            }
        }, 1500);

    }


    private void setDogPhoto(){
        Bundle bundle = getIntent().getExtras();
        uri = bundle.getString("imageUri");
        Bitmap imageCameraBitmap = BitmapFactory.decodeFile(uri);
        dogPhoto.setImageBitmap(imageCameraBitmap);
    }

    private ArrayList<String> loadLabels() throws IOException {
        ArrayList<String> labelList = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.getAssets().open("labels.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    private HashMap<String, Float> sortByValue(HashMap<String, Float> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Float> > list = new LinkedList<Map.Entry<String, Float> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Float> >() {
            public int compare(Map.Entry<String, Float> o1,Map.Entry<String, Float> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Collections.reverse(list);
        // put data from sorted list to hashmap
        HashMap<String, Float> temp = new LinkedHashMap<String, Float>();
        for (Map.Entry<String, Float> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private void saveResults(){
        // Save results in database
        DBManager dbManager = new DBManager(this,this);
        dbManager.open();
        int fkBreedOne = mapBreedToIndex.get(strBreedOne);
        int fkBreedTwo = mapBreedToIndex.get(strBreedTwo);
        int fkBreedThree = mapBreedToIndex.get(strBreedThree);
        selectedBreed = fkBreedOne;
        idDogCreated = dbManager.addDog(fkBreedOne,fkBreedTwo,fkBreedThree,percentageBreedOne,
                percentageBreedTwo,percentageBreedThree,selectedBreed,uri);
        dbManager.close();
    }

    private void analyzeImage(){

        try {
            labels = loadLabels();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(new NormalizeOp(0,255))
                .build();

        Bitmap bitmap = ((BitmapDrawable)dogPhoto.getDrawable()).getBitmap();
        TensorImage tImage = new TensorImage(DataType.FLOAT32);
        tImage.load(bitmap);
        tImage = imageProcessor.process(tImage);


        float[][] probabilityBuffer = new float[1][120];
        Interpreter tflite = null;

        try{
            MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(this, "tf_model.tflite");
            tflite = new Interpreter(tfliteModel);
        } catch (IOException e){
            Log.e("tfliteSupport", "Error reading model", e);
        }

        // Running inference

        if(null != tflite) {
            tflite.run(tImage.getBuffer(),probabilityBuffer);
        }

        // Get Results
        float [] inferredValues = probabilityBuffer[0];

        for(int i = 0 ; i < labels.size(); i++){
            resultsMap.put(labels.get(i),inferredValues[i]);
            mapBreedToIndex.put(labels.get(i),i+1);
        }

        resultsMap = sortByValue(resultsMap);
        int i = 0;
        for(String key : resultsMap.keySet()){

            if (i == 0) {
                strBreedOne = key;
                percentageBreedOne = resultsMap.get(strBreedOne) * 100;
                breedOne.setText(key +" "+ String.valueOf(percentageBreedOne) + " %");
            }

            if (i == 1) {
                strBreedTwo = key;
                percentageBreedTwo = resultsMap.get(strBreedTwo) * 100;
                breedTwo.setText(key + " " + String.valueOf(percentageBreedTwo) + " %");
            }

            if (i == 2) {
                strBreedThree = key;
                percentageBreedThree = resultsMap.get(strBreedThree) * 100;
                breedThree.setText(key + " " + String.valueOf(percentageBreedThree) + " %");
            }

            if(i==3)
                break;
            i++;
        }
        saveResults();
        setDogData();
        progress.dismiss();
    }

    private void setDogData(){
        DBManager dbManager = new DBManager(this,this);
        dbManager.open();
        DogData dogData = dbManager.getDogData(selectedBreed);
        dbManager.close();

        height.setText("Height: " + dogData.getHeight());
        weight.setText("Weight: " + dogData.getWeight());
        origin.setText("Origin: " + dogData.getOrigin());
        lifeSpan.setText("Life Span: " + dogData.getLifeSpan());
        temperament.setText(dogData.getTemperament());
        getHealthIssues(dogData.getHealth(),dogHealthIssues);
    }

    private void dogsBreedsListeners(){
        breedOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedBreed =  mapBreedToIndex.get(strBreedOne);
                setDogData();
                breedOne.setTextColor(getResources().getColor(R.color.blue_dockdeck));
                breedTwo.setTextColor(getResources().getColor(R.color.black));
                breedThree.setTextColor(getResources().getColor(R.color.black));
                updateSelectedBreed(idDogCreated,selectedBreed);
            }
        });

        breedTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedBreed =  mapBreedToIndex.get(strBreedTwo);
                setDogData();
                breedOne.setTextColor(getResources().getColor(R.color.black));
                breedTwo.setTextColor(getResources().getColor(R.color.blue_dockdeck));
                breedThree.setTextColor(getResources().getColor(R.color.black));
                updateSelectedBreed(idDogCreated,selectedBreed);
            }
        });

        breedThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedBreed =  mapBreedToIndex.get(strBreedThree);
                setDogData();
                breedOne.setTextColor(getResources().getColor(R.color.black));
                breedTwo.setTextColor(getResources().getColor(R.color.black));
                breedThree.setTextColor(getResources().getColor(R.color.blue_dockdeck));
                updateSelectedBreed(idDogCreated,selectedBreed);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ShareDogData.shareDogInfo(DogAnalysisActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateSelectedBreed(int dogId,int selectedBreedId){
        DBManager dbManager = new DBManager(this,this);
        dbManager.open();
        dbManager.updateDog(dogId,selectedBreedId);
        dbManager.close();
    }

    private void getHealthIssues(String health, LinearLayout linearLayout){
        linearLayout.removeAllViews();
        String[] arrOfStr = health.split("\\|");
        for(int i = 1; i < arrOfStr.length; i++){
            LayoutInflater lInflater = LayoutInflater.from(this);
            View view = lInflater.inflate(R.layout.dog_health_issue,null);
            TextView  issue = view.findViewById(R.id.dogIssue);
            issue.setText(arrOfStr[i]);
            linearLayout.addView(issue);
        }
    }
}
