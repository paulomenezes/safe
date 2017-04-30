package menezes.paulo.safe.fragment;

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
import menezes.paulo.safe.FriendsActivity;
import menezes.paulo.safe.PlaceActivity;
import menezes.paulo.safe.ProfileActivity;
import menezes.paulo.safe.R;
import menezes.paulo.safe.adapter.ListUserItemAdapter;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.data.Session;
import menezes.paulo.safe.entity.User;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class MainUser extends SherlockFragment {
	MobileServiceClient mClient;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    final View view = inflater.inflate(R.layout.fragment_users, container, false);
	    
	    mClient = Connect.getInstance();
	    mClient.getTable(User.class).where().field("id").ne(Session.getInt("userID")).execute(new TableQueryCallback<User>() {
			@Override
			public void onCompleted(final List<User> users, int i, Exception e, ServiceFilterResponse response) {
				ListView list = (ListView) view.findViewById(R.id.list);
				ProgressBar loading = (ProgressBar) view.findViewById(R.id.loading);
				TextView msg = (TextView) view.findViewById(R.id.msg);
				
				loading.setVisibility(View.GONE);
				if(e == null && users.size() > 0) {
					list.setAdapter(new ListUserItemAdapter(getActivity(), users));
					list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
	                    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
							Intent main = new Intent(getActivity(), ProfileActivity.class);
							Bundle b = new Bundle();
							b.putSerializable("user", users.get(position));
							main.putExtras(b);
							
							getActivity().startActivity(main);
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
	    
	    return view;
	}
}
