package menezes.paulo.safe.fragment;

import java.lang.reflect.Field;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import menezes.paulo.safe.R;
import menezes.paulo.safe.adapter.ViewPagerAdapterMyCollection;

public class MainMyCollection extends SherlockFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pages, container, false);
		ViewPager mViewPager = (ViewPager)view.findViewById(R.id.viewPager);
		mViewPager.setAdapter(new ViewPagerAdapterMyCollection(getChildFragmentManager()));
		
		return view;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (Exception e) {
			Log.e("error", "Error loading fragments");
		}
	}
}
