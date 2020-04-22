package com.learnateso.learn_ateso.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by BE on 2/15/2018.
 */

@Entity(tableName = "workbook")
public final class WorkBook {
    public static final String KEY_EXERCISE_ID = "exerciseID";
    public static final String KEY_ATESO_WORD = "ateso_word";
    public static final String KEY_ATESO_AUDIO = "audio";
    public static final String KEY_HINT = "hint";
    public static final String KEY_OPTION_1 = "opt_1";
    public static final String KEY_OPTION_2 = "opt_2";
    public static final String KEY_OPTION_3 = "opt_3";
    public static final String KEY_OPTION_4 = "opt_4";
    public static final String KEY_QUIZ_ANSWER = "answer";
    public static final String KEY_PIC_NAME_1 = "pic_name1";
    public static final String KEY_PIC_NAME_2 = "pic_name2";
    public static final String KEY_PIC_NAME_3 = "pic_name3";
    public static final String KEY_PIC_NAME_4 = "pic_name4";
    public static final String KEY_PIC_AUDIO1 = "pic_audio1";
    public static final String KEY_PIC_AUDIO2 = "pic_audio2";
    public static final String KEY_PIC_AUDIO3 = "pic_audio3";
    public static final String KEY_PIC_AUDIO4 = "pic_audio4";
    public static final String KEY_ATESO_COMPARISON_AUDIO1 = "a_comparison_audio1";
    public static final String KEY_ATESO_COMPARISON_AUDIO2 = "a_comparison_audio2";
    public static final String KEY_ENGLISH_WORD = "english_word";
    public static final String KEY_SAVED_AUDIO = "saved_audio";
    public static final String KEY_ATESO_WORD_MATCH1 = "ateso_word_match1";
    public static final String KEY_ATESO_WORD_MATCH2 = "ateso_word_match2";
    public static final String KEY_ATESO_WORD_MATCH3 = "ateso_word_match3";
    public static final String KEY_ATESO_WORD_MATCH_AUDIO1 = "ateso_word_match_audio1";
    public static final String KEY_ATESO_WORD_MATCH_AUDIO2 = "ateso_word_match_audio2";
    public static final String KEY_ATESO_WORD_MATCH_AUDIO3 = "ateso_word_match_audio3";
    public static final String KEY_ENG_WORD_MATCH1 = "eng_word_match1";
    public static final String KEY_ENG_WORD_MATCH2 = "eng_word_match2";
    public static final String KEY_ENG_WORD_MATCH3 = "eng_word_match3";
    public static final String KEY_ENG_WORD_MATCH4 = "eng_word_match4";
    public static final String KEY_ENG_WORD_MATCH5 = "eng_word_match5";
    public static final String KEY_SENT_CONST_PHRASE = "sent_const_phrase";
    public static final String KEY_SENT_CONST_ANS = "sent_const_ans";
    public static final String KEY_EXERCISE_NAME = "exercise_name";
    public static final String KEY_CATEGORY_ID = "category_id";
    public static final String KEY_SECTION_ID = "section_id";


    @PrimaryKey
    @NonNull
    @ColumnInfo(name = KEY_EXERCISE_ID)
    private int exerciseID;

    @Nullable
    @ColumnInfo(name = KEY_ATESO_WORD)
    private String atesoWord;

    @NonNull
    @ColumnInfo(name = KEY_ATESO_AUDIO)
    private String audio;

    @Nullable
    @ColumnInfo(name = KEY_HINT)
    private String hint;

    @Nullable
    @ColumnInfo(name = KEY_OPTION_1)
    private String opt_1;

    @Nullable
    @ColumnInfo(name = KEY_OPTION_2)
    private String opt_2;

    @Nullable
    @ColumnInfo(name = KEY_OPTION_3)
    private String opt_3;

    @Nullable
    @ColumnInfo(name = KEY_OPTION_4)
    private String opt_4;

    @Nullable
    @ColumnInfo(name = KEY_QUIZ_ANSWER)
    private String answer;

    @Nullable
    @ColumnInfo(name = KEY_PIC_NAME_1)
    private String pic_name1;

    @Nullable
    @ColumnInfo(name = KEY_PIC_NAME_2)
    private String pic_name2;

    @Nullable
    @ColumnInfo(name = KEY_PIC_NAME_3)
    private String pic_name3;

    @Nullable
    @ColumnInfo(name = KEY_PIC_NAME_4)
    private String pic_name4;

    @Nullable
    @ColumnInfo(name = KEY_PIC_AUDIO1)
    private String pic_audio1;

    @Nullable
    @ColumnInfo(name = KEY_PIC_AUDIO2)
    private String pic_audio2;

    @Nullable
    @ColumnInfo(name = KEY_PIC_AUDIO3)
    private String pic_audio3;

    @Nullable
    @ColumnInfo(name = KEY_PIC_AUDIO4)
    private String pic_audio4;

    @Nullable
    @ColumnInfo(name = KEY_ENGLISH_WORD)
    private String english_word;

    @Nullable
    @ColumnInfo(name = KEY_SAVED_AUDIO)
    private String saved_audio;

    @Nullable
    @ColumnInfo(name = KEY_ATESO_WORD_MATCH1)
    private String awordMatch1;

    @Nullable
    @ColumnInfo(name = KEY_ATESO_WORD_MATCH2)
    private String awordMatch2;

    @Nullable
    @ColumnInfo(name = KEY_ATESO_WORD_MATCH3
    )
    private String awordMatch3;

    @Nullable
    @ColumnInfo(name = KEY_ATESO_WORD_MATCH_AUDIO1
    )
    private String awordMatchAudio1;

    @Nullable
    @ColumnInfo(name = KEY_ATESO_WORD_MATCH_AUDIO2
    )
    private String awordMatchAudio2;

    @Nullable
    @ColumnInfo(name = KEY_ATESO_WORD_MATCH_AUDIO3
    )
    private String awordMatchAudio3;

    @Nullable
    @ColumnInfo(name = KEY_ENG_WORD_MATCH1)
    private String engWordMatch1;

    @Nullable
    @ColumnInfo(name = KEY_ENG_WORD_MATCH2)
    private String engWordMatch2;

    @Nullable
    @ColumnInfo(name = KEY_ENG_WORD_MATCH3)
    private String engWordMatch3;

    @Nullable
    @ColumnInfo(name = KEY_ENG_WORD_MATCH4)
    private String engWordMatch4;

    @Nullable
    @ColumnInfo(name = KEY_ENG_WORD_MATCH5)
    private String engWordMatch5;

    @Nullable
    @ColumnInfo(name = KEY_ATESO_COMPARISON_AUDIO1)
    private String atesoComparisonAudio1;

    @Nullable
    @ColumnInfo(name = KEY_ATESO_COMPARISON_AUDIO2)
    private String atesoComparisonAudio2;

    @Nullable
    @ColumnInfo(name = KEY_SENT_CONST_PHRASE)
    private String sentConstPhrase;

    @Nullable
    @ColumnInfo(name = KEY_SENT_CONST_ANS)
    private String sentConstAns;


    @NonNull
    @ColumnInfo(name = KEY_EXERCISE_NAME)
    private String exercise_name;

    @NonNull
    @ColumnInfo(name = KEY_SECTION_ID)
    private int workBookSectionId;

    @NonNull
    @ColumnInfo(name = KEY_CATEGORY_ID)
    private int workBookCategoryId;


    //getters and setters


    @NonNull
    public int getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(@NonNull int exerciseID) {
        this.exerciseID = exerciseID;
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

    @Nullable
    public String getHint() {
        return hint;
    }

    public void setHint(@Nullable String hint) {
        this.hint = hint;
    }

    @Nullable
    public String getOpt_1() {
        return opt_1;
    }

    public void setOpt_1(@Nullable String opt_1) {
        this.opt_1 = opt_1;
    }

    @Nullable
    public String getOpt_2() {
        return opt_2;
    }

    public void setOpt_2(@Nullable String opt_2) {
        this.opt_2 = opt_2;
    }

    @Nullable
    public String getOpt_3() {
        return opt_3;
    }

    public void setOpt_3(@Nullable String opt_3) {
        this.opt_3 = opt_3;
    }

    @Nullable
    public String getOpt_4() {
        return opt_4;
    }

    public void setOpt_4(@Nullable String opt_4) {
        this.opt_4 = opt_4;
    }

    @Nullable
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(@Nullable String answer) {
        this.answer = answer;
    }

    @Nullable
    public String getPic_name1() {
        return pic_name1;
    }

    public void setPic_name1(@Nullable String pic_name1) {
        this.pic_name1 = pic_name1;
    }

    @Nullable
    public String getPic_name2() {
        return pic_name2;
    }

    public void setPic_name2(@Nullable String pic_name2) {
        this.pic_name2 = pic_name2;
    }

    @Nullable
    public String getPic_name3() {
        return pic_name3;
    }

    public void setPic_name3(@Nullable String pic_name3) {
        this.pic_name3 = pic_name3;
    }

    @Nullable
    public String getPic_name4() {
        return pic_name4;
    }

    public void setPic_name4(@Nullable String pic_name4) {
        this.pic_name4 = pic_name4;
    }

    @Nullable
    public String getPic_audio1() {
        return pic_audio1;
    }

    public void setPic_audio1(@Nullable String pic_audio1) {
        this.pic_audio1 = pic_audio1;
    }

    @Nullable
    public String getPic_audio2() {
        return pic_audio2;
    }

    public void setPic_audio2(@Nullable String pic_audio2) {
        this.pic_audio2 = pic_audio2;
    }

    @Nullable
    public String getPic_audio3() {
        return pic_audio3;
    }

    public void setPic_audio3(@Nullable String pic_audio3) {
        this.pic_audio3 = pic_audio3;
    }

    @Nullable
    public String getPic_audio4() {
        return pic_audio4;
    }

    public void setPic_audio4(@Nullable String pic_audio4) {
        this.pic_audio4 = pic_audio4;
    }

    @Nullable
    public String getEnglish_word() {
        return english_word;
    }

    public void setEnglish_word(@Nullable String english_word) {
        this.english_word = english_word;
    }

    @Nullable
    public String getSaved_audio() {
        return saved_audio;
    }

    public void setSaved_audio(@Nullable String saved_audio) {
        this.saved_audio = saved_audio;
    }

    @Nullable
    public String getAwordMatch1() {
        return awordMatch1;
    }

    public void setAwordMatch1(@Nullable String awordMatch1) {
        this.awordMatch1 = awordMatch1;
    }

    @Nullable
    public String getAwordMatch2() {
        return awordMatch2;
    }

    public void setAwordMatch2(@Nullable String awordMatch2) {
        this.awordMatch2 = awordMatch2;
    }

    @Nullable
    public String getAwordMatch3() {
        return awordMatch3;
    }

    public void setAwordMatch3(@Nullable String awordMatch3) {
        this.awordMatch3 = awordMatch3;
    }

    @Nullable
    public String getAwordMatchAudio1() {
        return awordMatchAudio1;
    }

    public void setAwordMatchAudio1(@Nullable String awordMatchAudio1) {
        this.awordMatchAudio1 = awordMatchAudio1;
    }

    @Nullable
    public String getAwordMatchAudio2() {
        return awordMatchAudio2;
    }

    public void setAwordMatchAudio2(@Nullable String awordMatchAudio2) {
        this.awordMatchAudio2 = awordMatchAudio2;
    }

    @Nullable
    public String getAwordMatchAudio3() {
        return awordMatchAudio3;
    }

    public void setAwordMatchAudio3(@Nullable String awordMatchAudio3) {
        this.awordMatchAudio3 = awordMatchAudio3;
    }

    @Nullable
    public String getAtesoComparisonAudio1() {
        return atesoComparisonAudio1;
    }

    public void setAtesoComparisonAudio1(@Nullable String atesoComparisonAudio1) {
        this.atesoComparisonAudio1 = atesoComparisonAudio1;
    }

    @Nullable
    public String getAtesoComparisonAudio2() {
        return atesoComparisonAudio2;
    }

    public void setAtesoComparisonAudio2(@Nullable String atesoComparisonAudio2) {
        this.atesoComparisonAudio2 = atesoComparisonAudio2;
    }

    @Nullable
    public String getEngWordMatch1() {
        return engWordMatch1;
    }

    public void setEngWordMatch1(@Nullable String engWordMatch1) {
        this.engWordMatch1 = engWordMatch1;
    }

    @Nullable
    public String getEngWordMatch2() {
        return engWordMatch2;
    }

    public void setEngWordMatch2(@Nullable String engWordMatch2) {
        this.engWordMatch2 = engWordMatch2;
    }

    @Nullable
    public String getSentConstPhrase() {
        return sentConstPhrase;
    }

    public void setSentConstPhrase(@Nullable String sentConstPhrase) {
        this.sentConstPhrase = sentConstPhrase;
    }

    @Nullable
    public String getSentConstAns() {
        return sentConstAns;
    }

    public void setSentConstAns(@Nullable String sentConstAns) {
        this.sentConstAns = sentConstAns;
    }

    @Nullable
    public String getEngWordMatch3() {
        return engWordMatch3;
    }

    public void setEngWordMatch3(@Nullable String engWordMatch3) {
        this.engWordMatch3 = engWordMatch3;
    }

    @Nullable
    public String getEngWordMatch4() {
        return engWordMatch4;
    }

    public void setEngWordMatch4(@Nullable String engWordMatch4) {
        this.engWordMatch4 = engWordMatch4;
    }

    @Nullable
    public String getEngWordMatch5() {
        return engWordMatch5;
    }

    public void setEngWordMatch5(@Nullable String engWordMatch5) {
        this.engWordMatch5 = engWordMatch5;
    }

    @NonNull
    public String getExercise_name() {
        return exercise_name;
    }

    public void setExercise_name(@NonNull String exercise_name) {
        this.exercise_name = exercise_name;
    }

    @NonNull
    public int getWorkBookSectionId() {
        return workBookSectionId;
    }

    public void setWorkBookSectionId(@NonNull int workBookSectionId) {
        this.workBookSectionId = workBookSectionId;
    }

    @NonNull
    public int getWorkBookCategoryId() {
        return workBookCategoryId;
    }

    public void setWorkBookCategoryId(@NonNull int workBookCategoryId) {
        this.workBookCategoryId = workBookCategoryId;
    }
}
