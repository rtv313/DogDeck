package com.example.dogdeck;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.zip.Inflater;

import Models.Dog;

public class DogsListAdapter extends BaseAdapter {

    LinkedList<Dog> dogsList;
    LayoutInflater lInflater;
    Context context;
    MyDogsActivity activity;

    public DogsListAdapter(Context context,LinkedList<Dog> dogsList,MyDogsActivity activity){
        lInflater =LayoutInflater.from(context);
        this.dogsList = dogsList;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public int getCount(){
        return dogsList.size();
    }

    @Override
    public Dog getItem(int index){
        return dogsList.get(index);
    }

    @Override
    public long getItemId(int index){
        return 1;
    }

    @Override
    public View getView(int item, View view, ViewGroup parent){
        view  = lInflater.inflate(R.layout.dog_item,null);
        ImageView imgDog = view.findViewById(R.id.imgView);
        TextView dogBreed = view.findViewById(R.id.dogBreed);

        Dog dog = dogsList.get(item);
        Bitmap imageCameraBitmap = BitmapFactory.decodeFile(dog.getUriImage());
        imgDog.setImageBitmap(imageCameraBitmap);
        dogBreed.setText(dog.getSelectedBreedStr());
        return view;
    }
}
