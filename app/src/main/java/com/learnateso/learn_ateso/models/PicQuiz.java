package com.learnateso.learn_ateso.models;

/**
 * Created by BE on 2/26/2018.
 */

public class PicQuiz {

    private String Id;

    private String mPicQuizItemName;

    private String mPicImageName;

    private String mAudioName;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getmPicQuizItemName() {
        return mPicQuizItemName;
    }

    public void setmPicQuizItemName(String mPicQuizItemName) {
        this.mPicQuizItemName = mPicQuizItemName;
    }

    public String getmPicImageName() {
        return mPicImageName;
    }

    public void setmPicImageName(String mPicImageName) {
        this.mPicImageName = mPicImageName;
    }

    public String getmAudioName() {
        return mAudioName;
    }

    public void setmAudioName(String mAudioName) {
        this.mAudioName = mAudioName;
    }
}
