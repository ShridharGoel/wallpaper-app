package com.dsapps.wallpaperapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.dsapps.wallpaperapp.Adapters.WallpapersAdapter;
import com.dsapps.wallpaperapp.Models.Wallpaper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WallpapersActivity extends AppCompatActivity {

    ArrayList<Wallpaper> wallpapersList;
    WallpapersAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Wallpaper> favList;

    ProgressBar progressBar;

    DatabaseReference dbFav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpapers);

        progressBar=(ProgressBar)findViewById(R.id.progress_bar_wallpapers_activity);
        progressBar.setVisibility(View.VISIBLE);

        favList=new ArrayList<>();

        final String category_name=getIntent().getStringExtra("name");

        wallpapersList=new ArrayList<>();
        adapter=new WallpapersAdapter(this, wallpapersList);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view_wallpapers_activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            dbFav=FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("Favourites").child(category_name);
            fetchFavWallpapers(category_name);
        }
        else
        {
            fetchAllWallpapers(category_name);
        }

    }

    private void fetchFavWallpapers(final String category_name)
    {
        dbFav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.GONE);

                if(dataSnapshot.exists())
                {
                    for(DataSnapshot ds:dataSnapshot.getChildren()) {
                        String id = ds.getKey();
                        String title = ds.child("title").getValue().toString();
                        String desc = ds.child("desc").getValue().toString();
                        String url = ds.child("url").getValue().toString();

                        Log.d("onDataChange:", "title:" + title + ", desc:" + desc + ", url:" + url);

                        Wallpaper w = new Wallpaper(id, title, desc, url, category_name);
                        favList.add(w);
                    }

                }
                fetchAllWallpapers(category_name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchAllWallpapers(final String category_name)
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("images/"+category_name);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.GONE);

                if(dataSnapshot.exists())
                {
                    for(DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        String id=ds.getKey();
                        String title=ds.child("title").getValue().toString();
                        String desc=ds.child("desc").getValue().toString();
                        String url=ds.child("url").getValue().toString();

                        Log.d("onDataChange:", "title:"+title+", desc:"+desc+", url:"+url);

                        Wallpaper w=new Wallpaper(id, title, desc, url, category_name);

                        if(isFavourite(w))
                            w.isFavourite=true;

                        wallpapersList.add(w);
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isFavourite(Wallpaper w)
    {
        for(Wallpaper f:favList)
        {
            if(f.id.equals(w.id))
            {
                return true;
            }
        }
        return false;
    }
}
