package menezes.paulo.safe.adapter;

import menezes.paulo.safe.fragment.MainTipsAnvisa;
import menezes.paulo.safe.fragment.MainTipsFriends;
import menezes.paulo.safe.fragment.MainTipsPopular;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapterTips extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String titles[] = new String[] { "Anvisa", "Seguidores", "Populares" };
 
    public ViewPagerAdapterTips(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int position) {
        switch (position) {
	        case 0:
	            return new MainTipsAnvisa();
	        case 1:
	            return new MainTipsFriends();
	        case 2:
	        	return new MainTipsPopular();
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
