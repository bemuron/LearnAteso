package com.learnateso.learn_ateso.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.learnateso.learn_ateso.models.Category;
import com.learnateso.learn_ateso.models.ExploreCategory;

import java.util.List;

/**
 * Created by BE on 2/3/2018.
 */

@Dao
public interface CategoriesDao {

    /*
    insert categories into db
     */
    @Insert
    void insertCategory(Category category);

    @Query("Delete from categories")
    void deleteAll();

    @Query("SELECT * from categories ORDER BY category_id ASC")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * from explore_categories ORDER BY explore_id ASC")
    LiveData<List<ExploreCategory>> getExploreCategories();

    /*
    insert explore categories into db
     */
    @Insert
    void insertExploreCategory(ExploreCategory category);
}