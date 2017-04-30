package menezes.paulo.safe.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import menezes.paulo.safe.AboutActivity;
import menezes.paulo.safe.EditProfileActivity;
import menezes.paulo.safe.LoginActivity;
import menezes.paulo.safe.R;
import menezes.paulo.safe.data.Session;
import menezes.dd.processbutton.FlatButton;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

public class MainSettings extends SherlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		
		FlatButton editProfile = (FlatButton) view.findViewById(R.id.editProfile);
		editProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent edit = new Intent(getActivity(), EditProfileActivity.class);
				startActivity(edit);
			}
		});
		
		FlatButton about = (FlatButton) view.findViewById(R.id.about);
		about.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), AboutActivity.class));
			}
		});

		FlatButton logout = (FlatButton) view.findViewById(R.id.logout);
		logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LoginActivity.mGoogleApiClient.isConnected()) {
					Toast.makeText(getActivity(), "Google", Toast.LENGTH_LONG).show();
					
					Plus.AccountApi.clearDefaultAccount(LoginActivity.mGoogleApiClient);
					Plus.AccountApi.revokeAccessAndDisconnect(LoginActivity.mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {						
						@Override
						public void onResult(Status arg0) {
							
						}
					});
					LoginActivity.mGoogleApiClient.disconnect();
				}
				
				SharedPreferences.Editor editor = Session.getInstance().edit();
                editor.clear();
                editor.commit();
                
                Session.getEditorInstance().edit().clear().commit();

                CookieSyncManager.createInstance(getActivity());
                android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
                cookieManager.removeAllCookie();

				Session.putInt("userID", -1);
				Intent login = new Intent(getActivity(), LoginActivity.class);
				startActivity(login);
			}
		});

		return view;
	}
}