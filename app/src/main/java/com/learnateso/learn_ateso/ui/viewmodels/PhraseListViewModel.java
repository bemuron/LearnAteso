package com.learnateso.learn_ateso.ui.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import android.database.Cursor;

import com.learnateso.learn_ateso.data.AtesoRepository;
import com.learnateso.learn_ateso.models.Phrase;

import java.util.List;

public class PhraseListViewModel extends AndroidViewModel {

    //private member variable to hold reference to the repository
    private AtesoRepository mRepository;

    private Cursor phraseSectionCursor, phraseEnglishTranslation;
    private LiveData<List<Phrase>> phraseSectionList;
    private LiveData<List<Phrase>> favouritePhraseList;
    private LiveData<List<Phrase>> randomPhraseList;

    //constructor that gets a reference to the repository and gets the categories
    public PhraseListViewModel(Application application) {
        super(application);
        mRepository = new AtesoRepository(application);
    }


    /*public Cursor getPhrasesInSection(int sectionId, int categoryId){
        phraseSectionCursor = mRepository.getPhrasesInSection(sectionId, categoryId);

        return phraseSectionCursor;
    }*/

    public LiveData<List<Phrase>> getPhrasesInSection(int sectionId, int categoryId){
        phraseSectionList = mRepository.getPhrasesInSection(sectionId, categoryId);

        return phraseSectionList;
    }

    public LiveData<List<Phrase>> getRandomPhraseList(int phraseId){
        randomPhraseList = mRepository.getRandomPhraseList(phraseId);

        return randomPhraseList;
    }

    //get all favourite phrases
    public LiveData<List<Phrase>> getFavouritePhrases(){
        favouritePhraseList = mRepository.getAllFavouritePhrases();

        return favouritePhraseList;
    }

    //add a favourite verse
    public void addFavouritePhrase(int phraseId){
        mRepository.addFavourite(phraseId);
    }

    //remove favourite
    public void removeFavouritePhrase(int phraseId){
        mRepository.removeFavourite(phraseId);
    }

    //a wrapper insert() method that calls the Repository's insert() method. In this way,
    // the implementation of insert() is completely hidden from the UI.
    //public void insert(WorkBook workBook) { mRepository.insertLesson(workBook); }

}
