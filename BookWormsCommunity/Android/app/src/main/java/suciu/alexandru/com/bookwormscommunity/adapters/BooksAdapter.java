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

import suciu.alexandru.com.bookwormscommunity.models.Book;
import suciu.alexandru.com.bookwormscommunity.R;

/**
 * Created by Alexandru on 13.05.2016.
 */
public class BooksAdapter extends RecyclerView.Adapter <BooksAdapter.BookHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private List<Book> bookList = new ArrayList<>();

    public BooksAdapter(Context context, List<Book> bookList) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.bookList = bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    @Override
    public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.book_adapter_layout, parent, false);
        return new BookHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookHolder holder, int position) {

        Picasso.with(context).load(bookList.get(position).getImageURL()).error(R.drawable.book_loading)
                .placeholder(R.drawable.book_loading)
                .into(holder.imgBook);

        holder.bookId = bookList.get(position).getId();
        holder.tvTitle.setText(bookList.get(position).getTitle());
        holder.tvAuthor.setText(bookList.get(position).getAuthor());
        holder.ratingBar.setRating((float) bookList.get(position).getRating());
        holder.tvRating.setText(Double.toString(bookList.get(position).getRating()));
        holder.tvNrRatings.setText(bookList.get(position).getNrRatings().toString() + "  ratings");
        holder.tvNrRead.setText(bookList.get(position).getNrRead().toString() + "  readers");
    }

    public class BookHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvAuthor, tvRating, tvNrRatings, tvNrRead;
        ImageView imgBook;
        RatingBar ratingBar;
        int bookId;

        public BookHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = (TextView) itemView.findViewById(R.id.tvAuthor);
            tvRating = (TextView) itemView.findViewById(R.id.tvRating);
            tvNrRead = (TextView) itemView.findViewById(R.id.tvNrRead);
            tvNrRatings = (TextView) itemView.findViewById(R.id.tvNrRatings);
            imgBook = (ImageView) itemView.findViewById(R.id.imgBook);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }
    }
}
