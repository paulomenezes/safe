package menezes.paulo.safe.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import menezes.paulo.safe.EditProfileActivity;
import menezes.paulo.safe.FriendsActivity;
import menezes.paulo.safe.R;
import menezes.paulo.safe.ReportActivity;
import menezes.paulo.safe.adapter.ListReportItemAdapter;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.data.Session;
import menezes.paulo.safe.entity.Friend;
import menezes.paulo.safe.entity.Place;
import menezes.paulo.safe.entity.Report;
import menezes.paulo.safe.entity.Tip;
import menezes.paulo.safe.entity.User;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class MainProfile extends Fragment {

	private User mUser;
	private MobileServiceClient mClient;
    private ProgressDialog mLoading;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
		
		setHasOptionsMenu(true);
		
		mLoading = ProgressDialog.show(getActivity(), "", "Carregando usuário, aguarde...");
		
		mClient = Connect.getInstance();
		mClient.getTable(User.class).where().field("id").eq(Session.getInt("userID")).execute(new TableQueryCallback<User>() {
			@Override
			public void onCompleted(List<User> users, int i, Exception e, ServiceFilterResponse response) {
				if(e == null) {
					mUser = users.get(i);
					
					ImageView photo = (ImageView)rootView.findViewById(R.id.photo);
					
					mUser.getPhoto(photo, (TextView)rootView.findViewById(R.id.photo_name));
					
					((LinearLayout) rootView.findViewById(R.id.editProfile)).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							startActivity(new Intent(getActivity(), EditProfileActivity.class));
						}
					});
					
					((TextView)rootView.findViewById(R.id.photo_name)).setText(String.valueOf(mUser.firstname.toCharArray()[0]) + String.valueOf(mUser.lastname.toCharArray()[0]));
					((TextView)rootView.findViewById(R.id.name)).setText(mUser.getName());
					((TextView)rootView.findViewById(R.id.biografy)).setText(mUser.biografy != null ? mUser.biografy : "Adicione uma biografia");
					((TextView)rootView.findViewById(R.id.phone)).setText(mUser.phone != null ? "Telefone: " + mUser.phone : "Adicione um telefone");
					
					mClient.getTable(Report.class).where().field("idUser").eq(mUser.id).execute(new TableQueryCallback<Report>() {
						@Override
						public void onCompleted(final List<Report> reports, int i, Exception e, ServiceFilterResponse response) {
							((ProgressBar) rootView.findViewById(R.id.reportLoading)).setVisibility(View.GONE);
							
							if(e == null && reports.size() > 0) {
								((TextView)rootView.findViewById(R.id.reports)).setText(String.valueOf(reports.size()));
								((LinearLayout) rootView.findViewById(R.id.reportsLayout)).setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										startActivity(getActivity().getIntent().putExtra("page", 1));
										getActivity().finish();	
									}
								});
								
								ListView listContent = (ListView)rootView.findViewById(R.id.listReports);
								listContent.setVisibility(View.VISIBLE);
								
								listContent.setAdapter(new ListReportItemAdapter(getActivity(), reports));
								listContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
									@Override
				                    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
										Intent main = new Intent(getActivity(), ReportActivity.class);
										Bundle b = new Bundle();
										b.putSerializable("report", reports.get(position));
										main.putExtras(b);
										
										startActivity(main);
				                    }
								});
							} else {
								((TextView)rootView.findViewById(R.id.reports)).setText("0");
								((TextView) rootView.findViewById(R.id.reportMsg)).setVisibility(View.VISIBLE);
							}
							
							((TextView) rootView.findViewById(R.id.reports)).setVisibility(View.VISIBLE);
							((ProgressBar) rootView.findViewById(R.id.reportsLoading)).setVisibility(View.GONE);
						}
					});
					
					mLoading.dismiss();
				}
			}
		});
		
		mClient.getTable(Friend.class).where().field("idUser1").eq(Session.getInt("userID")).execute(new TableQueryCallback<Friend>() {
			@Override
			public void onCompleted(final List<Friend> friends, int i, Exception e, ServiceFilterResponse response) {
				if(e == null) {
					if(friends.size() > 0) {
						((LinearLayout) rootView.findViewById(R.id.followingLayout)).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent friend = new Intent(getActivity(), FriendsActivity.class);
								Bundle b = new Bundle();
								b.putInt("tipo", 1);
								b.putSerializable("friends", new ArrayList<Friend>(friends));
								friend.putExtras(b);
								startActivity(friend);
							}
					});
					}
					
					((TextView) rootView.findViewById(R.id.following)).setText(String.valueOf(friends.size()));
				} else {
					((TextView) rootView.findViewById(R.id.following)).setText("0");
				}
				
				((TextView) rootView.findViewById(R.id.following)).setVisibility(View.VISIBLE);
				((ProgressBar) rootView.findViewById(R.id.followingLoading)).setVisibility(View.GONE);
			}
		});
		
		mClient.getTable(Friend.class).where().field("idUser2").eq(Session.getInt("userID")).execute(new TableQueryCallback<Friend>() {
			@Override
			public void onCompleted(final List<Friend> friends, int i, Exception e, ServiceFilterResponse response) {
				if(e == null) {
					if(friends.size() > 0) {
						((LinearLayout) rootView.findViewById(R.id.followersLayout)).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent friend = new Intent(getActivity(), FriendsActivity.class);
								Bundle b = new Bundle();
								b.putInt("tipo", 2);
								b.putSerializable("friends", new ArrayList<Friend>(friends));
								friend.putExtras(b);
								startActivity(friend);
							}
						});
					}
					
					((TextView) rootView.findViewById(R.id.followers)).setText(String.valueOf(friends.size()));
				} else {
					((TextView) rootView.findViewById(R.id.followers)).setText("0");
				}
				
				((TextView) rootView.findViewById(R.id.followers)).setVisibility(View.VISIBLE);
				((ProgressBar) rootView.findViewById(R.id.followersLoading)).setVisibility(View.GONE);
			}
		});
	
		((TextView) rootView.findViewById(R.id.trophies)).setText("0");
		((LinearLayout) rootView.findViewById(R.id.trophiesLayout)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Em breve!", Toast.LENGTH_LONG).show();
			}
		});

		mClient.getTable(Place.class).where().field("idUser").eq(Session.getInt("userID")).execute(new TableQueryCallback<Place>() {
			@Override
			public void onCompleted(final List<Place> places, int i, Exception e, ServiceFilterResponse response) {
				if(e == null) {
					mClient.getTable(Tip.class).where().field("idUser").eq(Session.getInt("userID")).execute(new TableQueryCallback<Tip>() {
						@Override
						public void onCompleted(final List<Tip> tips, int j, Exception e, ServiceFilterResponse response) {
							if(e == null) {
								mClient.getTable(Report.class).where().field("idUser").eq(Session.getInt("userID")).execute(new TableQueryCallback<Report>() {
									@Override
									public void onCompleted(List<Report> reports, int k, Exception e, ServiceFilterResponse response) {
										if(e == null) {
											((LinearLayout) rootView.findViewById(R.id.collectionLayout)).setOnClickListener(new View.OnClickListener() {
												@Override
												public void onClick(View v) {
													startActivity(getActivity().getIntent().putExtra("page", 1));
													getActivity().finish();
												}
											});
											
											((TextView) rootView.findViewById(R.id.collection)).setText(String.valueOf(places.size() + tips.size() + reports.size()));
											((TextView) rootView.findViewById(R.id.collection)).setVisibility(View.VISIBLE);
											((ProgressBar) rootView.findViewById(R.id.collectionLoading)).setVisibility(View.GONE);
										} else {
											((TextView) rootView.findViewById(R.id.collection)).setText(String.valueOf("0"));
											((TextView) rootView.findViewById(R.id.collection)).setVisibility(View.VISIBLE);
											((ProgressBar) rootView.findViewById(R.id.collectionLoading)).setVisibility(View.GONE);
										}
									}
								});
							} else {
								((TextView) rootView.findViewById(R.id.collection)).setText(String.valueOf("0"));
								((TextView) rootView.findViewById(R.id.collection)).setVisibility(View.VISIBLE);
								((ProgressBar) rootView.findViewById(R.id.collectionLoading)).setVisibility(View.GONE);
							}
						}
					});
				} else {
					((TextView) rootView.findViewById(R.id.collection)).setText(String.valueOf("0"));
					((TextView) rootView.findViewById(R.id.collection)).setVisibility(View.VISIBLE);
					((ProgressBar) rootView.findViewById(R.id.collectionLoading)).setVisibility(View.GONE);
				}
			}
		});
		
		return rootView;
	}
}