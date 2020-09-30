package com.learnateso.learn_ateso.data;

import android.app.Application;
import androidx.lifecycle.LiveData;

import android.app.SearchManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.util.Log;

import com.learnateso.learn_ateso.data.database.AtesoDatabase;
import com.learnateso.learn_ateso.data.database.CategoriesDao;
import com.learnateso.learn_ateso.data.database.PhrasesDao;
import com.learnateso.learn_ateso.helpers.AppExecutors;
import com.learnateso.learn_ateso.models.Category;
import com.learnateso.learn_ateso.data.database.SectionsDao;
import com.learnateso.learn_ateso.data.database.UsersDao;
import com.learnateso.learn_ateso.data.database.WorkBookDao;
import com.learnateso.learn_ateso.models.ExploreCategory;
import com.learnateso.learn_ateso.models.Phrase;
import com.learnateso.learn_ateso.models.Section;
import com.learnateso.learn_ateso.models.User;
import com.learnateso.learn_ateso.models.WorkBook;

import java.util.HashMap;
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
    private LiveData<List<ExploreCategory>> mExploreCategories;
    private LiveData<List<Section>> mAllSections;
    private LiveData<List<Phrase>> phraseSectionList;
    private LiveData<List<Phrase>> favouritePhraseList;
    private LiveData<List<Phrase>> randomPhraseList;
    private LiveData<List<Phrase>> phraseSearchResultsList;
    private UsersDao mUsersDao;
    private Cursor singleLessonCursor, mUserDetail, searchResultsCursor;
    private static Cursor cursor;
    private int phraseNumber;
    private static final HashMap<String,String> mColumnMap = buildColumnMap();

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
        mExploreCategories = mCategoriesDao.getExploreCategories();
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

    //get the explore categories
    public LiveData<List<ExploreCategory>> getExploreCategories(){
        return mExploreCategories;
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

    //get the section name
    public String getSectionName(int sectionId){
        Log.e(TAG, "Section name = "+ mSectionsDao.getSectionName(sectionId));
        return mSectionsDao.getSectionName(sectionId);
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

    //queries for the search functionality
    public LiveData<List<Phrase>> getSearchResults(String query){
        phraseSearchResultsList = mPhrasesDao.getSearchResults(query);

        return phraseSearchResultsList;
    }

    public Cursor getSearchResultsCursor(String query){
        searchResultsCursor = mPhrasesDao.getSearchResultsCursor(query);

        return searchResultsCursor;
    }

    /**
     * Returns a Cursor over all words that match the given query
     *
     * @param query The string to search for
     * @param columns The columns to include, if null then all are included
     * @return Cursor over all words that match, or null if none found.
     */
    public Cursor getWordMatches(String query, String[] columns) {
        Cursor cursor;
        String selection = mPhrasesDao.KEY_TRANSLATION + " MATCH ?";
        String[] selectionArgs = new String[] {query+"*"};
        Log.e(TAG, "Search query = "+query);

        cursor = mPhrasesDao.getWordMatches(query+"*");
        //new getWordMatchesAsyncTask(mPhrasesDao).execute(query);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;

        //return query(selection, selectionArgs, columns);

         /*This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE <COL_WORD> MATCH 'query*'
         * which is an FTS3 search for the query text (plus a wildcard) inside the word column.
         *
         * - "rowid" is the unique id for all rows but we need this value for the "_id" column in
         *    order for the Adapters to work, so the columns need to make "_id" an alias for "rowid"
         * - "rowid" also needs to be used by the SUGGEST_COLUMN_INTENT_DATA alias in order
         *   for suggestions to carry the proper intent data.
         *   These aliases are defined in the IssuesProvider when queries are made.
         * - This can be revised to also search the definition text with FTS3 by changing
         *   the selection clause to use FTS_VIRTUAL_TABLE instead of COL_WORD (to search across
         *   the entire table, but sorting the relevance could be difficult.
          */

    }

    /**
     * Performs a database query.
     * @param selection The selection clause
     * @param selectionArgs Selection arguments for "?" components in the selection
     * @param columns The columns to return
     * @return A Cursor over all rows matching the query
     */
    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        /* The SQLiteBuilder provides a map for all possible columns requested to
         * actual columns in the database, creating a simple column alias mechanism
         * by which the ContentProvider does not need to know the real column names
         */
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(mPhrasesDao.FTS_VIRTUAL_PHRASES_TABLE);
        builder.setProjectionMap(mColumnMap);

        //Cursor cursor = builder.query(this.getReadableDatabase(),
          //      columns, selection, selectionArgs, null, null, null);
        Cursor cursor = null;

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    /**
     * Builds a map for all columns that may be requested, which will be given to the
     * SQLiteQueryBuilder. This is a good way to define aliases for column names, but must include
     * all columns, even if the value is the key. This allows the ContentProvider to request
     * columns w/o the need to know real column names and create the alias itself.
     */
    private static HashMap<String,String> buildColumnMap() {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put(PhrasesDao.KEY_TRANSLATION, PhrasesDao.KEY_TRANSLATION);
        map.put(PhrasesDao.KEY_ATESO_PHRASE, PhrasesDao.KEY_ATESO_PHRASE);
        map.put(BaseColumns._ID, "rowid AS " +
                BaseColumns._ID);
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        return map;
    }

    /**
     * Returns a Cursor positioned at the word specified by rowId
     *
     * @param rowId id of phrase to retrieve
     * @param //columns The columns to include, if null then all are included
     * @return Cursor positioned to matching phrase, or null if not found.
     */
    public Cursor getPhrase(String rowId) {
        String selection = "rowid = ?";
        String[] selectionArgs = new String[] {rowId};
        Log.e(TAG, "Search rowid = "+rowId);

        //new getPhraseAsyncTask(mPhrasesDao).execute(rowId);

        Log.e(TAG, "GetPhrase in repo = "+mPhrasesDao.getPhrase(rowId).getCount());
        Cursor cursor1 = mPhrasesDao.getPhrase(rowId);

        if (cursor1 == null) {
            return null;
        } else if (!cursor1.moveToFirst()) {
            cursor1.close();
            return null;
        }
        return cursor1;
        //return query(selection, selectionArgs, columns);

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE rowid = <rowId>
         */
    }

    public Cursor getAllPhrases(String[] columns) {
        Cursor cursor;
        cursor = query(null, null, columns);
        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE rowid = <rowId>
         */
    }

    private static class getWordMatchesAsyncTask extends AsyncTask<String, Void, Cursor>{

        private PhrasesDao mPhrasesDao;

        getWordMatchesAsyncTask(PhrasesDao dao){
            mPhrasesDao = dao;
        }

        @Override
        protected Cursor doInBackground(final String... params){
            return mPhrasesDao.getWordMatches(params[0]+"*");
        }

        @Override
        protected void onPostExecute(Cursor cursor1) {
            super.onPostExecute(cursor1);
            cursor = cursor1;
            Log.e(TAG, "matches counted = "+ cursor.getCount());

        }
    }

}
