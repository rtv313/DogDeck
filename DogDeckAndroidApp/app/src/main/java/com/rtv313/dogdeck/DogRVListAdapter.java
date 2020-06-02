package com.rtv313.dogdeck;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;


import DataBase.DBManager;
import Models.Dog;
import Models.DogData;

public class DogRVListAdapter extends RecyclerView.Adapter<DogRVListAdapter.MyViewHolder> {
        private Context context;
        private Activity activity;
        private LinkedList<Dog> dogList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public TextView origin;
            public TextView percentages;
            public ImageView thumbnail;
            public RelativeLayout viewBackground;
            public LinearLayout viewForeground;

            public MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.name);
                origin = view.findViewById(R.id.origin);
                percentages = view.findViewById(R.id.percentages);
                thumbnail = view.findViewById(R.id.thumbnail);
                viewBackground = view.findViewById(R.id.view_background);
                viewForeground = view.findViewById(R.id.view_foreground);
            }
        }

        public DogRVListAdapter(Context context,Activity activity,LinkedList<Dog> dogList) {
            this.context = context;
            this.dogList = dogList;
            this.activity = activity;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dog_rv_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final Dog dog = dogList.get(position);
            Bitmap imageCameraBitmap = BitmapFactory.decodeFile(dog.getUriImage());
            holder.thumbnail.setImageBitmap(imageCameraBitmap);
            holder.name.setText(dog.getSelectedBreedStr());
            DBManager dbManager = new DBManager(context,(Activity)context);
            dbManager.open();
            DogData data = dbManager.getDogData(dog.getSelectedBreed());
            dbManager.close();
            holder.origin.setText(data.getOrigin());
            holder.percentages.setText(dog.getPercentageBreedOne() + "/" +
                                       dog.getPercentageBreedTwo() + "/" +
                                       dog.getPercentageBreedThree());
            createListeners(holder,dog);
        }

        @Override
        public int getItemCount() {
            return dogList.size();
        }

        public void removeItem(int position) {
            dogList.remove(position);
            ((MyDogsActivity)activity).removeAddDogsMessage();
            // notify the item removed by position
            // to perform recycler view delete animations
            // NOTE: don't call notifyDataSetChanged()
            notifyItemRemoved(position);
        }

        public void restoreItem(Dog item, int position) {
            dogList.add(position, item);
            // notify item added by position
            notifyItemInserted(position);
        }

        private void createListeners(MyViewHolder holder, final Dog dog){
            holder.viewForeground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UpdateDogActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("idDog", dog.getId()); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                    context.startActivity(intent);
                }
            });
        }
}
