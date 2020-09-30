package com.learnateso.learn_ateso.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "explore_categories")
public final class ExploreCategory {

    @PrimaryKey
    @ColumnInfo(name = "explore_id")
    private int exploreId;

    @ColumnInfo(name = "name")
    private String mExploreName;

    @ColumnInfo(name = "description")
    private String mExploreDescription;

    @ColumnInfo(name = "image")
    private String mExploreImage;

    public int getExploreId() {
        return exploreId;
    }

    public void setExploreId(int exploreId) {
        this.exploreId = exploreId;
    }

    public String getExploreName() {
        return mExploreName;
    }

    public void setExploreName(String mExploreName) {
        this.mExploreName = mExploreName;
    }

    public String getExploreDescription() {
        return mExploreDescription;
    }

    public void setExploreDescription(String mExploreDescription) {
        this.mExploreDescription = mExploreDescription;
    }

    public String getExploreImage() {
        return mExploreImage;
    }

    public void setExploreImage(String mExploreImage) {
        this.mExploreImage = mExploreImage;
    }
}
