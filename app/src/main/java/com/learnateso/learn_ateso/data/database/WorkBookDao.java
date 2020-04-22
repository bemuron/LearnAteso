package com.learnateso.learn_ateso.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import android.database.Cursor;

import com.learnateso.learn_ateso.models.*;

/**
 * Created by BE on 2/3/2018.
 */

@Dao
public interface WorkBookDao {

    /*
    insert exercises into db
     */
    @Insert
    void insertLesson(WorkBook workBook);

    @Query("Delete from workbook")
    void deleteAll();

    @Query("select * from workbook where section_id = :sectionId and category_id = :categoryId")
    Cursor getLessonInSection(int sectionId, int categoryId);

    @Query("SELECT * FROM workbook")
    Cursor getLessonInCategory();
}
