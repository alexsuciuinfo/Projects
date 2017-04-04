package suciu.alexandru.com.bookwormscommunity.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import suciu.alexandru.com.bookwormscommunity.R;
import suciu.alexandru.com.bookwormscommunity.models.Book;

/**
 * Created by Alexandru on 13.06.2016.
 */
public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    private List<String> categoryList;
    private HashMap<String,List<Book>> bookCategories;
    public Context context;

    public MyExpandableListAdapter(Context context, List<String> listHeader, HashMap<String, List<Book>> bookCategories) {
        this.context = context;
        this.bookCategories = bookCategories;
        this.categoryList = listHeader;
    }

    public void setData(List<String> listHeader, HashMap<String, List<Book>> bookCategories) {
        this.bookCategories = bookCategories;
        this.categoryList = listHeader;
        notifyDataSetChanged();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.bookCategories.get(categoryList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Book children = (Book) getChild(groupPosition, childPosition);

        TextView tvTitle, tvAuthor, tvRating, tvNrRatings, tvNrRead, tvBookID;
        ImageView imgBook;
        RatingBar ratingBar;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.book_adapter_layout, null);
        }

        tvBookID = (TextView) convertView.findViewById(R.id.tvBookId);
        tvTitle = (TextView) convertView.findViewById(R.id.tvBookTitle);
        tvAuthor = (TextView) convertView.findViewById(R.id.tvAuthor);
        tvRating = (TextView) convertView.findViewById(R.id.tvRating);
        tvNrRatings = (TextView) convertView.findViewById(R.id.tvNrRatings);
        tvNrRead = (TextView) convertView.findViewById(R.id.tvNrRead);
        imgBook = (ImageView) convertView.findViewById(R.id.imgBook);
        ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);

        tvTitle.setText(children.getTitle());
        tvAuthor.setText(children.getAuthor());
        tvNrRatings.setText("No. of ratings : " + children.getNrRatings());
        tvNrRead.setText("No. of readers : " + children.getNrRead());
        tvRating.setText(Double.toString(children.getRating()));
        ratingBar.setRating( (float) children.getRating());
        Picasso.with(context).load(children.getImageURL()).error(R.drawable.book_loading)
                .placeholder(R.drawable.book_loading)
                .into(imgBook);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return bookCategories.get(categoryList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categoryList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return categoryList.size();
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_group, null);
        }

        TextView groupHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        groupHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}