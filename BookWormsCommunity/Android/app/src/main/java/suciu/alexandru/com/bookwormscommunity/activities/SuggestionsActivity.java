package suciu.alexandru.com.bookwormscommunity.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import suciu.alexandru.com.bookwormscommunity.utils.AppSharedPreferences;
import suciu.alexandru.com.bookwormscommunity.utils.Constants;
import suciu.alexandru.com.bookwormscommunity.R;
import suciu.alexandru.com.bookwormscommunity.utils.RecyclerItemClickListener;
import suciu.alexandru.com.bookwormscommunity.services.ServiceManager;
import suciu.alexandru.com.bookwormscommunity.models.Suggestion;
import suciu.alexandru.com.bookwormscommunity.adapters.SuggestionsAdapter;
import suciu.alexandru.com.bookwormscommunity.utils.Util;

public class SuggestionsActivity extends BaseActivity {

    private static List<Suggestion> suggestionsList;
    private RecyclerView recyclerView;
    private SuggestionsAdapter suggestionsAdapter;
    public static ServiceManager serviceManager;
    public static Spinner spinnerCategory, spinnerStatus;
    public static LinearLayout linearLayoutSearch;
    public static String API_KEY;
    public static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        API_KEY = AppSharedPreferences.getApiKey(getApplicationContext(), Constants.ApiKey);
        serviceManager = new ServiceManager();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");

        initialize();
        setCurrActions(RECOMMENDATIONS);
        activateToolbarHome();
        setDrawerItems();
        setRecyclerView();
        setCurrActionsStyle();

        progressDialog.show();
        setSuggestions();
    }



    private void setRecyclerView() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        suggestionsAdapter = new SuggestionsAdapter(SuggestionsActivity.this, new ArrayList<Suggestion>());
        recyclerView = (RecyclerView) findViewById(R.id.homeRecyclerView);
        final GridLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(suggestionsAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollColoredViewParallax(dy);
                if (dy > 0) {
                    hideToolbar(dy);
                } else {
                    showToolbar(dy);
                }

            }

            private void scrollColoredViewParallax(int dy) {
                llContainer.setTranslationY(llContainer.getTranslationY() - dy / 3);
            }

            private void hideToolbar(int dy) {
                if (cannotHide(dy)) {
                    llContainer.setTranslationY(-toolbar.getBottom());
                } else {
                    llContainer.setTranslationY(llContainer.getTranslationY() - dy);
                }
            }

            private boolean cannotHide(int dy) {
                return Math.abs(llContainer.getTranslationY() - dy) > toolbar.getBottom();
            }

            private void showToolbar(int dy) {
                if (cannotShow(dy)) {
                    llContainer.setTranslationY(0);
                } else {
                    llContainer.setTranslationY(llContainer.getTranslationY() - dy);
                }
            }

            private boolean cannotShow(int dy) {
                return llContainer.getTranslationY() - dy > 0;

            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(final View viewRec, final int position) {

                        viewRec.findViewById(R.id.imgBook).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(SuggestionsActivity.this, BookDetailsActivity.class);
                                intent.putExtra(Constants.BOOK_ID, suggestionsList.get(position).getBookSuggestion().getId());
                                startActivity(intent);
                            }
                        });

                        viewRec.findViewById(R.id.tvBookRead).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(SuggestionsActivity.this, BookDetailsActivity.class);
                                intent.putExtra(Constants.BOOK_ID, suggestionsList.get(position).getBookRead().getId());
                                startActivity(intent);
                            }
                        });

                        viewRec.findViewById(R.id.btnWantToRead).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setBookToWanted(suggestionsList.get(position).getBookSuggestion().getId(), suggestionsList.get(position));;
                            }
                        });
                    }
                })
        );
    }



    private void setSuggestions() {
        Call<List<Suggestion>> call = serviceManager.getApiInterface().getUserSuggestion(API_KEY);
        call.enqueue(new Callback<List<Suggestion>>() {
            @Override
            public void onResponse(Call<List<Suggestion>> call, Response<List<Suggestion>> response) {
                unsetNoData();
                progressDialog.cancel();
                if (response.isSuccessful()) {
                    suggestionsList = response.body();
                    suggestionsAdapter.setSuggestionsList(suggestionsList);
                    if (suggestionsList.size() == 0) {
                        setNoData();
                    }
                } else {
                    setNoData();
                }
            }



            @Override
            public void onFailure(Call<List<Suggestion>> call, Throwable t) {
                progressDialog.cancel();
                setNoData();
                Toast.makeText(SuggestionsActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void setBookToWanted(int bookId, final Suggestion suggestion) {
        Call call = serviceManager.getApiInterface().addBookToWanted(API_KEY, bookId, Util.getCurrentDate());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    suggestionsList.remove(suggestion);
                    suggestionsAdapter.setSuggestionsList(suggestionsList);
                } else {
                    Toast.makeText(SuggestionsActivity.this, Constants.POST_FAILED, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(SuggestionsActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle(Constants.RECOMMENDATIONS);
        progressDialog.show();
        setSuggestions();
    }
}
