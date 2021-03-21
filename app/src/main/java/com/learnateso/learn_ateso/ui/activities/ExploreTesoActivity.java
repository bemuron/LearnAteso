package com.learnateso.learn_ateso.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.models.Category;
import com.learnateso.learn_ateso.models.ExploreCategory;
import com.learnateso.learn_ateso.ui.adapters.CategoryGridAdapter;
import com.learnateso.learn_ateso.ui.adapters.ExploreCategoriesGridAdapter;
import com.learnateso.learn_ateso.ui.viewmodels.CategoriesViewModel;

import java.util.List;

public class ExploreTesoActivity extends AppCompatActivity implements ExploreCategoriesGridAdapter.GridAdapterOnItemClickHandler {

    private RecyclerView mRecyclerView;
    private List<ExploreCategory> categoryArrayList;
    private ExploreCategoriesGridAdapter categoryGridAdapter;
    private CategoriesViewModel mCategoriesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_teso);

        setupGridAdapter();

        mCategoriesViewModel = ViewModelProviders.of
                (this).get(CategoriesViewModel.class);

        mCategoriesViewModel.getExploreCategories().observe(this,
                new Observer<List<ExploreCategory>>() {
                    @Override
                    public void onChanged(@Nullable final List<ExploreCategory> categories) {
                        // Update the cached copy of the categories in the adapter.
                        categoryArrayList = categories;
                        categoryGridAdapter.setList(categories);
                    }
                });
    }

    private void setupGridAdapter(){
        mRecyclerView = findViewById(R.id.recyclerview_categories);

        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(this, 2);

        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        //mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        categoryGridAdapter = new ExploreCategoriesGridAdapter(
                categoryArrayList, this,this);

        mRecyclerView.setAdapter(categoryGridAdapter);
        //onGridItemClickListener(gridView);
    }

    @Override
    public void onItemClick(int position) {
        ExploreCategory category = categoryArrayList.get(position);

        Intent intent = new Intent(this, CategorySectionsActivity.class);
        intent.putExtra("category_id", category.getExploreId());
        intent.putExtra("category_name", category.getExploreName());
        startActivity(intent);
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
