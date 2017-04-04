package suciu.alexandru.com.bookwormscommunity.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import suciu.alexandru.com.bookwormscommunity.activities.BookDetailsActivity;
import suciu.alexandru.com.bookwormscommunity.utils.AppSharedPreferences;
import suciu.alexandru.com.bookwormscommunity.models.Book;
import suciu.alexandru.com.bookwormscommunity.adapters.BooksAdapter;
import suciu.alexandru.com.bookwormscommunity.utils.Constants;
import suciu.alexandru.com.bookwormscommunity.R;
import suciu.alexandru.com.bookwormscommunity.utils.RecyclerItemClickListener;
import suciu.alexandru.com.bookwormscommunity.services.ServiceManager;

public class MyBooksActivity extends AppCompatActivity {

    private static List<Book> bookList;
    private RecyclerView recyclerView;
    private BooksAdapter booksAdapter;
    public static ServiceManager serviceManager;
    public static Spinner spinnerBooks;
    public static String API_KEY;
    private static ProgressDialog progressDialog;
    private static String SORT_BY = Constants.DATE_ADDED, TYPE = Constants.ORDER_ASC;
    private static String USER_ID;
    private Toolbar toolbar;
    public static final int ALL = 0, READ = 1, WANTED = 2;
    public static int ACTION;
    TextView tvNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        API_KEY = AppSharedPreferences.getApiKey(getApplicationContext(), Constants.ApiKey);

        if (getIntent().getStringExtra(Constants.USER_ID) != null) {
            USER_ID = getIntent().getStringExtra(Constants.USER_ID);
            ACTION = getIntent().getIntExtra(Constants.SEARCH_TYPE, ALL);
        } else {
            USER_ID = AppSharedPreferences.getUserId(getApplicationContext(), Constants.USER_ID);
        }


        serviceManager = new ServiceManager();
        initializeComponents();
        setActionsForBooks();
        setRecyclerView();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();
    }

    private void initializeComponents() {

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("");

        tvNoData = (TextView) findViewById(R.id.tvNoDataFound);

        spinnerBooks = (Spinner) findViewById(R.id.spinnerToolbar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(toolbar.getContext(),
                R.array.CategoryMyBooks, R.layout.spinner_item_layout);
        spinnerBooks.setAdapter(adapter);
        spinnerBooks.setVisibility(View.VISIBLE);

        spinnerBooks.setSelection(ACTION);

    }

    private void setActionsForBooks() {
        spinnerBooks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        ACTION = ALL;
                        setMyBooksData(ALL, SORT_BY, TYPE);
                        break;
                    case 1:
                        ACTION = READ;
                        setMyBooksData(READ, SORT_BY, TYPE);
                        break;
                    case 2:
                        ACTION = WANTED;
                        setMyBooksData(WANTED, SORT_BY, TYPE);;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setRecyclerView() {
        booksAdapter = new BooksAdapter(MyBooksActivity.this, new ArrayList<Book>());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSearch);
        final GridLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(booksAdapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position) {
                        Intent intent = new Intent(MyBooksActivity.this, BookDetailsActivity.class);
                        intent.putExtra(Constants.BOOK_ID, bookList.get(position).getId());
                        startActivity(intent);
                    }
                })
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_books, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_author :
                SORT_BY = Constants.AUTHOR;
                setMyBooksData(ACTION, SORT_BY, TYPE);
                return true;
            case R.id.action_title :
                SORT_BY = Constants.TITLE;
                setMyBooksData(ACTION, SORT_BY, TYPE);
                return true;
            case R.id.action_date :
                SORT_BY = Constants.DATE_ADDED;
                setMyBooksData(ACTION, SORT_BY, TYPE);
                return true;
            case R.id.action_rating :
                SORT_BY = Constants.RATING;
                setMyBooksData(ACTION, SORT_BY, TYPE);
                return true;
            case R.id.action_nrRating :
                SORT_BY = Constants.NO_RATINGS;
                setMyBooksData(ACTION, SORT_BY, TYPE);
                return true;
            case R.id.action_readers :
                SORT_BY = Constants.NO_READ;
                setMyBooksData(ACTION, SORT_BY, TYPE);
                return true;
            case R.id.action_search_mode:
                if (TYPE == Constants.ORDER_ASC) {
                    TYPE = Constants.ORDER_DESC;
                    item.setIcon(R.drawable.ic_arrow_upward_white_36dp);
                    setMyBooksData(ACTION, SORT_BY, TYPE);
                } else {
                    TYPE = Constants.ORDER_ASC;
                    item.setIcon(R.drawable.ic_arrow_downward_white_36dp);
                    setMyBooksData(ACTION, SORT_BY, TYPE);
                }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setMyBooksData(int action, final String order, String type) {

        Call<List<Book>> call ;

        switch (action) {
            case ALL :
                call = serviceManager.getApiInterface().getUserBooks(API_KEY, USER_ID, order, type);
                break;
            case READ :
                call = serviceManager.getApiInterface().getUserReadBooks(API_KEY, USER_ID, order, type);
                break;
            default:
                call = serviceManager.getApiInterface().getUserWantedBooks(API_KEY, USER_ID, order, type);
        }

        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                unsetNoData();
                progressDialog.cancel();
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
                progressDialog.cancel();
                unsetNoData();
                Toast.makeText(MyBooksActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
            }

        });
    }

    public void setNoData() {
        tvNoData.setVisibility(View.VISIBLE);
    }

    public void unsetNoData() {
        tvNoData.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMyBooksData(ACTION, SORT_BY, TYPE);
    }
}
