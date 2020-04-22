package com.learnateso.learn_ateso.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Created by BE on 2/15/2018.
 */

@Entity(tableName = "wordquiz")
public final class WordQuiz {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "quizId")
    private String quizId;

    @NonNull
    @ColumnInfo(name = "ateso_word")
    private String atesoWord;

    @NonNull
    @ColumnInfo(name = "audio")
    private String audio;

    @NonNull
    @ColumnInfo(name = "hint")
    private String hint;

    @NonNull
    @ColumnInfo(name = "opt_1")
    private String opt_1;

    @NonNull
    @ColumnInfo(name = "opt_2")
    private String opt_2;

    @NonNull
    @ColumnInfo(name = "opt_3")
    private String opt_3;

    @NonNull
    @ColumnInfo(name = "opt_4")
    private String opt_4;

    @NonNull
    @ColumnInfo(name = "section_name")
    private String wordQuizSectionName;

    @NonNull
    @ColumnInfo(name = "category_name")
    private String wordQuizCategoryName;


    //getters and setters

    @NonNull
    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(@NonNull String quizId) {
        this.quizId = quizId;
    }

    @NonNull
    public String getAtesoWord() {
        return atesoWord;
    }

    public void setAtesoWord(@NonNull String atesoWord) {
        this.atesoWord = atesoWord;
    }

    @NonNull
    public String getAudio() {
        return audio;
    }

    public void setAudio(@NonNull String audio) {
        this.audio = audio;
    }

    @NonNull
    public String getHint() {
        return hint;
    }

    public void setHint(@NonNull String hint) {
        this.hint = hint;
    }

    @NonNull
    public String getOpt_1() {
        return opt_1;
    }

    public void setOpt_1(@NonNull String opt_1) {
        this.opt_1 = opt_1;
    }

    @NonNull
    public String getOpt_2() {
        return opt_2;
    }

    public void setOpt_2(@NonNull String opt_2) {
        this.opt_2 = opt_2;
    }

    @NonNull
    public String getOpt_3() {
        return opt_3;
    }

    public void setOpt_3(@NonNull String opt_3) {
        this.opt_3 = opt_3;
    }

    @NonNull
    public String getOpt_4() {
        return opt_4;
    }

    public void setOpt_4(@NonNull String opt_4) {
        this.opt_4 = opt_4;
    }

    @NonNull
    public String getWordQuizCategoryName() {
        return wordQuizCategoryName;
    }

    public void setWordQuizCategoryName(@NonNull String wordQuizCategoryName) {
        this.wordQuizCategoryName = wordQuizCategoryName;
    }

    @NonNull
    public String getWordQuizSectionName() {
        return wordQuizSectionName;
    }

    public void setWordQuizSectionName(@NonNull String wordQuizSectionName) {
        this.wordQuizSectionName = wordQuizSectionName;
    }

}
