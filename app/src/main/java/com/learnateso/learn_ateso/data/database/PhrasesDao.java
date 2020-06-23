package com.learnateso.learn_ateso.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import android.app.SearchManager;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.learnateso.learn_ateso.models.Phrase;

import java.util.List;

@Dao
public interface PhrasesDao {

    //Table Name
    static final String FTS_VIRTUAL_PHRASES_TABLE = "phrases";

    //column names
    public static final String KEY_ID = "_id";
    //public static final String KEY_ATESO_PHRASE = "ateso_phrase";
    //public static final String KEY_TRANSLATION = "translation";
    //column name below is what is expected for the search to work
    //the name is suggest_text_1
    public static final String KEY_ATESO_PHRASE = SearchManager.SUGGEST_COLUMN_TEXT_1;
    //suggest_text_2
    public static final String KEY_TRANSLATION = SearchManager.SUGGEST_COLUMN_TEXT_2;

    /*
    insert phrases into db
     */
    @Insert
    void insertPhrase(Phrase phrase);

    @Query("Delete from phrases")
    void deleteAll();

    @Query("select rowid AS rowid," +KEY_TRANSLATION + "," + KEY_ATESO_PHRASE +", audio, isFavourite, " +
            "phrase_pic, section_id, " +
            "category_id from phrases where section_id = :sectionId and category_id = :categoryId")
    LiveData<List<Phrase>> getPhrasesInSection(int sectionId, int categoryId);

    //@Query("SELECT * FROM phrases")
    //Cursor getPhrasesInCategory();

    //get a random phrase
    @Query("select rowid AS rowid," +KEY_TRANSLATION + "," + KEY_ATESO_PHRASE +", audio, isFavourite, " +
            "phrase_pic, section_id, category_id FROM phrases WHERE rowid = :phraseId")
    LiveData<List<Phrase>> getRandomPhrase(int phraseId);

    //adding a favourite to the db
    @Query("UPDATE phrases SET isFavourite = 1 WHERE rowid = :phraseId")
    void addFavorite(int phraseId);

    //getting a favourite from the db
    @Query("select rowid AS rowid," +KEY_TRANSLATION + "," + KEY_ATESO_PHRASE +", audio, isFavourite," +
            " phrase_pic, section_id, category_id FROM phrases WHERE isFavourite = 1")
    LiveData<List<Phrase>> getFavouritePhrases();

    //removing/unsetting a favourite
    @Query("UPDATE phrases SET isFavourite = 0 WHERE rowid = :phraseId")
    void removeFavorite(int phraseId);

    //count all the phrases we have
    @Query("SELECT COUNT(rowid) FROM phrases")
    int phrasesNumber();

    /*Phrases Search Feature queries*/

    @Query("SELECT "+KEY_TRANSLATION + ","+KEY_ATESO_PHRASE+ ", rowid AS "+ BaseColumns._ID+ ", " +
            "rowid AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID+ ", rowid AS " +
            SearchManager.SUGGEST_COLUMN_SHORTCUT_ID+
            " FROM phrases WHERE "+KEY_TRANSLATION + " MATCH :query")
    Cursor getWordMatches(String query);

    @Query("SELECT rowid AS rowid,"+KEY_TRANSLATION + ","+KEY_ATESO_PHRASE+ ", audio, isFavourite, " +
            "phrase_pic, section_id, category_id FROM phrases WHERE rowid = :rowid")
    Cursor getPhrase(String rowid);

    //get search results
    @Query("select rowid AS rowid," +KEY_TRANSLATION + "," + KEY_ATESO_PHRASE +", audio, isFavourite, phrase_pic, section_id, " +
            "category_id FROM phrases WHERE "+KEY_TRANSLATION + " MATCH :query")
    LiveData<List<Phrase>> getSearchResults(String query);

    //get search results cursor
    @Query("select rowid AS rowid," +KEY_TRANSLATION + "," + KEY_ATESO_PHRASE +", audio, isFavourite, phrase_pic, section_id, " +
            "category_id FROM phrases WHERE "+KEY_TRANSLATION + " MATCH :query")
    Cursor getSearchResultsCursor(String query);
}
