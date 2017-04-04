package suciu.alexandru.com.bookwormscommunity.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import suciu.alexandru.com.bookwormscommunity.R;
import suciu.alexandru.com.bookwormscommunity.models.Suggestion;

/**
 * Created by Alexandru on 13.05.2016.
 */
public class SuggestionsAdapter extends RecyclerView.Adapter <SuggestionsAdapter.SuggestionHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private List<Suggestion> suggestionsList = new ArrayList<>();

    public SuggestionsAdapter(Context context, List<Suggestion> suggestionsList) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.suggestionsList = suggestionsList;
    }

    public void setSuggestionsList(List<Suggestion> suggestionsList) {
        this.suggestionsList = suggestionsList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return suggestionsList.size();
    }

    @Override
    public SuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recommendation_item_layout, parent, false);
        return new SuggestionHolder(view);
    }

    @Override
    public void onBindViewHolder(SuggestionHolder holder, int position) {

        Picasso.with(context).load(suggestionsList.get(position).getBookSuggestion().getImageURL()).error(R.drawable.book_loading)
               .placeholder(R.drawable.book_loading)
               .into(holder.imgBook);
        SpannableString spannableString = new SpannableString(suggestionsList.get(position).getMessage());
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 17, suggestionsList.get(position).getBookRead().getTitle().length() + 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvTitle.setText(suggestionsList.get(position).getBookSuggestion().getTitle());
        holder.tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
        holder.tvAuthor.setText(suggestionsList.get(position).getBookSuggestion().getAuthor());
        holder.tvSuggestionsMessage.setText(spannableString);
        holder.tvSuggestionsMessage.setHighlightColor(Color.TRANSPARENT);
        holder.tvCategory.setText(suggestionsList.get(position).getBookSuggestion().getCategory());
        //Log.d("Calculator", suggestionsList.get(position).getBookSuggestion().getTitle());
    }

    public class SuggestionHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvAuthor, tvSuggestionsMessage, tvCategory;
        ImageView imgBook;

        public SuggestionHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = (TextView) itemView.findViewById(R.id.tvAuthor);
            tvCategory = (TextView) itemView.findViewById(R.id.tvCategory);
            tvSuggestionsMessage = (TextView) itemView.findViewById(R.id.tvBookRead);
            imgBook = (ImageView) itemView.findViewById(R.id.imgBook);
        }
    }
}
