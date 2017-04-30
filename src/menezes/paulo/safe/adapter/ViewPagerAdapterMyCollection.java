package menezes.paulo.safe.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import menezes.paulo.safe.fragment.MainMyCollectionPlaces;
import menezes.paulo.safe.fragment.MainMyCollectionReports;
import menezes.paulo.safe.fragment.MainMyCollectionTips;

public class ViewPagerAdapterMyCollection extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String titles[] = new String[] { "Denúncias", "Lugares", "Dicas" };
 
    public ViewPagerAdapterMyCollection(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int position) {
        switch (position) {
	        case 0:
	            return new MainMyCollectionReports();
	        case 1:
	            return new MainMyCollectionPlaces();
	        case 2:
	        	return new MainMyCollectionTips();
        }
        return null;
    }
 
    @Override
	public CharSequence getPageTitle(int position) {
        return titles[position];
    }
 
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
