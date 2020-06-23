package com.learnateso.learn_ateso.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.data.database.PhrasesDao;
import com.learnateso.learn_ateso.models.Phrase;

import java.util.List;

public class PhraseSearchResultsCursorAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private SearchView searchView;

    public PhraseSearchResultsCursorAdapter(Context context, Cursor cursor, SearchView sv) {
        super(context, cursor, false);
        mContext = context;
        searchView = sv;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mLayoutInflater.inflate(R.layout.phrase_search_item, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String englishPhrase = cursor.getString(cursor.getColumnIndexOrThrow(PhrasesDao.KEY_TRANSLATION));
        String atesoPhrase = cursor.getString(cursor.getColumnIndexOrThrow(PhrasesDao.KEY_ATESO_PHRASE));

        TextView englishTv = view.findViewById(R.id.search_result_header);
        englishTv.setText(englishPhrase);

        TextView atesoTv = view.findViewById(R.id.search_result_sub);
        atesoTv.setText(atesoPhrase);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //take next action based user selected item
                //TextView dealText = (TextView) view.findViewById(R.id.tv_deal);
                searchView.setIconified(true);
                //Toast.makeText(context, "Selected suggestion "+dealText.getText(),
                //        Toast.LENGTH_LONG).show();

            }
        });

    }
}
