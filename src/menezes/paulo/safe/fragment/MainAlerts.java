package menezes.paulo.safe.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import menezes.paulo.safe.PlaceActivity;
import menezes.paulo.safe.R;
import menezes.paulo.safe.adapter.ListPlaceItemAdapter;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.entity.Place;
import menezes.paulo.safe.entity.Report;
import menezes.paulo.safe.util.PlaceAlertComparator;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class MainAlerts extends SherlockFragment {
	MobileServiceClient mClient;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_alerts, container, false);
		
		mClient = Connect.getInstance();
	    mClient.getTable(Place.class).execute(new TableQueryCallback<Place>() {
			@Override
			public void onCompleted(final List<Place> places, int i, Exception e, ServiceFilterResponse response) {
				final ListView list = (ListView) view.findViewById(R.id.list);
				final ProgressBar loading = (ProgressBar) view.findViewById(R.id.loading);
				final TextView msg = (TextView) view.findViewById(R.id.msg);
				
				loading.setVisibility(View.GONE);
				if(e == null && places.size() > 0) {
					mClient.getTable(Report.class).execute(new TableQueryCallback<Report>() {
						@Override
						public void onCompleted(List<Report> reports, int j, Exception ex, ServiceFilterResponse filter) {
							if(reports != null) {
								final List<Pair<Integer, Place>> alertPlaces = new ArrayList<Pair<Integer,Place>>();
	
								for (int k = 0; k < places.size(); k++) {
									int place = 0;
									for (int l = 0; l < reports.size(); l++) {
										if(places.get(k).id == reports.get(l).idPlace) {
											place++;
										}
									}
									
									alertPlaces.add(new Pair<Integer, Place>(place, places.get(k)));
								}
								
								Collections.sort(alertPlaces, new PlaceAlertComparator());
								Collections.reverse(alertPlaces);
								
								final List<Place> finalPlaces = new ArrayList<Place>();
								for (int k = 0; k < alertPlaces.size(); k++) {
									finalPlaces.add(alertPlaces.get(k).second);
								}
								
								list.setAdapter(new ListPlaceItemAdapter(getActivity(), finalPlaces));
								list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
									@Override
				                    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
										Intent main = new Intent(getActivity(), PlaceActivity.class);
										Bundle b = new Bundle();
										b.putSerializable("place", finalPlaces.get(position));
										main.putExtras(b);
										
										startActivity(main);
				                    }
								});
								
								list.setVisibility(View.VISIBLE);
							} else {
								list.setVisibility(View.GONE);
								msg.setVisibility(View.VISIBLE);
								msg.setText("Sem alertas.");
							}
						}
					});
				} else {
					list.setVisibility(View.GONE);
					msg.setVisibility(View.VISIBLE);
					msg.setText("Sem alertas.");
				}
			}
		});
		
		return view;
	}
}