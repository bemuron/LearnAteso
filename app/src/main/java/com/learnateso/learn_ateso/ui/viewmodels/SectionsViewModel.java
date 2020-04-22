package com.learnateso.learn_ateso.ui.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.learnateso.learn_ateso.data.AtesoRepository;
import com.learnateso.learn_ateso.models.Section;

import java.util.List;

/**
 * Created by BE on 2/3/2018.
 */

public class SectionsViewModel extends AndroidViewModel {

    //private member variable to hold reference to the repository
    private AtesoRepository mRepository;

    //private LiveData member variable to cache the sections
    private LiveData<List<Section>> mAllSections;

    //constructor that gets a reference to the repository and gets the categories
    public SectionsViewModel(Application application) {
        super(application);
        mRepository = new AtesoRepository(application);
        //mAllSections = mRepository.getSectionsInCategory();
    }

    //a getter method for all the sections. This hides the implementation from the UI
    public LiveData<List<Section>> getSectionsInCategory(){
        return mAllSections;
    }

    public LiveData<List<Section>> getSections(int categoryId) {
        //if (categoryName != null) {
            mAllSections = mRepository.getSections(categoryId);

        //}
        return mAllSections;
    }

    //a wrapper insert() method that calls the Repository's insert() method. In this way,
    // the implementation of insert() is completely hidden from the UI.
    public void insert(Section section) { mRepository.insertSection(section); }


}
