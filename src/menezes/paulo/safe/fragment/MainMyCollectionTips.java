package menezes.paulo.safe.fragment;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import menezes.paulo.safe.R;
import menezes.paulo.safe.adapter.ListTipItemAdapter;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.data.Session;
import menezes.paulo.safe.entity.Tip;
import menezes.dd.processbutton.FlatButton;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class MainMyCollectionTips extends SherlockFragment {
	MobileServiceClient mClient;
	ProgressDialog mLoading;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    final View view = inflater.inflate(R.layout.fragment_my_collection, container, false);
	   	    
	    mClient = Connect.getInstance();
	    mClient.getTable(Tip.class).where().field("idUser").eq(Session.getInt("userID")).execute(new TableQueryCallback<Tip>() {
			@Override
			public void onCompleted(final List<Tip> tips, int i, Exception e, ServiceFilterResponse response) {
				ListView list = (ListView) view.findViewById(R.id.list);
				ProgressBar loading = (ProgressBar) view.findViewById(R.id.loading);
				TextView msg = (TextView) view.findViewById(R.id.msg);
				
				loading.setVisibility(View.GONE);
				if(e == null && tips.size() > 0) {
					list.setAdapter(new ListTipItemAdapter(getActivity(), tips));
					list.setVisibility(View.VISIBLE);
				} else {
					list.setVisibility(View.GONE);
					msg.setVisibility(View.VISIBLE);
					msg.setText("Você não adicionou nenhuma dica.");
				}
			}
		});
	    
	    final FlatButton addTip = (FlatButton) view.findViewById(R.id.addTip);
	    addTip.setVisibility(View.VISIBLE);
	    addTip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				final View v = inflater.inflate(R.layout.dialog_add_tip, null);

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				final Dialog dialog;

				builder.setView(v); 
				
				dialog = builder.create();
				dialog.show();
				
				final FlatButton addCom = (FlatButton) v.findViewById(R.id.addTip);
				addCom.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
						mLoading = ProgressDialog.show(getActivity(), "", "Enviando sua dica...");
						
						Tip tip = new Tip();
						tip.idUser = Session.getInt("userID");
						tip.text = ((EditText) v.findViewById(R.id.text)).getText().toString();
						
						mClient.getTable(Tip.class).insert(tip, new TableOperationCallback<Tip>() {
							@Override
							public void onCompleted(Tip tip, Exception e, ServiceFilterResponse response) {
								mLoading.dismiss();
								if(e == null) {
									getActivity().finish();
									startActivity(getActivity().getIntent());
								} else {
									Toast.makeText(getActivity(), "Houve um error, tente novamente", Toast.LENGTH_LONG).show();
								}
							}
						});
					}
				});
			}
		});
	    
	    return view;
	}
}
