package menezes.paulo.safe.adapter;

import menezes.paulo.safe.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuListAdapter extends BaseAdapter {
	
	Context context;
    String[] mTitle;
    int[] mIcon;
    LayoutInflater inflater;
 
    public MenuListAdapter(Context context, String[] title, int[] icon) {
        this.context = context;
        this.mTitle = title;
        this.mIcon = icon;
    }
 
    @Override
    public int getCount() {
        return mTitle.length;
    }
 
    @Override
    public Object getItem(int position) {
        return mTitle[position];
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
        TextView txtTitle;
        ImageView imgIcon;
 
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.drawer_list_item, parent, false);
 
        txtTitle = (TextView) itemView.findViewById(R.id.title);
        txtTitle.setText(mTitle[position]);
 
        imgIcon = (ImageView) itemView.findViewById(R.id.icon);
        imgIcon.setImageResource(mIcon[position]);
        
        return itemView;
    }

}
