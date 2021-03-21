package com.learnateso.learn_ateso.ui.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.learnateso.learn_ateso.models.Category;
import com.learnateso.learn_ateso.data.AtesoRepository;
import com.learnateso.learn_ateso.models.ExploreCategory;

import java.util.List;

/**
 * Created by BE on 2/3/2018.
 */

public class CategoriesViewModel extends AndroidViewModel {

    //private member variable to hold reference to the repository
    private AtesoRepository mRepository;

    //private LiveData member variable to cache the categories
    private LiveData<List<Category>> mAllCategories;

    //private LiveData member variable to cache the categories
    private LiveData<List<ExploreCategory>> mExploreCategories;

    //constructor that gets a reference to the repository and gets the categories
    public CategoriesViewModel(Application application) {
        super(application);
        mRepository = new AtesoRepository(application);
        mAllCategories = mRepository.getAllCategories();
        //mExploreCategories = mRepository.getExploreCategories();
    }

    //a getter method for all the categories. This hides the implementation from the UI
    public LiveData<List<Category>> getAllCategories(){
        return mAllCategories;
    }

    //a wrapper insert() method that calls the Repository's insert() method. In this way,
    // the implementation of insert() is completely hidden from the UI.
    public void insert(Category category) { mRepository.insert(category); }

    //a getter method for all the explore categories. This hides the implementation from the UI
    /*public LiveData<List<ExploreCategory>> getExploreCategories(){
        return mExploreCategories;
    }*/


}
