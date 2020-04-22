package com.learnateso.learn_ateso.ui.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.models.Section;

import java.util.List;
import java.util.Locale;

/**
 * Created by BE on 2/15/2018.
 */

public class CategorySectionsAdapter extends RecyclerView.Adapter<CategorySectionsAdapter.MyViewHolder> {
    private static final String TAG = CategorySectionsAdapter.class.getSimpleName();

    public List<Section> mSectionslist;
    private LayoutInflater inflater;
    private SectionClickListener listener;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, count;
        public Button openPhrases, openQuiz;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.section_name);
            openPhrases = view.findViewById(R.id.open_phrases);
            openQuiz = view.findViewById(R.id.open_quiz);
            thumbnail = view.findViewById(R.id.section_pic);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            //listener.onMessageRowClicked(getAdapterPosition());
            //listener.onRowLongClicked(getAdapterPosition());
            //view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            //return true;
        }
    }

    //List<Section> categories

    public CategorySectionsAdapter(Context context, List<Section> sections, SectionClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
        this.mSectionslist = sections;

        //setList(sections);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        String imageName;
        int imageId;
        Section section = mSectionslist.get(position);

        holder.title.setText(section.getSectionName());

        imageName = section.getSectionImage().toLowerCase(Locale.US);
        imageId = context.getResources().getIdentifier(imageName,
                "drawable", context.getPackageName());

        // loading album cover using Glide library
        Glide.with(context).load(imageId).into(holder.thumbnail);

        // apply click events
        applyClickEvents(holder, position, section.getCategoryId(), section.getSectionId(), section.getSectionName());

    }

    //handling clicks on the different sections
    private void applyClickEvents(MyViewHolder holder, final int position,
                                  final int categoryId, final int sectionId, final String sectionName) {
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position, categoryId, sectionId, sectionName);
            }
        });

        holder.openPhrases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onOpenPhrasesClicked(position, categoryId, sectionId, sectionName);
            }
        });

        holder.openQuiz.setOnClickListener(view -> {
            listener.onStartQuizClicked(position, categoryId, sectionId, sectionName);
        });

    }

    public void setList(List<Section> section) {
        this.mSectionslist = section;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int i) {
        return mSectionslist.get(i).getSectionId();
    }

    @Override
    public int getItemCount() {
        Log.e(TAG, "size of section list: "+mSectionslist.size());
        return mSectionslist.size();
    }

    public interface SectionClickListener {
        void onIconClicked(int position, int categoryId, int sectionId, String sectionName);
        void onOpenPhrasesClicked(int position, int categoryId, int sectionId, String sectionName);
        void onStartQuizClicked(int position, int categoryId, int sectionId, String sectionName);

    }

}
