package com.example.dogdeck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Models.Dog;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.MyViewHolder> {
        private Context context;
        private List<Dog> dogList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name, description, price;
            public ImageView thumbnail;
            public RelativeLayout viewBackground, viewForeground;

            public MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.name);
                description = view.findViewById(R.id.description);
                price = view.findViewById(R.id.price);
                thumbnail = view.findViewById(R.id.thumbnail);
                viewBackground = view.findViewById(R.id.view_background);
                viewForeground = view.findViewById(R.id.view_foreground);
            }
        }


        public CartListAdapter(Context context, List<Dog> cartList) {
            this.context = context;
            this.dogList = cartList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dog_rv_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final Dog item = dogList.get(position);
            holder.name.setText(item.getSelectedBreedStr());
            holder.description.setText(item.getSelectedBreedStr());
            holder.price.setText("₹" + item.getSelectedBreedStr());
        }

        @Override
        public int getItemCount() {
            return dogList.size();
        }

        public void removeItem(int position) {
            dogList.remove(position);
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
}
