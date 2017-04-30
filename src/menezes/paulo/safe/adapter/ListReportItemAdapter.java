package menezes.paulo.safe.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import menezes.paulo.safe.R;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.entity.Report;
import menezes.paulo.safe.entity.Report_Photo;
import menezes.paulo.safe.task.ImageDownloaderTask;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class ListReportItemAdapter extends ArrayAdapter<Report> {
    private final Context context;
    private List<Report> mPlaces = new ArrayList<Report>();
    private MobileServiceClient mClient;
    
    public ListReportItemAdapter(Context context, List<Report> stores) {
        super(context, R.layout.list_places, stores);
        this.context = context;
        this.mPlaces = stores;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mClient = Connect.getInstance();
        
        View rowView = inflater.inflate(R.layout.list_places, parent, false);
        TextView titleView = (TextView) rowView.findViewById(R.id.name);
        TextView addressView = (TextView) rowView.findViewById(R.id.address);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        final Drawable icon = getContext().getResources().getDrawable(R.drawable.ic_launcher);

        mClient.getTable(Report_Photo.class).where().field("idReport").eq(mPlaces.get(position).id).top(1).execute(new TableQueryCallback<Report_Photo>() {
			@Override
			public void onCompleted(List<Report_Photo> photos, int i, Exception e, ServiceFilterResponse response) {
				if(e == null) {
					if(photos.size() > 0) {
						new ImageDownloaderTask(imageView).execute("http://wisapp.azurewebsites.net/safe/" + photos.get(i).image);
					}
				}
			}
		});

        imageView.setMaxWidth(icon.getIntrinsicWidth());
        imageView.setAdjustViewBounds(true);

        titleView.setText(mPlaces.get(position).title);
        addressView.setText(mPlaces.get(position).type);

        return rowView;
    }
}