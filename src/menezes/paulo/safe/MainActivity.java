package menezes.paulo.safe;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import menezes.paulo.safe.adapter.MenuListAdapter;
import menezes.paulo.safe.fragment.AddReportDialog;
import menezes.paulo.safe.fragment.MainAlerts;
import menezes.paulo.safe.fragment.MainHome;
import menezes.paulo.safe.fragment.MainMyCollection;
import menezes.paulo.safe.fragment.MainNextPlaces;
import menezes.paulo.safe.fragment.MainProfile;
import menezes.paulo.safe.fragment.MainSettings;
import menezes.paulo.safe.fragment.MainTips;
import menezes.paulo.safe.fragment.MainUser;

import menezes.paulo.safe.R;

public class MainActivity extends SherlockFragmentActivity {

	DrawerLayout mDrawerLayout;
	ListView mDrawerList;
	ActionBarDrawerToggle mDrawerToggle;
	MenuListAdapter mMenuAdapter;
	
	String[] title;
	int[] icon;
	
	Fragment fragments[] = null;
	
	CharSequence mDrawerTitle;
	CharSequence mTitle;
	
	int mPage = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
		
		if(getIntent() != null) {
			Bundle b = getIntent().getExtras();
			if(b != null && b.getBoolean("addReport")) {
				FragmentManager fm = getSupportFragmentManager();
		        AddReportDialog addReport = new AddReportDialog();
		        addReport.setArguments(b);
		        addReport.show(fm, "add_report");
			}
			
			if(b != null && b.getInt("page") == 1) {
				mPage = 1;
			}
		}
		
		if (fragments == null) {
			fragments = new Fragment[8];
					 
			fragments[0] = new MainHome();
			fragments[1] = new MainMyCollection();
			fragments[2] = new MainAlerts();
			fragments[3] = new MainNextPlaces();
			fragments[4] = new MainTips();
			fragments[5] = new MainProfile();
			fragments[6] = new MainUser();
			fragments[7] = new MainSettings();
		}
		
		mTitle = mDrawerTitle = getTitle();
		title = new String[] { "Inicio", "Minha Coleção", "Alertas", "Locais Próximo", "Dicas", "Perfil", "Usuários", "Configurações" };
		icon = new int[] { 
				R.drawable.ic_action_safe, R.drawable.ic_action_collection, R.drawable.ic_action_warning, 
				R.drawable.ic_action_map, R.drawable.ic_action_labels, R.drawable.ic_action_person,
				R.drawable.ic_action_group, R.drawable.ic_action_settings };
		
		mMenuAdapter = new MenuListAdapter(this, title, icon);
		
		mDrawerList = (ListView)findViewById(R.id.listview_drawer);
		mDrawerList.setAdapter(mMenuAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(R.drawable.ic_action_safe);
		
		mDrawerToggle = new ActionBarDrawerToggle(
				this, mDrawerLayout, R.drawable.ic_drawer, 
				R.string.drawer_open, R.string.drawer_close) {
			
			@Override
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}
			
			@Override
			public void onDrawerOpened(View view) {
				getSupportActionBar().setTitle(mDrawerTitle);
				super.onDrawerClosed(view);
			}
		};
		
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		if(savedInstanceState == null) {
			selectItem(mPage);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home) {
			if(mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}
	
	private void selectItem(int position) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.content_frame, fragments[position]);
		ft.commit();
		
		mDrawerList.setItemChecked(position, true);
		
		setTitle(title[position]);
		
		mDrawerLayout.closeDrawer(mDrawerList);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	    mDrawerToggle.syncState();
    }
	 
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	    mDrawerToggle.onConfigurationChanged(newConfig);
    }
	 
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
	    getSupportActionBar().setTitle(mTitle);
    }
	 
	@Override
	public void onBackPressed() {
		FragmentManager manager = getSupportFragmentManager();
	    if (manager.getBackStackEntryCount() > 0) {
	    	manager.popBackStack();
        } else {
	        super.onBackPressed();
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.main, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem addReportItem = menu.getItem(0);
		addReportItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				FragmentManager fm = getSupportFragmentManager();
		        AddReportDialog addReport = new AddReportDialog();
		        addReport.show(fm, "add_report");
		        
				return false;
			}
		});
		
		return super.onPrepareOptionsMenu(menu);
	}
}