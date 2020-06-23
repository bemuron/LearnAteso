package com.learnateso.learn_ateso.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.learnateso.learn_ateso.models.Section;

import java.util.List;

/**
 * Created by BE on 2/3/2018.
 */

@Dao
public interface SectionsDao {

    /*
    insert categories into db
     */
    @Insert
    void insertSection(Section section);

    @Query("Delete from sections")
    void deleteAll();

    @Query("SELECT * FROM sections WHERE category_id = :category_id ORDER BY section_id ASC")
    LiveData<List<Section>> getSectionsInCategory(int category_id);

    @Query("SELECT section_name FROM sections WHERE section_id = :section_id")
    String getSectionName(int section_id);

}
