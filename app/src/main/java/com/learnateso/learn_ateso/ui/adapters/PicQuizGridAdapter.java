package com.learnateso.learn_ateso.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.learnateso.learn_ateso.helpers.CheckableImageButton;
import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.models.PicQuiz;

import java.util.List;
import java.util.Locale;

/**
 * Created by BE on 2/3/2018.
 */

public class PicQuizGridAdapter extends BaseAdapter {

    private final PicQuizGridAdapterOnItemClickHandler mClickHandler;

    private CheckableImageButton mCurrentButton;

    private Context mContext;
    //private final CategoriesViewModel mCategoriesViewModel;
    private List<PicQuiz> mPicQuiz;

    public PicQuizGridAdapter(List<PicQuiz> picQuiz, Context c,
                              PicQuizGridAdapterOnItemClickHandler clickHandler){
        this.mContext = c;
        this.mClickHandler = clickHandler;
        this.mPicQuiz = picQuiz;


        //mCategoriesViewModel = categoriesViewModel;
        //setList(categories);

    }
    @Override
    public int getCount() {
        return mPicQuiz != null ? mPicQuiz.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View rowView = view;
        String imageName;
        int imageId;
        ViewHolder viewHolder;
        if (rowView == null){
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

            rowView = inflater.inflate(R.layout.pic_quiz_grid_item,viewGroup,false);

            //configure view holder
            viewHolder = new ViewHolder();
            viewHolder.quizItemName = rowView.findViewById(R.id.quiz_pic_name);
            viewHolder.imageView = (CheckableImageButton) rowView.findViewById(R.id.checkable_image);
            viewHolder.itemContainer = rowView.findViewById(R.id.picQuizGridItemContainer);

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.quizItemName.setText(mPicQuiz.get(i).getmPicQuizItemName());

        //get the audio name  to obtain the audio id
        //audioName = mPicQuiz.get(i).getmAudioName();

        //get the image name and display the image
        imageName = mPicQuiz.get(i).getmPicImageName().toLowerCase(Locale.US);
        imageId = mContext.getResources().getIdentifier(imageName,
                "drawable", mContext.getPackageName());
        viewHolder.imageView.setImageResource(imageId);

        mCurrentButton = viewHolder.imageView;
        applyClickEvents(viewHolder, mPicQuiz.get(i).getmPicQuizItemName(),mCurrentButton);

        return rowView;
    }

    public void setList(List<PicQuiz> picQuiz) {
        this.mPicQuiz = picQuiz;
        notifyDataSetChanged();
    }

    /**
     * The interface that receives onItemClick messages.
     */
    public interface PicQuizGridAdapterOnItemClickHandler {
        void onItemClick(String itemName, CheckableImageButton button);
    }

    //handling different click events
    private void applyClickEvents(ViewHolder holder, final String itemname, final CheckableImageButton button) {
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickHandler.onItemClick(itemname, button);
            }
        });

        holder.imageView.setOnCheckedChangeListener(new CheckableImageButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CheckableImageButton button, boolean isChecked) {

                if (isChecked && mCurrentButton != button) {
                    mCurrentButton.setChecked(false);
                    mCurrentButton = button;

                }

            }
            });
    }

    public class ViewHolder implements View.OnClickListener{
        CheckableImageButton imageView;
        private TextView quizItemName;
        public LinearLayout itemContainer;
        private String audioName;

        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onItemClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v) {

            //int adapterPosition = getAdapterPosition();
            //Date date = mForecast.get(adapterPosition).getDate();
            //mClickHandler.onItemClick(date);
            //if (v == TextView){
             //mClickHandler.onItemClick(((TextView) v).getText().toString());
            //}
        }
    }

}
