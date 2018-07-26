package com.dsapps.wallpaperapp.Models;

/**
 * Created by Shridhar on 22-Jun-18.
 */

public class Category
{
    public String name, desc, thumb;

    public Category(String name, String desc, String thumb) {
        this.name = name;
        this.desc = desc;
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getThumb() {
        return thumb;
    }
}
