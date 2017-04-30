package menezes.paulo.safe;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import menezes.paulo.safe.adapter.ListPlaceItemAdapter;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.entity.Place;
import menezes.paulo.safe.fragment.AddPlaceDialog;
import menezes.dd.processbutton.FlatButton;
import com.microsoft.windowsazure.mobileservices.QueryOrder;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class PlaceSelectActivity extends SherlockFragmentActivity {

	private Drawable mActionBarBackgroundDrawable;
	private Bundle mBundle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_select);
		
		mBundle = getIntent().getExtras();
	
		this.mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.gradient);
		getActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);
		
		Connect.getInstance().getTable(Place.class).orderBy("id", QueryOrder.Descending).execute(new TableQueryCallback<Place>() {
			@Override
			public void onCompleted(final List<Place> places, int i, Exception e, ServiceFilterResponse response) {
				if(e == null) {
					if(places.size() > 0) {
						ListView listContent = (ListView)findViewById(R.id.listPlaces);
						listContent.setVisibility(View.VISIBLE);
						
						((ProgressBar)findViewById(R.id.listLoading)).setVisibility(View.GONE);
						
						listContent.setAdapter(new ListPlaceItemAdapter(PlaceSelectActivity.this, places));
						listContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
		                    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
								Intent main = new Intent(PlaceSelectActivity.this, MainActivity.class);
								
								if (mBundle == null) {
									mBundle = new Bundle();
								}
								
								mBundle.putBoolean("addReport", true);
								mBundle.putInt("placeID", places.get(position).id);
								mBundle.putString("placeName", places.get(position).name);
	
								main.putExtras(mBundle);
								startActivity(main);
		                    }
						});
					} else {
						((ProgressBar)findViewById(R.id.listLoading)).setVisibility(View.GONE);
						((TextView)findViewById(R.id.listPlacesNoResult)).setVisibility(View.VISIBLE);
					}
				}
			}
		});
		
		FlatButton addPlace = (FlatButton)findViewById(R.id.addPlaceButton);
		addPlace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FragmentManager fm = getSupportFragmentManager();
		        AddPlaceDialog addPlace = new AddPlaceDialog();
		        if(mBundle != null) {
		        	addPlace.setArguments(mBundle);
		        }
		        	
		        addPlace.show(fm, "add_place");
			}
		});
	}

    @Override
    public boolean onNavigateUp() {
        Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(nextScreen);

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(nextScreen);
    }
}