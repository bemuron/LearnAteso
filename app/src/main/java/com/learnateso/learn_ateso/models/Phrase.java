package com.learnateso.learn_ateso.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts3;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.learnateso.learn_ateso.data.database.PhrasesDao;

@Fts3
@Entity(tableName = "phrases")
public final class Phrase {

    @PrimaryKey
    @NonNull
    //@ColumnInfo(name = "phrase_id")
    @ColumnInfo(name = "rowid")
    private int rowId;
    //private int phraseId;

    @ColumnInfo(name = PhrasesDao.KEY_ATESO_PHRASE)//ateso_phrase
    private String atesoPhrase;

    @ColumnInfo(name = "audio")
    private String atesoAudio;

    @ColumnInfo(name = PhrasesDao.KEY_TRANSLATION)//translation
    private String translation;

    @ColumnInfo(name = "isFavourite")
    private int isFavourite;

    @ColumnInfo(name = "phrase_pic")
    private String phrasePic;

    @ColumnInfo(name = "section_id")
    private int phraseSectionId;

    @ColumnInfo(name = "category_id")
    private int phraseCategoryId;

    @NonNull
    public int getRowId() {
        return rowId;
    }

    public void setRowId(@NonNull int rowId) {
        this.rowId = rowId;
    }

    /*@NonNull
    public int getPhraseId() {
        return rowId;
    }

    public void setPhraseId(@NonNull int rowId) {
        this.rowId = rowId;
    }*/

    public String getAtesoPhrase() {
        return atesoPhrase;
    }

    public void setAtesoPhrase(String atesoPhrase) {
        this.atesoPhrase = atesoPhrase;
    }

    public String getAtesoAudio() {
        return atesoAudio;
    }

    public void setAtesoAudio(String atesoAudio) {
        this.atesoAudio = atesoAudio;
    }

    public String getTranslation() {
        return translation;
    }

    public String getPhrasePic() {
        return phrasePic;
    }

    public void setPhrasePic(String phrasePic) {
        this.phrasePic = phrasePic;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public int getPhraseSectionId() {
        return phraseSectionId;
    }

    public void setPhraseSectionId(int phraseSectionId) {
        this.phraseSectionId = phraseSectionId;
    }

    public int getPhraseCategoryId() {
        return phraseCategoryId;
    }

    public void setPhraseCategoryId(int phraseCategoryId) {
        this.phraseCategoryId = phraseCategoryId;
    }

    public int getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(int isFavourite) {
        this.isFavourite = isFavourite;
    }
}