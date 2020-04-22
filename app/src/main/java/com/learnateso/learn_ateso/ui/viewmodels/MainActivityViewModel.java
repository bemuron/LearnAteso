package com.learnateso.learn_ateso.ui.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import android.database.Cursor;
import android.util.Log;

import com.learnateso.learn_ateso.data.AtesoRepository;
import com.learnateso.learn_ateso.models.User;

import java.util.HashMap;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by BE on 2/3/2018.
 */

public class MainActivityViewModel extends AndroidViewModel {

    //private member variable to hold reference to the repository
    private AtesoRepository mRepository;

    private HashMap<String, String> mUser;

    private Cursor userDetailsCursor;

    //constructor that gets a reference to the repository and gets the user details
    public MainActivityViewModel(Application application) {
        super(application);
        mRepository = new AtesoRepository(application);
    }

    //a getter method for the user. This hides the implementation from the UI
    /*
    HashMap<String, String> getUserDetails(){
        return mUser;
    }
*/
    public HashMap<String, String> getUserDetails() {
        mUser = new HashMap<String, String>();

        userDetailsCursor = mRepository.getUser();
        // Move to first row
        userDetailsCursor.moveToFirst();
        if (userDetailsCursor.getCount() > 0) {
            mUser.put("name", userDetailsCursor.getString(1));
            mUser.put("email", userDetailsCursor.getString(2));
            mUser.put("uid", userDetailsCursor.getString(3));
            mUser.put("created_at", userDetailsCursor.getString(4));
        }
        userDetailsCursor.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + mUser.toString());

        return mUser;
    }

    //a wrapper insert() method that calls the Repository's insert() method. In this way,
    // the implementation of insert() is completely hidden from the UI.
    public void insert(User user) { mRepository.insertUser(user); }

    public void delete() { mRepository.deleteUser(); }

    //get the number of phrases in the db
    /*
    public int NumberOfPhrases(){
        return mRepository.countAllPhrases();
    }
    */


}
