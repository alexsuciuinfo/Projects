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

import suciu.alexandru.com.bookwormscommunity.models.News;
import suciu.alexandru.com.bookwormscommunity.R;

/**
 * Created by Alexandru on 13.05.2016.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private List<News> newsList = new ArrayList<>();

    public NewsAdapter(Context context, List<News> newsList) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.newsList = newsList;
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    @Override
    public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.news_item_layout, parent, false);
        return new NewsHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsHolder holder, int position) {

        Picasso.with(context).load(newsList.get(position).getBook().getImageURL()).error(R.drawable.book_loading)
                .placeholder(R.drawable.book_loading)
                .into(holder.imgBook);

        holder.tvTitle.setText(newsList.get(position).getBook().getTitle());
        holder.tvAuthor.setText(newsList.get(position).getBook().getAuthor());
        holder.ratingBar.setRating((float) newsList.get(position).getBook().getRating());
        holder.tvRating.setText(Double.toString(newsList.get(position).getBook().getRating()));
        holder.tvNewsMessage.setText(newsList.get(position).getMessage());

        if (newsList.get(position).getBook().getNrRead() != null) {
            holder.tvNrIndicator.setText(newsList.get(position).getBook().getNrRead());
        } else {
            holder.tvNrIndicator.setText(newsList.get(position).getBook().getNrRatings());
        }
        holder.tvNrThisWeek.setText(newsList.get(position).getThisWeek());
    }

    public class NewsHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvAuthor, tvRating, tvNrIndicator, tvNewsMessage, tvNrThisWeek;
        ImageView imgBook;
        RatingBar ratingBar;

        public NewsHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = (TextView) itemView.findViewById(R.id.tvAuthor);
            tvRating = (TextView) itemView.findViewById(R.id.tvRating);
            tvNrIndicator = (TextView) itemView.findViewById(R.id.tvNrRead);
            tvNewsMessage = (TextView) itemView.findViewById(R.id.tvNewsMessage);
            tvNrThisWeek = (TextView) itemView.findViewById(R.id.tvNrReadThisWeek);
            imgBook = (ImageView) itemView.findViewById(R.id.imgBook);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }
    }
}
