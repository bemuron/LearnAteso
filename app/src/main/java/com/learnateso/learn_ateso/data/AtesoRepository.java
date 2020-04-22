package com.learnateso.learn_ateso.data;

import android.app.Application;
import androidx.lifecycle.LiveData;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.learnateso.learn_ateso.data.database.AtesoDatabase;
import com.learnateso.learn_ateso.data.database.CategoriesDao;
import com.learnateso.learn_ateso.data.database.PhrasesDao;
import com.learnateso.learn_ateso.helpers.AppExecutors;
import com.learnateso.learn_ateso.models.Category;
import com.learnateso.learn_ateso.data.database.SectionsDao;
import com.learnateso.learn_ateso.data.database.UsersDao;
import com.learnateso.learn_ateso.data.database.WorkBookDao;
import com.learnateso.learn_ateso.models.Phrase;
import com.learnateso.learn_ateso.models.Section;
import com.learnateso.learn_ateso.models.User;
import com.learnateso.learn_ateso.models.WorkBook;

import java.util.List;

/**
 * Created by BE on 2/3/2018.
 */

public class AtesoRepository {

    private static final String TAG = AtesoRepository.class.getSimpleName();
    private static AtesoRepository instance;
    private CategoriesDao mCategoriesDao;
    private SectionsDao mSectionsDao;
    private WorkBookDao mWorkBookDao;
    private PhrasesDao mPhrasesDao;
    private AppExecutors mExecutors;
    private LiveData<List<Category>> mAllCategories;
    private LiveData<List<Section>> mAllSections;
    private LiveData<List<Phrase>> phraseSectionList;
    private LiveData<List<Phrase>> favouritePhraseList;
    private LiveData<List<Phrase>> randomPhraseList;
    private UsersDao mUsersDao;
    private Cursor singleLessonCursor, mUserDetail;
    private int phraseNumber;

    //constructor that gets a handle to the db and initializes the member
    //variables
    public AtesoRepository(Application application){
        AtesoDatabase db = AtesoDatabase.getDatabase(application);
        mUsersDao = db.usersDao();
        mCategoriesDao = db.categoriesDao();
        mSectionsDao = db.sectionsDao();
        mWorkBookDao = db.workBookDao();
        mPhrasesDao = db.phrasesDao();
        mAllCategories = mCategoriesDao.getAllCategories();
        mExecutors = AppExecutors.getInstance();
        instance = this;
        //mUserDetail = mUsersDao.getUserDetails();
        //mAllSections = mSectionsDao.getSectionsInCategory("Basics");
    }

    public static AtesoRepository getInstance(){
        return  instance;
    }

    //a wrapper for getAllCategories. Room executes all queries on a separate
    //thread. Observed LiveData will notify the observer when the data has changed
    public LiveData<List<Category>> getAllCategories(){
        return mAllCategories;
    }

    public LiveData<List<Section>> getSections( final int categoryId) {

        mAllSections = mSectionsDao.getSectionsInCategory(categoryId);

        return mAllSections;

    }

    public Cursor getSingleLessonCursor(int sectionId, int categoryId){
        singleLessonCursor = mWorkBookDao.getLessonInSection(sectionId, categoryId);

        return singleLessonCursor;
    }

    public Cursor getSingleLesson(){
        singleLessonCursor = mWorkBookDao.getLessonInCategory();

        return singleLessonCursor;
    }

    /*public Cursor getPhrasesInSection(int sectionId, int categoryId){
        phraseSectionCursor = mPhrasesDao.getPhrasesInSection(sectionId, categoryId);
        //phraseSectionList = mPhrasesDao.getPhrasesInSection(sectionId, categoryId);

        return phraseSectionCursor;
    }*/

    public LiveData<List<Phrase>> getPhrasesInSection(int sectionId, int categoryId){
        //phraseSectionCursor = mPhrasesDao.getPhrasesInSection(sectionId, categoryId);
        phraseSectionList = mPhrasesDao.getPhrasesInSection(sectionId, categoryId);

        return phraseSectionList;
    }

    public LiveData<List<Phrase>> getRandomPhraseList(int phraseId){
        randomPhraseList = mPhrasesDao.getRandomPhrase(phraseId);

        return randomPhraseList;
    }

    public Cursor getUser(){
        mUserDetail = mUsersDao.getUserDetails();

        return mUserDetail;
    }

    //adding a favourite
    public void addFavourite(int phraseId){
        new addFavouriteAsyncTask(mPhrasesDao).execute(phraseId);
    }

    //removing a favourite
    public void removeFavourite(int phraseId){
        new removeFavouriteAsyncTask(mPhrasesDao).execute(phraseId);
    }

    //getting all favourite phrases
    public LiveData<List<Phrase>>  getAllFavouritePhrases(){
        favouritePhraseList = mPhrasesDao.getFavouritePhrases();

        return  favouritePhraseList;
    }

    //get the number of phrases we have in the db
    public int countAllPhrases(){

        mExecutors.diskIO().execute(() ->{
            phraseNumber = mPhrasesDao.phrasesNumber();
            Log.e(TAG, "phrase counted in = "+ phraseNumber);
        });

        Log.e(TAG, "phrase counted = "+ phraseNumber);
        return phraseNumber;
    }

    //a wrapper for the insert() method. Must be called on a non UI thread
    //or the app will crash
    public void insertUser (User user){
        new insertUserAsyncTask(mUsersDao).execute(user);
    }

    public void deleteUser (){
        mUsersDao.deleteUser();
    }

    //a wrapper for the insert() method. Must be called on a non UI thread
    //or the app will crash
    public void insert (Category category){
        new insertAsyncTask(mCategoriesDao).execute(category);
    }

    //a wrapper for the insert() method. Must be called on a non UI thread
    //or the app will crash
    public void insertSection (Section section){
        new insertSectionAsyncTask(mSectionsDao).execute(section);
    }

    //a wrapper for the insert() method. Must be called on a non UI thread
    //or the app will crash
    public void insertLesson (WorkBook workBook){
        new insertLessonAsyncTask(mWorkBookDao).execute(workBook);
    }

    private static class insertUserAsyncTask extends AsyncTask<User, Void, Void>{

        private UsersDao mUserAsyncTaskDao;

        insertUserAsyncTask(UsersDao dao){
            mUserAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final User... params){
            mUserAsyncTaskDao.insertUser(params[0]);
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<Category, Void, Void>{

        private CategoriesDao mAsyncTaskDao;

        insertAsyncTask(CategoriesDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Category... params){
            mAsyncTaskDao.insertCategory(params[0]);
            return null;
        }
    }

    private static class insertSectionAsyncTask extends AsyncTask<Section, Void, Void>{

        private SectionsDao mAsyncSectionDao;

        insertSectionAsyncTask(SectionsDao dao){
            mAsyncSectionDao = dao;
        }

        @Override
        protected Void doInBackground(final Section... params){
            mAsyncSectionDao.insertSection(params[0]);
            return null;
        }
    }

    //insert new lesson row to the db
    private static class insertLessonAsyncTask extends AsyncTask<WorkBook, Void, Void>{

        private WorkBookDao mAsyncTaskDao;

        insertLessonAsyncTask(WorkBookDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final WorkBook... params){
            mAsyncTaskDao.insertLesson(params[0]);
            return null;
        }
    }

    //add a favourite to the db
    private static class addFavouriteAsyncTask extends AsyncTask<Integer, Void, Void>{

        private PhrasesDao mAsyncTaskDao;

        addFavouriteAsyncTask(PhrasesDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Integer... params){
            mAsyncTaskDao.addFavorite(params[0]);
            return null;
        }
    }

    //remove a favourite to the db
    private static class removeFavouriteAsyncTask extends AsyncTask<Integer, Void, Void>{

        private PhrasesDao mAsyncTaskDao;

        removeFavouriteAsyncTask(PhrasesDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Integer... params){
            mAsyncTaskDao.removeFavorite(params[0]);
            return null;
        }
    }

    //count the number of phrases in the db
    private static class countNumberOfPhrasesAsyncTask extends AsyncTask<Void, Void, Void>{

        private PhrasesDao mAsyncTaskDao;
        private int phrasesNumber;

        countNumberOfPhrasesAsyncTask(PhrasesDao dao, int phrasesNumber){
            mAsyncTaskDao = dao;
            this.phrasesNumber = phrasesNumber;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            phrasesNumber = mAsyncTaskDao.phrasesNumber();
            Log.e(TAG, "phrase count in doinback = "+ phrasesNumber);
            return null;
        }
    }


}
