package com.learnateso.learn_ateso.ui.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.ui.fragments.ArticlesFragment;
import com.learnateso.learn_ateso.ui.fragments.ProverbsFragment;

public class ExploreTabsPagerAdapter extends FragmentStateAdapter {

    @StringRes
    public static final int[] TAB_TITLES = new int[]{R.string.exp_tab_articles,
            R.string.exp_tab_proverbs};
    //R.string.exp_tab_riddles, R.string.exp_tab_videos,
    //            R.string.exp_tab_wise_sayings

    public ExploreTabsPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ArticlesFragment.newInstance("sample", "sample1");
            case 1:
                return ProverbsFragment.newInstance("sample 1", "sample2");
            /*default:
                return PostJobBudgetFragment.newInstance();*/
        }
        return null;
    }



    @Override
    public int getItemCount() {
        return 2;//Number of fragments displayed;
    }
}
