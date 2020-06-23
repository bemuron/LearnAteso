package com.learnateso.learn_ateso.ui.fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.ui.activities.PhraseListActivity;
import com.learnateso.learn_ateso.ui.viewmodels.SectionsViewModel;
import com.learnateso.learn_ateso.ui.activities.WorkBookActivity;
import com.learnateso.learn_ateso.ui.adapters.CategorySectionsAdapter;
import com.learnateso.learn_ateso.models.Section;
import com.learnateso.learn_ateso.ui.activities.CategorySectionsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BE on 2/15/2018.
 */

/**
 * Display a list of {@link //Sections}. User can choose one of them to start the exercise
 */
public class SectionsFragment extends Fragment implements CategorySectionsAdapter.SectionClickListener {

    private static final String TAG = SectionsFragment.class.getSimpleName();
    private View view;
    private static final String CATEGORY_NAME = "category_name";
    private static final String CATEGORY_ID = "category_id";
    private static final String ATESO_QUIZ = "Ateso_quiz";
    private CategorySectionsAdapter categorySectionsAdapter;
    private SectionsViewModel sectionsViewModel;
    private RecyclerView recyclerView;
    private String categoryName, sectionName;
    private boolean isAtesoQuiz;
    private int categoryId;
    private List<Section> sectionsList = new ArrayList<>();
    int [] section_images = { R.drawable.greet, R.drawable.greet, R.drawable.greet,
            R.drawable.greet};

    public SectionsFragment() {
        // Requires empty public constructor
    }

    public static SectionsFragment newInstance(int categoryId, String categoryName, boolean isQuiz) {

        Bundle arguments = new Bundle();
        arguments.putInt(CATEGORY_ID, categoryId);
        arguments.putString(CATEGORY_NAME, categoryName);
        SectionsFragment fragment = new SectionsFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getInt(CATEGORY_ID);
            categoryName = getArguments().getString(CATEGORY_NAME);
            isAtesoQuiz = getArguments().getBoolean(ATESO_QUIZ);
        }

        try {
            //set the name of this fragment in the toolbar
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(categoryName);
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
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(categoryName);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        //sectionsViewModel.getSections(getArguments().getString("catname"));
        //Toast.makeText(getActivity(), ""+getArguments().getInt("category_id"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.sections_fragment_list, container, false);

        getAllWidgets(view);

         /*
        Use ViewModelProviders to associate your ViewModel with your UI controller.
        When your app first starts, the ViewModelProviders will create the ViewModel.
        When the activity is destroyed, for example through a configuration change,
        the ViewModel persists. When the activity is re-created, the ViewModelProviders
        return the existing ViewModel
         */
        sectionsViewModel = ViewModelProviders.of
                (getActivity()).get(SectionsViewModel.class);

        /*
        an observer for the LiveData returned by getAllWords().
        The onChanged() method fires when the observed data changes and the activity is
        in the foreground.
        */

        sectionsViewModel.getSections(categoryId).observe(getActivity(),
                new Observer<List<Section>>() {
                    @Override
                    public void onChanged(@Nullable final List<Section> sections) {
                        sectionsList = sections;
                        // Update the cached copy of the words in the adapter.
                        Log.e(TAG, "section list size: "+sections.size());
                        categorySectionsAdapter.setList(sections);
                    }
                });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupListAdapter();

    }

    public void getAllWidgets(View view) {
        recyclerView = view.findViewById(R.id.sectionsListRecyclerView);
    }

    private void setupListAdapter() {

        categorySectionsAdapter = new CategorySectionsAdapter(CategorySectionsActivity.getInstance(),
                sectionsList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CategorySectionsActivity.getInstance());
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(CategorySectionsActivity.getInstance(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 10, false));
        recyclerView.setAdapter(categorySectionsAdapter);

    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onIconClicked(int position, int categoryId, int sectionId, String sectionName) {

        //Toast.makeText(getActivity(), "Pos = "+ position + ", Cat = "+ categoryName +
        //      ", sect = "+ sectionName, Toast.LENGTH_LONG).show();
        //sectionName = sectionsList.get(sectionId).getSectionName();
        /*if (isAtesoQuiz) {
            Intent intent = new Intent(CategorySectionsActivity.getInstance(), WorkBookActivity.class);
            intent.putExtra("categoryId", categoryId);
            intent.putExtra("sectionId", sectionId);
            intent.putExtra("sectionName", sectionName);
            intent.putExtra("categoryName", categoryName);
            Log.d(TAG, "sectionID = " + sectionId + " *** categoryID = " + categoryId);
            startActivity(intent);
        } else{

            //Intent intent = new Intent(CategorySectionsActivity.getInstance(), WorkBookActivity.class);
            Intent intent = new Intent(CategorySectionsActivity.getInstance(), PhraseListActivity.class);
            intent.putExtra("categoryId", categoryId);
            intent.putExtra("sectionId", sectionId);
            intent.putExtra("sectionName", sectionName);
            intent.putExtra("categoryName", categoryName);
            Log.d(TAG, "sectionID = " + sectionId + " *** categoryID = " + categoryId);
            startActivity(intent);
            //CategorySectionsActivity.getInstance().finish();
    }*/

        }

        //open phrases activity
    @Override
    public void onOpenPhrasesClicked(int position, int categoryId, int sectionId, String sectionName) {

        Intent intent = new Intent(CategorySectionsActivity.getInstance(), PhraseListActivity.class);
        intent.putExtra("categoryId", categoryId);
        intent.putExtra("sectionId", sectionId);
        intent.putExtra("sectionName", sectionName);
        intent.putExtra("categoryName", categoryName);
        Log.d(TAG, "sectionID = " + sectionId + " *** categoryID = " + categoryId);
        startActivity(intent);
    }

    //start quiz
    @Override
    public void onStartQuizClicked(int position, int categoryId, int sectionId, String sectionName) {

        Intent intent = new Intent(CategorySectionsActivity.getInstance(), WorkBookActivity.class);
        intent.putExtra("categoryId", categoryId);
        intent.putExtra("sectionId", sectionId);
        intent.putExtra("sectionName", sectionName);
        intent.putExtra("categoryName", categoryName);
        Log.d(TAG, "sectionID = " + sectionId + " *** categoryID = " + categoryId);
        startActivity(intent);
    }

}
