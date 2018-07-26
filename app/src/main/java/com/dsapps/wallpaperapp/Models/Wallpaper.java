package com.dsapps.wallpaperapp.Models;

import com.google.firebase.database.Exclude;

/**
 * Created by Shridhar on 23-Jun-18.
 */

public class Wallpaper
{
    @Exclude
    public String id;

    public String title, desc, url;

    @Exclude
    public String category;

    @Exclude
    public boolean isFavourite=false;

    public Wallpaper(String id, String title, String desc, String url, String category) {
        this.id=id;
        this.title = title;
        this.desc = desc;
        this.url = url;
        this.category=category;
    }

}
