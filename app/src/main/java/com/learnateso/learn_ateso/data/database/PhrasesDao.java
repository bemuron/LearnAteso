package com.learnateso.learn_ateso.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import android.database.Cursor;

import com.learnateso.learn_ateso.models.Phrase;

import java.util.List;

@Dao
public interface PhrasesDao {

    /*
    insert phrases into db
     */
    @Insert
    void insertPhrase(Phrase phrase);

    @Query("Delete from phrases")
    void deleteAll();

    @Query("select * from phrases where section_id = :sectionId and category_id = :categoryId")
    //Cursor getPhrasesInSection(int sectionId, int categoryId);
    LiveData<List<Phrase>> getPhrasesInSection(int sectionId, int categoryId);

    @Query("SELECT * FROM phrases")
    Cursor getPhrasesInCategory();

    //get a random phrase
    @Query("SELECT * FROM phrases WHERE phrase_id = :phraseId")
    LiveData<List<Phrase>> getRandomPhrase(int phraseId);

    //adding a favourite to the db
    @Query("UPDATE phrases SET isFavourite = 1 WHERE phrase_id = :phraseId")
    void addFavorite(int phraseId);

    //getting a favourite from the db
    @Query("SELECT * FROM phrases WHERE isFavourite = 1")
    LiveData<List<Phrase>> getFavouritePhrases();

    //removing/unsetting a favourite
    @Query("UPDATE phrases SET isFavourite = 0 WHERE phrase_id = :phraseId")
    void removeFavorite(int phraseId);

    //count all the phrases we have
    @Query("SELECT COUNT(phrase_id) FROM phrases")
    int phrasesNumber();
}
