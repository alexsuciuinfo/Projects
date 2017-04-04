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
import suciu.alexandru.com.bookwormscommunity.models.News;
import suciu.alexandru.com.bookwormscommunity.adapters.NewsAdapter;
import suciu.alexandru.com.bookwormscommunity.R;
import suciu.alexandru.com.bookwormscommunity.utils.RecyclerItemClickListener;
import suciu.alexandru.com.bookwormscommunity.services.ServiceManager;

public class NewsActivity extends BaseActivity {

    private static List<News> newsList;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    public static ServiceManager serviceManager;
    public static Spinner spinnerCategory, spinnerStatus;
    public static LinearLayout linearLayoutSearch;
    public static String API_KEY;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        API_KEY = AppSharedPreferences.getApiKey(getApplicationContext(), Constants.ApiKey);
        serviceManager = new ServiceManager();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");

        initialize();
        setCurrActions(NEWS);
        activateToolbarHome();
        setDrawerItems();
        setRecyclerView();
        setCurrActionsStyle();

        progressDialog.show();
        setNews();
    }


    private void setRecyclerView() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        newsAdapter = new NewsAdapter(NewsActivity.this, new ArrayList<News>());
        recyclerView = (RecyclerView) findViewById(R.id.homeRecyclerView);
        final GridLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(newsAdapter);
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
                    @Override public void onItemClick(View view, final int position) {
                        Intent intent = new Intent(NewsActivity.this, BookDetailsActivity.class);
                        intent.putExtra(Constants.BOOK_ID, newsList.get(position).getBook().getId());
                        startActivity(intent);
                    }
                })
        );
    }



    private void setNews() {
        Call<List<News>> call = serviceManager.getApiInterface().getNews(API_KEY);
        call.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                unsetNoData();
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    newsList = response.body();
                    newsAdapter.setNewsList(newsList);
                    if (newsList.size() == 0) {
                        setNoData();
                    }
                } else {
                    setNoData();
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                progressDialog.dismiss();
                setNoData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle(Constants.NEWS);
        progressDialog.show();
        setNews();
    }


}
