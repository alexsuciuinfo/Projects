package suciu.alexandru.com.bookwormscommunity.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import suciu.alexandru.com.bookwormscommunity.R;

/**
 * Created by Alexandru on 13.02.2016.
 */
public class DrawerAdapter extends BaseAdapter {

    private List<DrawerItem> drawerItems;
    private LayoutInflater layoutInflater;

    public DrawerAdapter(Context context, List<DrawerItem> DrawerItems) {
        super();
        this.drawerItems = DrawerItems;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return drawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = layoutInflater.inflate(R.layout.fragment_item_skelet, null);
        DrawerItem drawerItem = drawerItems.get(position);

        TextView text = (TextView) convertView.findViewById(R.id.tvDrawerItemText);
        text.setText(drawerItem.getTitle());

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imgDrawerItemPhoto);
        imageView.setImageResource(drawerItem.getItemPosition());

        return convertView;
    }
}