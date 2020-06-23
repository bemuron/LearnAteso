package com.learnateso.learn_ateso.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.models.Phrase;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class PhrasesExpandableListAdapter extends BaseExpandableListAdapter {
    private static final String TAG = PhrasesExpandableListAdapter.class.getSimpleName();
    private Context context;
    private List<String> expandableListTitle;
    private LinkedHashMap<String, List<Phrase>> expandableListDetail;
    private String englishPhrase;
    private int groupPosition;
    private PhrasesExpandableListAdapterListener listener;
    private ImageView notFav, inFavs, iconImp, phrasePic;

    public  PhrasesExpandableListAdapter(Context context, List<String> expandableListTitle,
                                         LinkedHashMap<String, List<Phrase>> expandableListDetail,
                                         PhrasesExpandableListAdapterListener listener){
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.listener = listener;
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public int getChildrenCount(int listPosition) {

        return this.expandableListDetail.get(
                this.expandableListTitle.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {

        return this.expandableListDetail.get(
                this.expandableListTitle.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //Phrase listTitle = (Phrase)getGroup(listPosition);
        String listTitle = (String)getGroup(listPosition);
        englishPhrase = (String)getGroup(listPosition);
        groupPosition = listPosition;
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.phrase_headers, null);
        }

        TextView listTitleTextView = convertView.findViewById(R.id.english_phrase);

        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        //final String expandedListText = (String)getChild(listPosition, expandedListPosition);

        final Phrase expandedListText = (Phrase)getChild(listPosition, expandedListPosition);

        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.phrase_list_item, null);
        }

        //text view for the ateso phrase
        TextView expandedListTextView = convertView.findViewById(R.id.atesoPhraseHeader);
        expandedListTextView.setText(expandedListText.getAtesoPhrase());

        //favourites star
        iconImp = convertView.findViewById(R.id.icon_star);
        applyImportant(iconImp,expandedListText);
        iconImp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconImportantClicked(expandedListText, groupPosition);
            }
        });

        //get and display the phrase pic
        phrasePic = convertView.findViewById(R.id.phrase_pic);

        if (expandedListText.getPhrasePic() == null){
            Log.e(TAG,"Phrase image name = null");
            phrasePic.setVisibility(View.GONE);
        }else{
            phrasePic.setVisibility(View.VISIBLE);
            Log.e(TAG,"Phrase image name = "+expandedListText.getPhrasePic());
            String imageName = expandedListText.getPhrasePic().toLowerCase(Locale.US);
            int imageId = context.getResources().getIdentifier(imageName,
                    "drawable", context.getPackageName());
            phrasePic.setImageResource(imageId);
        }

        //share imageview and click listener
        ImageView share = convertView.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                shareIt(englishPhrase,expandedListText.getAtesoPhrase());
                //Toast.makeText(getApplicationContext(), englishPhrase + expandedListText.getAtesoPhrase(),
                //      Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    //code for sharing the words with other apps
    public void shareIt(String eWord, String aWord){
        Intent sharingIntent = new Intent();
        sharingIntent.setAction(Intent.ACTION_SEND);
        String shareBody = "\n Hey, I learnt how to say" +
                "\n\""+eWord+"\"" +
                "\n in Ateso: it is " +
                "\n\"" + aWord+"\"" +
                "\nDownload the Learn Ateso app from the Google Playstore.";
        //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void applyImportant(ImageView imageView, Phrase phrase) {
        int isFavourite = phrase.getIsFavourite();
        if (isFavourite == 1) {
            imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_black_24dp));
            imageView.setColorFilter(ContextCompat.getColor(context, R.color.icon_tint_selected));
        } else {
            imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_border_black_24dp));
            imageView.setColorFilter(ContextCompat.getColor(context, R.color.icon_tint_normal_black));
        }
    }

    public interface PhrasesExpandableListAdapterListener {
        //void onIconClicked(int position);

        void onIconImportantClicked(Phrase phrase, int position);

        /*void onMessageRowClicked(int position);

        void onRowLongClicked(int position);*/
    }

}
