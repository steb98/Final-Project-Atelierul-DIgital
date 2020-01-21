package com.buggy.tripbook;

import android.content.Context;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<TripItemModel> mTrip;
    Context context;
    static int pos;

    public RecyclerViewAdapter(ArrayList<TripItemModel> mTrip, Context context) {
        this.mTrip = mTrip;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);


        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    public Bitmap loadImageFromStorage(String path) {
        Bitmap b=null;
        try {
            File f = new File(path);
            b = BitmapFactory.decodeStream(new FileInputStream(f));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return b;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.title.setText(mTrip.get(position).getTripTitle());
        holder.destination.setText(mTrip.get(position).getTripDestination());
        //replace me
        /*
        Bitmap photo = loadImageFromStorage(mTrip.get(position).getTripImage());
        if(photo==null) {

            holder.image.setImageResource(R.drawable.desert_image);
        }else{
            holder.image.setImageBitmap(photo);
        }

         */
        Glide.with(holder.image.getContext()).load(mTrip.get(position).getTripImage()).placeholder(R.drawable.desert_image).into(holder.image);
        //holder.image.setImageURI(mTrip.get(position).getTripImage());
        holder.price.setText(mTrip.get(position).getTripPrice()+" $");
        holder.rating.setRating(mTrip.get(position).getTripRating());
        holder.rating.setIsIndicator(true);
        if(mTrip.get(position).getTripFav()==1){
            holder.fav.setImageResource(R.drawable.ic_star_gold_24dp);
        }
        else{
            holder.fav.setImageResource(R.drawable.ic_star_black_24dp);
        }

    }

    @Override
    public int getItemCount() {
        return mTrip.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView price;
        ImageView image;
        TextView title;
        TextView destination;

        RatingBar rating;

        ImageView fav;
       // ConstraintLayout parentLayout;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.tripPrice);
            image = itemView.findViewById(R.id.tripImage);
            title = itemView.findViewById(R.id.tripTitle);
            destination = itemView.findViewById(R.id.tripDestination);
            rating = itemView.findViewById(R.id.tripRating);
            fav = itemView.findViewById(R.id.tripFav);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    EditTripFragment next = new EditTripFragment();
                    AppCompatActivity activity = (AppCompatActivity) context;
                    activity.getSupportFragmentManager().beginTransaction().hide(activity.getSupportFragmentManager().findFragmentById(R.id.flMain))
                    .add(R.id.flMain,next)
                    .addToBackStack(null)
                    .commit();
                    pos = getAdapterPosition();
                    //((EditText) (((AppCompatActivity) context).findViewById(R.id.editTripName))).setText("test");


                    return true;
                }
            });
            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos = getAdapterPosition();
                    if(mTrip.get(pos).getTripFav()==1){
                        mTrip.get(pos).setTripFav(0);
                        ((ImageView)view).setImageResource(R.drawable.ic_star_black_24dp);
                        Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mTrip.get(pos).setTripFav(1);
                        ((ImageView)view).setImageResource(R.drawable.ic_star_gold_24dp);
                        Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }

}
