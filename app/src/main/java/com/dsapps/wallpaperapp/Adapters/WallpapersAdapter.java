package com.dsapps.wallpaperapp.Adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dsapps.wallpaperapp.Models.Wallpaper;
import com.dsapps.wallpaperapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Shridhar on 23-Jun-18.
 */

public class WallpapersAdapter extends RecyclerView.Adapter<WallpapersAdapter.WallpapersViewHolder>
{
    Context context;
    ArrayList<Wallpaper> wallpapersList;

    public WallpapersAdapter(Context context, ArrayList<Wallpaper> wallpapersList) {
        this.context = context;
        this.wallpapersList = wallpapersList;
    }

    @NonNull
    @Override
    public WallpapersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.wallpaper_recycler_view_content, parent, false);
        return new WallpapersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpapersViewHolder holder, int position) {
        holder.textView.setText(wallpapersList.get(position).title);
        Glide.with(context).load(wallpapersList.get(position).url).into(holder.imageView);

        if(wallpapersList.get(position).isFavourite)
        {
            holder.checkBox.setChecked(true);
        }
    }



    @Override
    public int getItemCount() {
        return wallpapersList.size();
    }

    public class WallpapersViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, CompoundButton.OnCheckedChangeListener
    {

        ImageView imageView;
        TextView textView;

        CheckBox checkBox;
        ImageView shareBtn;
        ImageView downloadBtn;

        public WallpapersViewHolder(View itemView) {
            super(itemView);

            imageView=(ImageView)itemView.findViewById(R.id.wallpaper_image_view);
            textView=(TextView)itemView.findViewById(R.id.wallpaper_name);

            checkBox=(CheckBox)itemView.findViewById(R.id.fav_checkbox);
            shareBtn=(ImageView)itemView.findViewById(R.id.share_btn);
            downloadBtn=(ImageView)itemView.findViewById(R.id.download_btn);

            checkBox.setOnCheckedChangeListener(this);
            shareBtn.setOnClickListener(this);
            downloadBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch(view.getId())
            {
                case R.id.share_btn:
                    shareWallpaper(wallpapersList.get(getAdapterPosition()));
                    break;

                case R.id.download_btn:
                    downloadWallpaper(wallpapersList.get(getAdapterPosition()));
                    Toast.makeText(context, "Downloading wallpaper...", Toast.LENGTH_SHORT).show();
                    break;
            }

        }

        private void shareWallpaper(Wallpaper w)
        {
            ((Activity)context).findViewById(R.id.progress_bar_wallpapers_activity).setVisibility(View.VISIBLE);

            Glide.with(context)
                    .asBitmap()
                    .load(w.url)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                            ((Activity)context).findViewById(R.id.progress_bar_wallpapers_activity).setVisibility(View.GONE);

                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("image/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));

                            context.startActivity(Intent.createChooser(shareIntent, "Share Wallpaper"));
                        }
                    });
        }

        private Uri getLocalBitmapUri(Bitmap bmp)
        {
            Uri bmpUri=null;

            try {

                File file=new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "Wallpaper_App_"+System.currentTimeMillis()+".png");
                FileOutputStream fileOutputStream=new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
                bmpUri=Uri.fromFile(file);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bmpUri;


        }

        private void downloadWallpaper(final Wallpaper wallpaper)
        {
            Glide.with(context)
                    .asBitmap()
                    .load(wallpaper.url)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                            ((Activity)context).findViewById(R.id.progress_bar_wallpapers_activity).setVisibility(View.GONE);

                            Intent downloadIntent = new Intent(Intent.ACTION_VIEW);

                            Uri uri=saveAndGetUri(resource, wallpaper.id);

                            if(uri!=null)
                            {
                                downloadIntent.setDataAndType(uri, "image/*");
                                context.startActivity(Intent.createChooser(downloadIntent, "Open Wallpaper"));
                            }
                        }
                    });
        }

        private Uri saveAndGetUri(Bitmap bitmap, String id)
        {
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                    Uri uri=Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);

                    context.startActivity(intent);
                }
                else
                {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                }

                return null;
            }

            File folder=new File(Environment.getExternalStorageDirectory().toString()+"/wallpaper_app");
            folder.mkdirs();

            File file=new File(folder, id+".jpg");

            try {
                FileOutputStream fos=new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

                return Uri.fromFile(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if(FirebaseAuth.getInstance().getCurrentUser()==null)
            {
                Toast.makeText(context, "Please login first.", Toast.LENGTH_SHORT).show();
                compoundButton.setChecked(false);
                return;
            }

            Wallpaper w=wallpapersList.get(getAdapterPosition());

            DatabaseReference favRef= FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Favourites").child(w.category)
                    .child(w.id);


            if(b)
            {
                favRef.setValue(w);
            }
            else
            {
                favRef.setValue(null);
            }

        }

    }

}
