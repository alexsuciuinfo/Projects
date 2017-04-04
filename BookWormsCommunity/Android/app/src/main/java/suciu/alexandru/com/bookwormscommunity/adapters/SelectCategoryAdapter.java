package suciu.alexandru.com.bookwormscommunity.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import suciu.alexandru.com.bookwormscommunity.R;

/**
 * Created by Alexandru on 14.06.2016.
 */
public class SelectCategoryAdapter extends BaseExpandableListAdapter {

    String header;
    List<String> categories;
    Context context;

    public SelectCategoryAdapter(Context context, String header, List<String> categories) {
        this.header = header;
        this.categories = categories;
        this.context = context;
    }

    public void setData(String header, List<String> categories) {
        this.header = header;
        this.categories = categories;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int i) {
        return categories.size();
    }

    @Override
    public Object getGroup(int i) {
        return header;
    }

    @Override
    public Object getChild(int i, int i1) {
        return categories.get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.group_select_category, null);
        }

        TextView tvHeader = (TextView) view.findViewById(R.id.tvGroupHeaderSelect);
        tvHeader.setText(header);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        final String childText = (String) getChild(i, i1);

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.select_category_item, null);
        }

        CheckBox checkBoxCategory = (CheckBox) view.findViewById(R.id.checkboxCategory);
        checkBoxCategory.setText(childText);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }



}
