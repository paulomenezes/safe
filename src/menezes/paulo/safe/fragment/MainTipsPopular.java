package menezes.paulo.safe.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import menezes.paulo.safe.R;
import menezes.paulo.safe.adapter.ListTipItemAdapter;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.entity.Tip;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.QueryOrder;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class MainTipsPopular extends SherlockFragment {
	MobileServiceClient mClient;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    final View view = inflater.inflate(R.layout.fragment_tips, container, false);
	    
	    mClient = Connect.getInstance();
	    mClient.getTable(Tip.class).orderBy("id", QueryOrder.Descending).execute(new TableQueryCallback<Tip>() {
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
					msg.setText("Nenhuma dica encontrada.");
				}
			}
		});
	    
	    return view;
	}
}
