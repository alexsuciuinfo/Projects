package suciu.alexandru.com.bookwormscommunity.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import suciu.alexandru.com.bookwormscommunity.utils.AppSharedPreferences;
import suciu.alexandru.com.bookwormscommunity.models.Book;
import suciu.alexandru.com.bookwormscommunity.adapters.BooksAdapter;
import suciu.alexandru.com.bookwormscommunity.utils.Constants;
import suciu.alexandru.com.bookwormscommunity.R;
import suciu.alexandru.com.bookwormscommunity.utils.RecyclerItemClickListener;
import suciu.alexandru.com.bookwormscommunity.services.ServiceManager;

public class BooksActivity extends BaseActivity {

    private static List<Book> bookList;
    private RecyclerView recyclerView;
    private BooksAdapter booksAdapter;
    public static ServiceManager serviceManager;
    public static Spinner spinnerCategory, spinnerStatus;
    public static LinearLayout linearLayoutSearch;
    public static String API_KEY, USER_ID;
    private static ProgressDialog progressDialog;
    private SearchView search_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        API_KEY = AppSharedPreferences.getApiKey(getApplicationContext(), Constants.ApiKey);
        USER_ID = AppSharedPreferences.getUserId(getApplicationContext(), Constants.USER_ID);
        serviceManager = new ServiceManager();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        initialize();
        setCurrActions(MYBOOKS);
        activateToolbarHome();
        activateSpinner();
        setActionsForMyBooks();
        setDrawerItems();
        setRecyclerView();
        setCurrActionsStyle();
    }

    private void setActionsForMyBooks() {
        spinnerToolbar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                progressDialog.show();
                switch (i) {
                    case 0:
                        Call<List<Book>> callAll = serviceManager.getApiInterface().getUserBooks(API_KEY, USER_ID, Constants.DATE_ADDED, "");
                        setMyBooksData(callAll);
                        break;
                    case 1:
                        Call<List<Book>> callRead = serviceManager.getApiInterface().getUserReadBooks(API_KEY, USER_ID, Constants.DATE_ADDED, "");
                        setMyBooksData(callRead);
                        break;
                    case 2:
                        Call<List<Book>> callWanted = serviceManager.getApiInterface().getUserWantedBooks(API_KEY, USER_ID, Constants.DATE_ADDED, "");
                        setMyBooksData(callWanted);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setRecyclerView() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        booksAdapter = new BooksAdapter(BooksActivity.this, new ArrayList<Book>());
        recyclerView = (RecyclerView) findViewById(R.id.homeRecyclerView);
        final GridLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(booksAdapter);
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
                    @Override
                    public void onItemClick(View view, final int position) {
                        Intent intent = new Intent(BooksActivity.this, BookDetailsActivity.class);
                        intent.putExtra(Constants.BOOK_ID, bookList.get(position).getId());
                        startActivity(intent);
                    }
                })
        );
    }


    private void setMyBooksData(Call<List<Book>> call) {
        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                unsetNoData();
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    bookList = response.body();
                    booksAdapter.setBookList(bookList);
                    if (bookList.size() == 0) {
                        setNoData();
                    }
                } else {
                    setNoData();
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(BooksActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
                setNoData();
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.show();
        Call<List<Book>> callAll = serviceManager.getApiInterface().getUserBooks(API_KEY, USER_ID, Constants.DATE_ADDED, "");
        setMyBooksData(callAll);
    }

}
