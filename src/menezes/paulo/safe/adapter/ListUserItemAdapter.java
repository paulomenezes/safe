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
import menezes.paulo.safe.entity.User;

public class ListUserItemAdapter extends ArrayAdapter<User> {
    private final Context context;
    private List<User> mUsers = new ArrayList<User>();

    public ListUserItemAdapter(Context context, List<User> stores) {
        super(context, R.layout.list_places, stores);
        this.context = context;
        this.mUsers = stores;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        View rowView = inflater.inflate(R.layout.list_users, parent, false);
        TextView nameView = (TextView) rowView.findViewById(R.id.name);
        TextView biografyView = (TextView) rowView.findViewById(R.id.biografy);
        TextView phoneView = (TextView) rowView.findViewById(R.id.phone);

        final ImageView imageView = (ImageView) rowView.findViewById(R.id.photo);
        mUsers.get(position).getPhoto(imageView, (TextView) rowView.findViewById(R.id.photo_name));

        nameView.setText(mUsers.get(position).getName());
        biografyView.setText(mUsers.get(position).biografy);
        phoneView.setText(mUsers.get(position).phone);

        return rowView;
    }
}