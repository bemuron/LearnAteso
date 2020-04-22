package com.learnateso.learn_ateso.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "phrases")
public final class Phrase {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "phrase_id")
    private int phraseId;

    @ColumnInfo(name = "ateso_phrase")
    private String atesoPhrase;

    @ColumnInfo(name = "audio")
    private String atesoAudio;

    @ColumnInfo(name = "translation")
    private String translation;

    @ColumnInfo(name = "isFavourite")
    private int isFavourite;

    @ColumnInfo(name = "section_id")
    private int phraseSectionId;

    @ColumnInfo(name = "category_id")
    private int phraseCategoryId;

    @NonNull
    public int getPhraseId() {
        return phraseId;
    }

    public void setPhraseId(@NonNull int phraseId) {
        this.phraseId = phraseId;
    }

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