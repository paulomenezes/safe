package menezes.paulo.safe.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
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
import menezes.paulo.safe.adapter.ListTipItemAdapter;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.data.Session;
import menezes.paulo.safe.entity.Friend;
import menezes.paulo.safe.entity.Tip;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class MainTipsFriends  extends SherlockFragment {
	MobileServiceClient mClient;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    final View view = inflater.inflate(R.layout.fragment_tips, container, false);
	    
	    mClient = Connect.getInstance();
	    mClient.getTable(Tip.class).execute(new TableQueryCallback<Tip>() {
			@Override
			public void onCompleted(final List<Tip> tips, int i, Exception e, ServiceFilterResponse response) {
				final ListView list = (ListView) view.findViewById(R.id.list);
				final ProgressBar loading = (ProgressBar) view.findViewById(R.id.loading);
				final TextView msg = (TextView) view.findViewById(R.id.msg);
				
				loading.setVisibility(View.GONE);
				if(e == null && tips.size() > 0) {
					mClient.getTable(Friend.class).where().field("idUser1").eq(Session.getInt("userID")).execute(new TableQueryCallback<Friend>() {
						@Override
						public void onCompleted(List<Friend> friends, int j, Exception ex, ServiceFilterResponse filter) {
							if(friends != null) {
								final List<Tip> alertPlaces = new ArrayList<Tip>();
	
								for (int k = 0; k < tips.size(); k++) {
									for (int l = 0; l < friends.size(); l++) {
										if(tips.get(k).idUser == friends.get(l).idUser2) {
											alertPlaces.add(tips.get(k));
										}
									}
								}
								
								list.setAdapter(new ListTipItemAdapter(getActivity(), alertPlaces));
								
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
