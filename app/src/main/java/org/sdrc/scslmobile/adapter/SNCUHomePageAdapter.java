package org.sdrc.scslmobile.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.sdrc.scslmobile.fragment.IntermediateFragment;
import org.sdrc.scslmobile.fragment.OutcomeFragment;
import org.sdrc.scslmobile.fragment.ProcessFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jagat Bandhu Sahoo(jagat@sdrc.co.in) on 4/24/2017.
 */

public class SNCUHomePageAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
   private final List<String> mFragmentTitleList = new ArrayList<>();

    public SNCUHomePageAdapter(FragmentManager manager) {
        super(manager);

    }

    @Override
    public Fragment getItem(int position) {
       return  mFragmentList.get(position);

    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}

