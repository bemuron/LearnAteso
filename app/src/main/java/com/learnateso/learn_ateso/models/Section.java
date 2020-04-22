package com.learnateso.learn_ateso.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Created by BE on 2/15/2018.
 */

@Entity(tableName = "sections")
public final class Section {

    @PrimaryKey
    @ColumnInfo(name = "section_id")
    private int sectionId;

    @ColumnInfo(name = "section_name")
    private String sectionName;

    @ColumnInfo(name = "section_image")
    private String sectionImage;

    @ColumnInfo(name = "category_id")
    private int categoryId;
/*
    //constructor
    public Section(@NonNull String Id, @NonNull String sectionName, @NonNull String sectionImage) {
        this.Id = Id;
        this.sectionName = sectionName;
    }
*/
    //getters and setters
    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionImage() {
        return sectionImage;
    }

    public void setSectionImage(String sectionImage) {
        this.sectionImage = sectionImage;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

}
