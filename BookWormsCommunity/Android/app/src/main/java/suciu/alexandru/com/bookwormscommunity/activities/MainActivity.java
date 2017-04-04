package suciu.alexandru.com.bookwormscommunity.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import suciu.alexandru.com.bookwormscommunity.R;
import suciu.alexandru.com.bookwormscommunity.adapters.BooksAdapter;
import suciu.alexandru.com.bookwormscommunity.adapters.MyExpandableListAdapter;
import suciu.alexandru.com.bookwormscommunity.adapters.SelectCategoryAdapter;
import suciu.alexandru.com.bookwormscommunity.models.Book;
import suciu.alexandru.com.bookwormscommunity.services.ServiceManager;
import suciu.alexandru.com.bookwormscommunity.utils.AppSharedPreferences;
import suciu.alexandru.com.bookwormscommunity.utils.Constants;
import suciu.alexandru.com.bookwormscommunity.utils.RecyclerItemClickListener;

public class MainActivity extends AppCompatActivity {
    // More efficient than HashMap for mapping integers to objects
    public static HashMap<String, List<Book>> bookCategoryList;
    private ExpandableListView expandableListViewBookCategories;
    private ExpandableListView expandableListViewSelectCategories;
    private MyExpandableListAdapter bookCategoriesAdapter;
    private SelectCategoryAdapter bookSelectCategoriesAdapter;
    public static List<String> categories, newCategories;
    public String header = "Choose Category";
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
    public static String RECYCLER = "RECYCLER", LIST = "LIST", VIEW_TYPE = "RECYCLER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        API_KEY = AppSharedPreferences.getApiKey(getApplicationContext(), Constants.ApiKey);

        if (getIntent().getStringExtra(Constants.USER_ID) != null) {
            USER_ID = getIntent().getStringExtra(Constants.USER_ID);
            ACTION = getIntent().getIntExtra(Constants.SEARCH_TYPE, ALL);
        } else {
            USER_ID = AppSharedPreferences.getUserId(getApplicationContext(), Constants.USER_ID);
        }

        newCategories = new ArrayList<>();
        bookList = new ArrayList<>();
        bookCategoryList = new HashMap<>();
        serviceManager = new ServiceManager();
        initializeComponents();
        setRecyclerView();
        setExpandableListViewSelectCategory();
        setExpandableListViewBookCategories();
        setActionsForBooks();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();
    }

    private void setExpandableListViewSelectCategory() {
        bookSelectCategoriesAdapter = new SelectCategoryAdapter(MainActivity.this, header, new ArrayList<String>());
        expandableListViewSelectCategories = (ExpandableListView) findViewById(R.id.expandableSelectCategory);
        expandableListViewSelectCategories.setAdapter(bookSelectCategoriesAdapter);
        getCategories();

        expandableListViewSelectCategories.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
               // expandableListViewSelectCategories.grou
            }
        });

        expandableListViewSelectCategories.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkboxCategory);

                if (expandableListViewSelectCategories.isGroupExpanded(0)) {
                    checkBox.toggle();
                    if (checkBox.isChecked()) {
                        newCategories.add(categories.get(i1));
                    } else {
                        int pos = getListPositionByName(newCategories, categories.get(i1));
                        newCategories.remove(pos);
                    }
                    checkData(ACTION, SORT_BY, TYPE);
                }
                return false;
            }
        });
    }


    private void setExpandableListViewBookCategories() {
        expandableListViewBookCategories = (ExpandableListView) findViewById(R.id.expandableBookCategories);
        bookCategoriesAdapter = new MyExpandableListAdapter(this, new ArrayList<String>(), new HashMap<String, List<Book>>());
        expandableListViewBookCategories.setAdapter(bookCategoriesAdapter);

        expandableListViewBookCategories.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                TextView groupName = (TextView) expandableListView.findViewById(R.id.lblListHeader);
                Intent intent = new Intent(MainActivity.this, BookDetailsActivity.class);
                intent.putExtra(Constants.BOOK_ID, bookCategoryList.get(groupName.getText()).get(i1).getId());
                startActivity(intent);
                return false;
            }
        });
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
                        checkData(ALL, SORT_BY, TYPE);
                        break;
                    case 1:
                        ACTION = READ;
                        checkData(READ, SORT_BY, TYPE);
                        break;
                    case 2:
                        ACTION = WANTED;
                        checkData(WANTED, SORT_BY, TYPE);;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setRecyclerView() {
        booksAdapter = new BooksAdapter(MainActivity.this, new ArrayList<Book>());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSearch);
        final GridLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(booksAdapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position) {
                        Intent intent = new Intent(MainActivity.this, BookDetailsActivity.class);
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
                checkData(ACTION, SORT_BY, TYPE);
                return true;
            case R.id.action_title :
                SORT_BY = Constants.TITLE;
                checkData(ACTION, SORT_BY, TYPE);
                return true;
            case R.id.action_date :
                SORT_BY = Constants.DATE_ADDED;
                checkData(ACTION, SORT_BY, TYPE);
                return true;
            case R.id.action_rating :
                SORT_BY = Constants.RATING;
                checkData(ACTION, SORT_BY, TYPE);
                return true;
            case R.id.action_nrRating :
                SORT_BY = Constants.NO_RATINGS;
                checkData(ACTION, SORT_BY, TYPE);
                return true;
            case R.id.action_readers :
                SORT_BY = Constants.NO_READ;
                checkData(ACTION, SORT_BY, TYPE);
                return true;
            case R.id.action_search_mode:
                if (TYPE == Constants.ORDER_ASC) {
                    TYPE = Constants.ORDER_DESC;
                    item.setIcon(R.drawable.ic_arrow_upward_white_36dp);
                    checkData(ACTION, SORT_BY, TYPE);
                } else {
                    TYPE = Constants.ORDER_ASC;
                    item.setIcon(R.drawable.ic_arrow_downward_white_36dp);
                    checkData(ACTION, SORT_BY, TYPE);
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
                    if (VIEW_TYPE == RECYCLER) {
                        booksAdapter.setBookList(bookList);
                        if (bookList.size() == 0) {
                            setNoData();
                        }
                    } else {
                        showBooksByCategory();
                    }
                } else {
                    setNoData();
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                progressDialog.cancel();
                unsetNoData();
                Toast.makeText(MainActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
            }

        });
    }

    private void getCategories() {
        Call<List<String>> call = serviceManager.getApiInterface().getCategories(API_KEY);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    categories = response.body();
                    bookSelectCategoriesAdapter.setData(header, categories);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load categories", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(MainActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
            }
        });
    }




    public void setNoData() {
        tvNoData.setVisibility(View.VISIBLE);
    }

    public void unsetNoData() {
        tvNoData.setVisibility(View.GONE);
    }

    public void removeRecyclerView() {
        booksAdapter.setBookList(new ArrayList<Book>());
        recyclerView.setVisibility(View.GONE);
        VIEW_TYPE = LIST;
    }

    public void removeExpandableListView(){
        bookCategoriesAdapter.setData(new ArrayList<String>(), bookCategoryList);
        expandableListViewBookCategories.setVisibility(View.GONE);
        VIEW_TYPE = RECYCLER;
    }

    public void addRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
        VIEW_TYPE = RECYCLER;
        setMyBooksData(ACTION, SORT_BY, TYPE);
    }

    public void addExpandableListView() {
        expandableListViewBookCategories.setVisibility(View.VISIBLE);
        VIEW_TYPE = LIST;
        setMyBooksData(ACTION, SORT_BY, TYPE);
    }

    public void showBooksByCategory() {
        for (int i = 0; i < newCategories.size(); i++) {
            List<Book> listBook  = new ArrayList<>();
            for (int j = 0; j < bookList.size(); j++) {
                if (bookList.get(j).getCategory().equals(newCategories.get(i))) {
                    listBook.add(bookList.get(j));
                }
            }
            bookCategoryList.put(newCategories.get(i), listBook);
        }
        bookCategoriesAdapter.setData(newCategories, bookCategoryList);
    }

    private void checkData(int action, String sort, String type) {

        ACTION = action;
        SORT_BY = sort;
        TYPE = type;

        if (newCategories.size() == 0) {
            removeExpandableListView();
            addRecyclerView();
        } else {
            removeRecyclerView();
            addExpandableListView();
        }
    }

    public int getListPositionByName(List<String> list, String name){
        for(int i = 0; i < list.size(); i++){
            if (list.get(i).equals(name)) {
                return i;
            }
        }
        return -1;
    }


}