package com.learnateso.learn_ateso.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.models.Phrase;

import java.util.List;

public class PhraseSearchResultsAdapter extends ArrayAdapter {
    private List<Phrase> phraseSearchList;
    private Context mContext;
    private int searchResultItemLayout;

    public PhraseSearchResultsAdapter(Context context, int resource,
                                List<Phrase> storeSourceDataLst) {
        super(context, resource, storeSourceDataLst);
        phraseSearchList = storeSourceDataLst;
        mContext = context;
        searchResultItemLayout = resource;
    }

    @Override
    public int getCount() {
        return phraseSearchList.size();
    }

    @Override
    public Phrase getItem(int position) {
        return phraseSearchList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(searchResultItemLayout, parent, false);
        }

        Phrase phrase = getItem(position);

        TextView englishPhrase = view.findViewById(R.id.result_header);
        englishPhrase.setText(phrase.getTranslation());

        TextView atesoPhrase = view.findViewById(R.id.result_sub);
        atesoPhrase.setText(phrase.getAtesoPhrase());

        return view;
    }
}
