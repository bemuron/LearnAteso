package com.learnateso.learn_ateso.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Created by BE on 2/15/2018.
 */

@Entity(tableName = "sentence_construction")
public final class SentenceConstruction {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sentenceId")
    private String sentenceId;

    @NonNull
    @ColumnInfo(name = "ateso_phrase")
    private String atesoPhrase;

    @NonNull
    @ColumnInfo(name = "phrase_audio")
    private String phraseAudio;

    @NonNull
    @ColumnInfo(name = "phrase_hint")
    private String phraseHint;

    @NonNull
    @ColumnInfo(name = "opt_1")
    private String sentConstOpt1;

    @NonNull
    @ColumnInfo(name = "opt_2")
    private String sentConstOpt2;

    @NonNull
    @ColumnInfo(name = "opt_3")
    private String sentConstOpt3;

    @NonNull
    @ColumnInfo(name = "opt_4")
    private String sentConstOpt4;

    @NonNull
    @ColumnInfo(name = "section_name")
    private String sentConstSectionName;

    @NonNull
    @ColumnInfo(name = "category_name")
    private String sentConstCategoryName;


    //getters and setters

    @NonNull
    public String getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(@NonNull String sentenceId) {
        this.sentenceId = sentenceId;
    }

    @NonNull
    public String getAtesoPhrase() {
        return atesoPhrase;
    }

    public void setAtesoPhrase(@NonNull String atesoPhrase) {
        this.atesoPhrase = atesoPhrase;
    }

    @NonNull
    public String getPhraseAudio() {
        return phraseAudio;
    }

    public void setPhraseAudio(@NonNull String phraseAudio) {
        this.phraseAudio = phraseAudio;
    }

    @NonNull
    public String getPhraseHint() {
        return phraseHint;
    }

    public void setPhraseHint(@NonNull String phraseHint) {
        this.phraseHint = phraseHint;
    }

    @NonNull
    public String getSentConstOpt1() {
        return sentConstOpt1;
    }

    public void setSentConstOpt1(@NonNull String sentConstOpt1) {
        this.sentConstOpt1 = sentConstOpt1;
    }

    @NonNull
    public String getSentConstOpt2() {
        return sentConstOpt2;
    }

    public void setSentConstOpt2(@NonNull String sentConstOpt2) {
        this.sentConstOpt2 = sentConstOpt2;
    }

    @NonNull
    public String getSentConstOpt3() {
        return sentConstOpt3;
    }

    public void setSentConstOpt3(@NonNull String sentConstOpt3) {
        this.sentConstOpt3 = sentConstOpt3;
    }

    @NonNull
    public String getSentConstOpt4() {
        return sentConstOpt4;
    }

    public void setSentConstOpt4(@NonNull String sentConstOpt4) {
        this.sentConstOpt4 = sentConstOpt4;
    }

    @NonNull
    public String getSentConstSectionName() {
        return sentConstSectionName;
    }

    public void setSentConstSectionName(@NonNull String sentConstSectionName) {
        this.sentConstSectionName = sentConstSectionName;
    }

    @NonNull
    public String getSentConstCategoryName() {
        return sentConstCategoryName;
    }

    public void setSentConstCategoryName(@NonNull String sentConstCategoryName) {
        this.sentConstCategoryName = sentConstCategoryName;
    }
}
