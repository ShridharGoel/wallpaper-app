package com.dsapps.wallpaperapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

public class HomeActivity extends AppCompatActivity {

    NoSwipePager noSwipePager;
    BottomBarAdapter bottomBarAdapter;
    AHBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation=(AHBottomNavigation)findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem item1 =
                new AHBottomNavigationItem("Home", R.drawable.ic_home);

        AHBottomNavigationItem item2 =
                new AHBottomNavigationItem("Favourites",
                        R.drawable.ic_favourites);

        AHBottomNavigationItem item3 =
                new AHBottomNavigationItem("Settings",
                        R.drawable.ic_settings);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        bottomNavigation.setCurrentItem(0);

        noSwipePager=(NoSwipePager)findViewById(R.id.pager);

        noSwipePager.setPagingEnabled(false);
        bottomBarAdapter=new BottomBarAdapter(getSupportFragmentManager());

        HomeFragment homeFragment=new HomeFragment();
        FavouritesFragment favouritesFragment=new FavouritesFragment();
        SettingsFragment settingsFragment=new SettingsFragment();

        bottomBarAdapter.addFragments(homeFragment);
        bottomBarAdapter.addFragments(favouritesFragment);
        bottomBarAdapter.addFragments(settingsFragment);

        noSwipePager.setAdapter(bottomBarAdapter);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (!wasSelected)
                    noSwipePager.setCurrentItem(position);
                return true;
            }
        });
    }
}
