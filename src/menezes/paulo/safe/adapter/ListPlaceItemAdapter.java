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
import menezes.paulo.safe.entity.Place;
import menezes.paulo.safe.entity.Place_Photo;
import menezes.paulo.safe.task.ImageDownloaderTask;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class ListPlaceItemAdapter extends ArrayAdapter<Place> {
    private final Context context;
    private List<Place> mPlaces = new ArrayList<Place>();
    private MobileServiceClient mClient;

    public ListPlaceItemAdapter(Context context, List<Place> stores) {
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

        mClient.getTable(Place_Photo.class).where().field("idPlace").eq(mPlaces.get(position).id).top(1).execute(new TableQueryCallback<Place_Photo>() {
			@Override
			public void onCompleted(List<Place_Photo> photos, int i, Exception e, ServiceFilterResponse response) {
				if(e == null) {
					if(photos.size() > 0) {
						new ImageDownloaderTask(imageView).execute("http://wisapp.azurewebsites.net/safe/" + photos.get(i).image);
					}
				}
			}
		});

        imageView.setMaxWidth(icon.getIntrinsicWidth());
        imageView.setAdjustViewBounds(true);

        titleView.setText(mPlaces.get(position).name);
        addressView.setText(mPlaces.get(position).address);

        return rowView;
    }
}