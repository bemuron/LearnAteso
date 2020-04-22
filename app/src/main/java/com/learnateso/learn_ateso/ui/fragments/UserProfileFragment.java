package com.learnateso.learn_ateso.ui.fragments;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.ui.viewmodels.CategoriesViewModel;

/**
 * Created by BE on 2/13/2018.
 */

public class UserProfileFragment extends Fragment {

    private CategoriesViewModel mCategoriesViewModel;

    public UserProfileFragment(){

    }

    @Override
    public void onResume() {
        super.onResume();
        //mCategoriesViewModel.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_profile_fragment, container, false);

        /*
        Use ViewModelProviders to associate your ViewModel with your UI controller.
        When your app first starts, the ViewModelProviders will create the ViewModel.
        When the activity is destroyed, for example through a configuration change,
        the ViewModel persists. When the activity is re-created, the ViewModelProviders
        return the existing ViewModel
         */
        mCategoriesViewModel = ViewModelProviders.of
                ((AppCompatActivity)getActivity()).get(CategoriesViewModel.class);

        return rootView;
    }

}
