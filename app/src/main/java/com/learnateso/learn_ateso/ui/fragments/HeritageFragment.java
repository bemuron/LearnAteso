package com.learnateso.learn_ateso.ui.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learnateso.learn_ateso.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HeritageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HeritageFragment extends Fragment {
    private static final String TAG = HeritageFragment.class.getSimpleName();
    private static final String FRAGMENT_NAME = "fragment_name";

    private String mFragmentName;

    public HeritageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fragmentName Name of the category we are in.
     * @return A new instance of fragment EventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventsFragment newInstance(String fragmentName) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_NAME, fragmentName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFragmentName = getArguments().getString(FRAGMENT_NAME);
        }

        try {
            //set the name of this fragment in the toolbar
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mFragmentName);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            //set the name of this fragment in the toolbar
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mFragmentName);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        //sectionsViewModel.getSections(getArguments().getString("catname"));
        //Toast.makeText(getActivity(), ""+getArguments().getInt("category_id"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_heritage, container, false);
    }
}
