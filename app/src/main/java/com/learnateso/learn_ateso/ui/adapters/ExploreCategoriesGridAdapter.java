package com.learnateso.learn_ateso.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.models.Category;
import com.learnateso.learn_ateso.models.ExploreCategory;

import java.util.List;
import java.util.Locale;

/**
 * Created by BE on 2/3/2018.
 */

public class ExploreCategoriesGridAdapter extends RecyclerView.Adapter<ExploreCategoriesGridAdapter.MyViewHolder> {

    private final GridAdapterOnItemClickHandler mClickHandler;

    private Context mContext;
    private LayoutInflater inflater;
    //private final CategoriesViewModel mCategoriesViewModel;
    private List<ExploreCategory> mCategories;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView categoryName;
        //public LinearLayout itemContainer;
        public ConstraintLayout itemContainer;

        public MyViewHolder(View view) {
            super(view);
            categoryName = view.findViewById(R.id.explore_name);
            imageView = view.findViewById(R.id.explore_pic);
            itemContainer = view.findViewById(R.id.exploreGridItemContainer);
            //view.setOnLongClickListener(this);
        }
    }

    public ExploreCategoriesGridAdapter(List<ExploreCategory> categories, Context c,
                                        GridAdapterOnItemClickHandler clickHandler){
        this.mContext = c;
        inflater = LayoutInflater.from(c);
        this.mClickHandler = clickHandler;
        this.mCategories = categories;


        //mCategoriesViewModel = categoriesViewModel;
        //setList(categories);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ExploreCategory category = mCategories.get(position);

        //displaying the category info
        holder.categoryName.setText(category.getExploreName());
        String imageName = category.getExploreImage().toLowerCase(Locale.US);
        int imageId = mContext.getResources().getIdentifier(imageName,
                "drawable", mContext.getPackageName());
        holder.imageView.setImageResource(imageId);

        //apply click events
        applyClickEvents(holder, position);
    }

    //handling different click events
    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.itemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickHandler.onItemClick(position);
            }
        });
    }

    @Override
    public long getItemId(int i) {
        return mCategories.get(i).getExploreId();
    }

    @Override
    public int getItemCount() {
        //return mCategories.size();
        return mCategories != null ? mCategories.size() : 0;
    }

    public void setList(List<ExploreCategory> categories) {
        this.mCategories = categories;
        notifyDataSetChanged();
    }

    /**
     * The interface that receives onItemClick messages.
     */
    public interface GridAdapterOnItemClickHandler {
        void onItemClick(int position);
    }

}
