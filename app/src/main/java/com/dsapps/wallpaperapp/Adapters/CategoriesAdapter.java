package com.dsapps.wallpaperapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dsapps.wallpaperapp.Models.Category;
import com.dsapps.wallpaperapp.R;
import com.dsapps.wallpaperapp.WallpapersActivity;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Shridhar on 22-Jun-18.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>
{

    Context context;
    ArrayList<Category> categoryList;

    public CategoriesAdapter(Context context, ArrayList<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_view_content, parent, false);
        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, final int position) {

        holder.categoryName.setText(categoryList.get(position).name);
        Glide.with(context).load(categoryList.get(position).thumb).into(holder.thumbnailImageView);

        Log.d(TAG, "onBindViewHolder: "+ holder.categoryName.getText());

        holder.thumbnailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, WallpapersActivity.class);
                intent.putExtra("name",categoryList.get(position).name);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoriesViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbnailImageView;
        TextView categoryName;

        public CategoriesViewHolder(View itemView) {
            super(itemView);

            thumbnailImageView=(ImageView)itemView.findViewById(R.id.thumbnail_image_view);
            categoryName=(TextView)itemView.findViewById(R.id.category_name);

            Log.d(TAG, "CategoriesViewHolder: "+categoryName);
        }
    }

}
