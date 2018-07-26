package com.dsapps.wallpaperapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.dsapps.wallpaperapp.Adapters.CategoriesAdapter;
import com.dsapps.wallpaperapp.Models.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Shridhar on 22-Jun-18.
 */

public class HomeFragment extends Fragment
{

    ArrayList<Category> categoryList;
    RecyclerView recyclerView;
    CategoriesAdapter adapter;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=(RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        progressBar=(ProgressBar)view.findViewById(R.id.progress_bar_fragment_home);

        progressBar.setVisibility(View.VISIBLE);

        categoryList=new ArrayList<>();

        adapter=new CategoriesAdapter(getActivity(), categoryList);
        recyclerView.setAdapter(adapter);

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.GONE);

                if(dataSnapshot.exists())
                {
                    for(DataSnapshot ds: dataSnapshot.getChildren())
                    {
                        String name=ds.getKey();
                        String desc=ds.child("desc").getValue().toString();
                        String thumb=ds.child("thumbnail").getValue().toString();

                        Log.d(TAG, "onDataChange: name, desc, thumb: "+ name + ", "+ desc + ", "+ thumb);

                        Category c=new Category(name, desc, thumb);
                        categoryList.add(c);

                    }

                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return view;

    }
}
