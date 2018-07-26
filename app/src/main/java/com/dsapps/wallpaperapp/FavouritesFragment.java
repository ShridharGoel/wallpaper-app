package com.dsapps.wallpaperapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dsapps.wallpaperapp.Adapters.WallpapersAdapter;
import com.dsapps.wallpaperapp.Models.Wallpaper;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.data.AbstractDataBuffer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Shridhar on 22-Jun-18.
 */

public class FavouritesFragment extends Fragment {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    WallpapersAdapter adapter;
    ArrayList<Wallpaper> favWallpapers;
    DatabaseReference dbFavs;
    CoordinatorLayout favouritesCoordinator;
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        favWallpapers = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_favourites_fragment);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_favourites_fragment);
        adapter = new WallpapersAdapter(getContext(), favWallpapers);

        textView = (TextView) view.findViewById(R.id.text_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        favouritesCoordinator = (CoordinatorLayout) view.findViewById(R.id.favourites_coordinator);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            textView.setVisibility(View.VISIBLE);
        } else {

            textView.setVisibility(View.GONE);

            dbFavs = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("Favourites");

            progressBar.setVisibility(View.VISIBLE);

            dbFavs.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    progressBar.setVisibility(View.GONE);

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot category : dataSnapshot.getChildren()) {
                            for (DataSnapshot wallpaper : category.getChildren()) {
                                String id = wallpaper.getKey();
                                String title = wallpaper.child("title").getValue().toString();
                                String desc = wallpaper.child("desc").getValue().toString();
                                String url = wallpaper.child("url").getValue().toString();

                                Log.d("onDataChange:", "title:" + title + ", desc:" + desc + ", url:" + url);

                                Wallpaper w = new Wallpaper(id, title, desc, url, category.getKey());

                                w.isFavourite = true;

                                favWallpapers.add(w);
                            }

                        }

                        adapter.notifyDataSetChanged();

                    }

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        return view;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here

            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }

    }
}
