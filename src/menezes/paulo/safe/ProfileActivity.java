package menezes.paulo.safe;

import java.util.ArrayList;
import java.util.List;

import menezes.paulo.safe.adapter.ListReportItemAdapter;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.data.Session;
import menezes.paulo.safe.entity.Friend;
import menezes.paulo.safe.entity.Place;
import menezes.paulo.safe.entity.Report;
import menezes.paulo.safe.entity.Tip;
import menezes.paulo.safe.entity.User;
import menezes.dd.processbutton.FlatButton;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableDeleteCallback;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {

	private User mUser;
	private MobileServiceClient mClient;
    private ProgressDialog mLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
		mLoading = ProgressDialog.show(this, "", "Carregando usuário, aguarde...");
		
		Bundle b = getIntent().getExtras();
		mUser = (User)b.getSerializable("user");
		
		mClient = Connect.getInstance();
					
		ImageView photo = (ImageView)findViewById(R.id.photo);
		
		mUser.getPhoto(photo, (TextView)findViewById(R.id.photo_name));
		
		((TextView)findViewById(R.id.photo_name)).setText(String.valueOf(mUser.firstname.toCharArray()[0]) + String.valueOf(mUser.lastname.toCharArray()[0]));
		((TextView)findViewById(R.id.name)).setText(mUser.getName());
		((TextView)findViewById(R.id.biografy)).setText(mUser.biografy != null ? mUser.biografy : "Sem biografia");
		((TextView)findViewById(R.id.phone)).setText(mUser.phone != null ? "Telefone: " + mUser.phone : "Nenhum telefone cadastrado");
		
		if(mUser.id != Session.getInt("userID")) {
			mClient.getTable(Friend.class).where().field("idUser2").eq(mUser.id).and().field("idUser1").eq(Session.getInt("userID")).execute(new TableQueryCallback<Friend>() {
				@Override
				public void onCompleted(final List<Friend> friends, final int i, Exception e, ServiceFilterResponse response) {
					if(e == null && friends.size() > 0) {
						((FlatButton) findViewById(R.id.unfollow)).setVisibility(View.VISIBLE);
						((FlatButton) findViewById(R.id.unfollow)).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								mLoading = ProgressDialog.show(ProfileActivity.this, "", "Deixando de seguir...");
								mClient.getTable(Friend.class).delete(friends.get(i), new TableDeleteCallback() {
									@Override
									public void onCompleted(Exception e, ServiceFilterResponse response) {
										if(e == null) {
											mLoading.dismiss();
											startActivity(getIntent());
											finish();
										}
									}
								});
							}
						});
					} else {
						((FlatButton) findViewById(R.id.follow)).setVisibility(View.VISIBLE);
						((FlatButton) findViewById(R.id.follow)).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Friend f = new Friend();
								f.idUser1 = Session.getInt("userID");
								f.idUser2 = mUser.id;
	
								mLoading = ProgressDialog.show(ProfileActivity.this, "", "Seguindo...");
								mClient.getTable(Friend.class).insert(f, new TableOperationCallback<Friend>() {
									@Override
									public void onCompleted(Friend friend, Exception e, ServiceFilterResponse response) {
										if(e == null) {
											mLoading.dismiss();
											startActivity(getIntent());
											finish();
										}
									}
								});
							}
						});
					}
					
					((ProgressBar) findViewById(R.id.followLoading)).setVisibility(View.GONE);
				}
			});
		} else {
			((ProgressBar) findViewById(R.id.followLoading)).setVisibility(View.GONE);
		}
		
		mClient.getTable(Report.class).where().field("idUser").eq(mUser.id).and().field("anonymuos").eq(0).execute(new TableQueryCallback<Report>() {
			@Override
			public void onCompleted(final List<Report> reports, int i, Exception e, ServiceFilterResponse response) {
				((ProgressBar) findViewById(R.id.reportLoading)).setVisibility(View.GONE);
				
				if(e == null && reports.size() > 0) {
					((TextView)findViewById(R.id.reports)).setText(String.valueOf(reports.size()));
					((LinearLayout) findViewById(R.id.reportsLayout)).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							startActivity(new Intent(ProfileActivity.this, AllReportsActivity.class).putExtra("userID", mUser.id));
							ProfileActivity.this.finish();	
						}
					});
					
					ListView listContent = (ListView)findViewById(R.id.listReports);
					listContent.setVisibility(View.VISIBLE);
					
					listContent.setAdapter(new ListReportItemAdapter(ProfileActivity.this, reports));
					listContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
	                    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
							Intent main = new Intent(ProfileActivity.this, ReportActivity.class);
							Bundle b = new Bundle();
							b.putSerializable("report", reports.get(position));
							main.putExtras(b);
							
							startActivity(main);
	                    }
					});
				} else {
					((TextView)findViewById(R.id.reports)).setText("0");
					((TextView) findViewById(R.id.reportMsg)).setVisibility(View.VISIBLE);
				}
				
				((TextView) findViewById(R.id.reports)).setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(R.id.reportsLoading)).setVisibility(View.GONE);
			}
		});
		
		mLoading.dismiss();
		
		mClient.getTable(Friend.class).where().field("idUser1").eq(mUser.id).execute(new TableQueryCallback<Friend>() {
			@Override
			public void onCompleted(final List<Friend> friends, int i, Exception e, ServiceFilterResponse response) {
				if(e == null) {
					((LinearLayout) findViewById(R.id.followingLayout)).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent friend = new Intent(ProfileActivity.this, FriendsActivity.class);
							Bundle b = new Bundle();
							b.putInt("tipo", 1);
							b.putSerializable("friends", new ArrayList<Friend>(friends));
							friend.putExtras(b);
							startActivity(friend);
						}
					});
					
					((TextView) findViewById(R.id.following)).setText(String.valueOf(friends.size()));
				} else {
					((TextView) findViewById(R.id.following)).setText("0");
				}
				
				((TextView) findViewById(R.id.following)).setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(R.id.followingLoading)).setVisibility(View.GONE);
			}
		});
		
		mClient.getTable(Friend.class).where().field("idUser2").eq(mUser.id).execute(new TableQueryCallback<Friend>() {
			@Override
			public void onCompleted(final List<Friend> friends, int i, Exception e, ServiceFilterResponse response) {
				if(e == null) {
					((LinearLayout) findViewById(R.id.followersLayout)).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent friend = new Intent(ProfileActivity.this, FriendsActivity.class);
							Bundle b = new Bundle();
							b.putInt("tipo", 2);
							b.putSerializable("friends", new ArrayList<Friend>(friends));
							friend.putExtras(b);
							startActivity(friend);
						}
					});
					
					((TextView) findViewById(R.id.followers)).setText(String.valueOf(friends.size()));
				} else {
					((TextView) findViewById(R.id.followers)).setText("0");
				}
				
				((TextView) findViewById(R.id.followers)).setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(R.id.followersLoading)).setVisibility(View.GONE);
			}
		});
	
		((TextView) findViewById(R.id.trophies)).setText("0");
		((LinearLayout) findViewById(R.id.trophiesLayout)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(ProfileActivity.this, "Em breve!", Toast.LENGTH_LONG).show();
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
