package com.learnateso.learn_ateso.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by BE on 2/3/2018.
 */

@Entity(tableName = "categories")
public final class Category {

    @PrimaryKey
    @ColumnInfo(name = "category_id")
    private int categoryId;

    @ColumnInfo(name = "category_name")
    private String mCategoryName;

    @ColumnInfo(name = "category_image")
    private String mCategoryImage;

    /*
    public Category(@NonNull String mCategoryName, @NonNull String mCategoryImage){
        //this.mId = cat_id;
        this.mCategoryName = mCategoryName;
        this.mCategoryImage = mCategoryImage;
    }
*/
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String mCategoryName){
        this.mCategoryName = mCategoryName;
    }

    public void setCategoryImage(String mCategoryImage){
        this.mCategoryImage = mCategoryImage;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public String getCategoryImage() {
        return mCategoryImage;
    }
}
