package suciu.alexandru.com.bookwormscommunity.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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
import suciu.alexandru.com.bookwormscommunity.services.ServiceManager;
import suciu.alexandru.com.bookwormscommunity.utils.RecyclerItemClickListener;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SearchView searchView;
    public static List<Book> bookList;
    private RecyclerView recyclerView;
    private static String searchContent;
    private BooksAdapter booksAdapter;
    private ServiceManager serviceManager;
    public static String API_KEY;
    private ProgressDialog progressDialog;
    TextView tvNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        API_KEY = AppSharedPreferences.getApiKey(getApplicationContext(), Constants.ApiKey);

        if (getIntent().getStringExtra(Constants.SEARCH) != null) {
            searchContent = getIntent().getStringExtra(Constants.SEARCH);
        }

        serviceManager = new ServiceManager();
        progressDialog = new ProgressDialog(this);
        initializeComponents();
        setUpRecyclerView();

        if (searchContent != null) {
            searchBooks(searchContent);
        }
    }


    private void setUpRecyclerView() {
        booksAdapter = new BooksAdapter(SearchActivity.this, new ArrayList<Book>());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSearch);
        final GridLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(booksAdapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        Intent intent = new Intent(SearchActivity.this, BookDetailsActivity.class);
                        intent.putExtra(Constants.BOOK_ID, bookList.get(position).getId());
                        startActivity(intent);
                        finish();
                    }
                })
        );
    }

    private void initializeComponents() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvNoData = (TextView) findViewById(R.id.tvNoDataFound);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search_book);
        searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (searchContent != null)
                {
                    if (!s.equals(searchContent)) {
                        progressDialog.show();
                        searchBooks(s);
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return false;
            }
        });

        return true;
    }

    private void searchBooks(final String searchContent) {
        Call<List<Book>> call = serviceManager.getApiInterface().searchBooks(API_KEY, searchContent);
        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                unsetNoData();
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    bookList = response.body();
                    Toast.makeText(SearchActivity.this, searchContent, Toast.LENGTH_LONG).show();
                    booksAdapter.setBookList(bookList);

                    if(bookList.size() == 0) {
                        setNoData();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                progressDialog.dismiss();
                setNoData();
            }
        });
    }

    public void unsetNoData(){
        tvNoData.setVisibility(View.GONE);
    }

    public void setNoData(){
        tvNoData.setVisibility(View.VISIBLE);
    }

}
