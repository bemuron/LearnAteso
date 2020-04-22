package com.learnateso.learn_ateso.ui.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import android.database.Cursor;

import com.learnateso.learn_ateso.data.AtesoRepository;
import com.learnateso.learn_ateso.models.Section;
import com.learnateso.learn_ateso.models.WorkBook;

import java.util.List;

/**
 * Created by BE on 2/3/2018.
 */

public class WorkBookViewModel extends AndroidViewModel {

    //private member variable to hold reference to the repository
    private AtesoRepository mRepository;

    //private LiveData member variable to cache the sections
    private LiveData<List<Section>> mAllSections;

    private Cursor singleLessonCursor;

    //constructor that gets a reference to the repository and gets the categories
    public WorkBookViewModel(Application application) {
        super(application);
        mRepository = new AtesoRepository(application);
    }


    public Cursor getSingleLesson(int sectionId, int categoryId){
        singleLessonCursor = mRepository.getSingleLessonCursor(sectionId, categoryId);

        return singleLessonCursor;
    }

    public Cursor getLessonInCategory(){
        singleLessonCursor = mRepository.getSingleLesson();

        return singleLessonCursor;
    }

    //a wrapper insert() method that calls the Repository's insert() method. In this way,
    // the implementation of insert() is completely hidden from the UI.
    public void insert(WorkBook workBook) { mRepository.insertLesson(workBook); }


}
