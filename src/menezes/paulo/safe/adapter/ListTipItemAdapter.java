package menezes.paulo.safe.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import menezes.paulo.safe.R;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.entity.Tip;
import menezes.paulo.safe.entity.User;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class ListTipItemAdapter extends ArrayAdapter<Tip> {
    private final Context context;
    private List<Tip> mTip = new ArrayList<Tip>();
    private MobileServiceClient mClient;
    
    public ListTipItemAdapter(Context context, List<Tip> tips) {
        super(context, R.layout.list_places, tips);
        this.context = context;
        this.mTip = tips;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View v = inflater.inflate(R.layout.list_comments, parent, false);

        mClient = Connect.getInstance();
        mClient.getTable(User.class).where().field("id").eq(mTip.get(position).idUser).execute(new TableQueryCallback<User>() {
			@Override
			public void onCompleted(List<User> users, int i, Exception e, ServiceFilterResponse response) {
				if(e == null) {											
				    ((TextView) v.findViewById(R.id.name)).setText(users.get(i).getName());
				    ((TextView) v.findViewById(R.id.text)).setText(mTip.get(position).text);
				    
					users.get(i).getPhoto((ImageView) v.findViewById(R.id.image), (TextView) v.findViewById(R.id.photo_name));
				}
			}
		});

        return v;
    }
}