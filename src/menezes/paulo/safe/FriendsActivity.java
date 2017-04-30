package menezes.paulo.safe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import menezes.paulo.safe.adapter.ListUserItemAdapter;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.entity.Friend;
import menezes.paulo.safe.entity.User;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class FriendsActivity extends Activity {
	MobileServiceClient mClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);

		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
		
		Bundle b = getIntent().getExtras();
		if(b != null) {
			mClient = Connect.getInstance();
			
			if(b.getInt("tipo") == 1) {
				setTitle("Seguidores");
				final ListView list = (ListView) FriendsActivity.this.findViewById(R.id.list);
				final ProgressBar loading = (ProgressBar) FriendsActivity.this.findViewById(R.id.loading);
				final TextView msg = (TextView) FriendsActivity.this.findViewById(R.id.msg);
				
				if(b.getSerializable("friends") != null) {
					final List<Friend> friends = (ArrayList<Friend>)b.getSerializable("friends");
					mClient.getTable(User.class).execute(new TableQueryCallback<User>() {
						@Override
						public void onCompleted(final List<User> users, int i, Exception e, ServiceFilterResponse response) {
							loading.setVisibility(View.GONE);
							if(e == null && users.size() > 0) {
								final List<User> myFriends = new ArrayList<User>();
								for (int j = 0; j < friends.size(); j++) {
									for (int k = 0; k < users.size(); k++) {
										if(friends.get(j).idUser2 == users.get(k).id) {
											myFriends.add(users.get(k));
										}
									}
								}
								
								list.setAdapter(new ListUserItemAdapter(FriendsActivity.this, myFriends));
								list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
									@Override
				                    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
										Intent main = new Intent(FriendsActivity.this, ProfileActivity.class);
										Bundle b = new Bundle();
										b.putSerializable("user", myFriends.get(position));
										main.putExtras(b);
										
										FriendsActivity.this.startActivity(main);
				                    }
								});
								
								list.setVisibility(View.VISIBLE);
							} else {
								list.setVisibility(View.GONE);
								msg.setVisibility(View.VISIBLE);
								msg.setText("Nenhum usuário encontrado.");
							}
						}
					});
				} else {
					list.setVisibility(View.GONE);
					msg.setVisibility(View.VISIBLE);
					msg.setText("Nenhum usuário encontrado.");
				}
			} else if(b.getInt("tipo") == 2) {
				setTitle("Seguindo");
				final ListView list = (ListView) FriendsActivity.this.findViewById(R.id.list);
				final ProgressBar loading = (ProgressBar) FriendsActivity.this.findViewById(R.id.loading);
				final TextView msg = (TextView) FriendsActivity.this.findViewById(R.id.msg);
				
				if(b.getSerializable("friends") != null) {
					final List<Friend> friends = (ArrayList<Friend>)b.getSerializable("friends");
					mClient.getTable(User.class).execute(new TableQueryCallback<User>() {
						@Override
						public void onCompleted(final List<User> users, int i, Exception e, ServiceFilterResponse response) {
							
							loading.setVisibility(View.GONE);
							if(e == null && users.size() > 0) {
								final List<User> myFriends = new ArrayList<User>();
								for (int j = 0; j < friends.size(); j++) {
									for (int k = 0; k < users.size(); k++) {
										if(friends.get(j).idUser1 == users.get(k).id) {
											myFriends.add(users.get(k));
										}
									}
								}
								
								list.setAdapter(new ListUserItemAdapter(FriendsActivity.this, myFriends));
								list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
									@Override
				                    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
										Intent main = new Intent(FriendsActivity.this, ProfileActivity.class);
										Bundle b = new Bundle();
										b.putSerializable("user", myFriends.get(position));
										main.putExtras(b);
										
										FriendsActivity.this.startActivity(main);
				                    }
								});
								
								list.setVisibility(View.VISIBLE);
							} else {
								list.setVisibility(View.GONE);
								msg.setVisibility(View.VISIBLE);
								msg.setText("Nenhum usuário encontrado.");
							}
						}
					});
				} else {
					list.setVisibility(View.GONE);
					msg.setVisibility(View.VISIBLE);
					msg.setText("Nenhum usuário encontrado.");
				}
			}
		}
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
