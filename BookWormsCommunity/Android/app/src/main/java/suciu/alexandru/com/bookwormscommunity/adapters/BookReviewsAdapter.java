package suciu.alexandru.com.bookwormscommunity.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import suciu.alexandru.com.bookwormscommunity.models.BookReview;
import suciu.alexandru.com.bookwormscommunity.R;
import suciu.alexandru.com.bookwormscommunity.utils.CircleTransform;

/**
 * Created by Alexandru on 13.05.2016.
 */
public class BookReviewsAdapter extends RecyclerView.Adapter<BookReviewsAdapter.BookReviewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private List<BookReview> bookReviewsList = new ArrayList<>();

    public BookReviewsAdapter(Context context, List<BookReview> bookReviewsList) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.bookReviewsList = bookReviewsList;
    }

    public void setBookReviewList(List<BookReview> bookReviewsList) {
        this.bookReviewsList = bookReviewsList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return bookReviewsList.size();
    }

    @Override
    public BookReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.review_layout, parent, false);
        return new BookReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookReviewHolder holder, int position) {

        Picasso.with(context).load(bookReviewsList.get(position).getUser().getPhoto()).error(R.drawable.placeholder_profile_circle)
                .placeholder(R.drawable.placeholder_profile_circle)
                .transform(new CircleTransform())
                .into(holder.imgUserPhoto);

        holder.tvDate.setText(bookReviewsList.get(position).getReview().getDate());
        holder.tvUsername.setText(bookReviewsList.get(position).getUser().getUsername());
        holder.tvReview.setText(bookReviewsList.get(position).getReview().getMessage());
        if (Double.valueOf(bookReviewsList.get(position).getReview().getRating()) != 0)
        holder.ratingBar.setRating((float) bookReviewsList.get(position).getReview().getRating());
    }

    public class BookReviewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvReview, tvUsername;
        ImageView imgUserPhoto;
        RatingBar ratingBar;

        public BookReviewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tvReviewDate);
            tvReview = (TextView) itemView.findViewById(R.id.tvUserReview);
            tvUsername = (TextView) itemView.findViewById(R.id.tvReviewUsername);
            imgUserPhoto = (ImageView) itemView.findViewById(R.id.imgUserPhotoReview);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBarReviewRating);
        }
    }
}
