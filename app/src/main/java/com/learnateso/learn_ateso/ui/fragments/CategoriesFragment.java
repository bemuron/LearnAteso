package com.learnateso.learn_ateso.ui.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learnateso.learn_ateso.models.Category;
import com.learnateso.learn_ateso.ui.adapters.CategoryGridAdapter;
import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.ui.activities.CategorySectionsActivity;
import com.learnateso.learn_ateso.ui.viewmodels.CategoriesViewModel;

import java.util.List;

/**
 * Created by BE on 2/12/2018.
 */

public class CategoriesFragment extends Fragment implements
        CategoryGridAdapter.CategoryGridAdapterOnItemClickHandler {
    private static final String TAG = CategoriesFragment.class.getSimpleName();

    public static final String ATESO_QUIZ = "Ateso_Quiz";
    private static final String FRAGMENT_NAME = "fragment_name";

    private TextView textView;
    private String mFragmentName;

    private RecyclerView mRecyclerView;

    private CategoriesViewModel mCategoriesViewModel;
    private static boolean isAtesoQuiz;

    private CategoryGridAdapter categoryGridAdapter;
    private List<Category> categoryArrayList; //= new ArrayList<Category>();

    public CategoriesFragment(){

    }

    public static CategoriesFragment newInstance(Boolean isQuiz) {

        Bundle arguments = new Bundle();
        arguments.putBoolean(ATESO_QUIZ, isQuiz);
        CategoriesFragment fragment = new CategoriesFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isAtesoQuiz = getArguments().getBoolean(ATESO_QUIZ);
        }

        try {
            //set the name of this fragment in the toolbar
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Learn Ateso");
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //mCategoriesViewModel.start();
        try {
            //set the name of this fragment in the toolbar
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Learn Ateso");
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.categories_fragment,container,false);

        setupGridAdapter(rootView);

        /*
        Use ViewModelProviders to associate your ViewModel with your UI controller.
        When your app first starts, the ViewModelProviders will create the ViewModel.
        When the activity is destroyed, for example through a configuration change,
        the ViewModel persists. When the activity is re-created, the ViewModelProviders
        return the existing ViewModel
         */
        mCategoriesViewModel = ViewModelProviders.of
                (getActivity()).get(CategoriesViewModel.class);

        /*
        an observer for the LiveData returned by getAllCategories().
        The onChanged() method fires when the observed data changes and the activity is
        in the foreground.
        */
        mCategoriesViewModel.getAllCategories().observe(getActivity(),
                new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable final List<Category> categories) {
                // Update the cached copy of the categories in the adapter.
                categoryArrayList = categories;
                categoryGridAdapter.setList(categories);
            }
        });
        return  rootView;
    }

    private void setupGridAdapter(View view){
        //gridView = view.findViewById(R.id.gridview);
        mRecyclerView = view.findViewById(R.id.recyclerview_categories);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        //mRecyclerView.setHasFixedSize(true);

        //mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        categoryGridAdapter = new CategoryGridAdapter(
                categoryArrayList,getActivity(),this);

        mRecyclerView.setAdapter(categoryGridAdapter);
        //onGridItemClickListener(gridView);
    }

    /**
     * This method is for responding to clicks on our grid list.
     *
     * @param position id of category selected
     */
    @Override
    public void onItemClick(int position){
        Category category = categoryArrayList.get(position);

        Intent intent = new Intent(getActivity(), CategorySectionsActivity.class);
        intent.putExtra("category_id", category.getCategoryId());
        intent.putExtra("category_name", category.getCategoryName());
        if (isAtesoQuiz) {
            intent.putExtra("Ateso_Quiz", true);
        }else {
            intent.putExtra("Ateso_Quiz", false);
        }
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
}
