package suciu.alexandru.com.bookwormscommunity.activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import suciu.alexandru.com.bookwormscommunity.activities.MainActivity;
import suciu.alexandru.com.bookwormscommunity.test.MyBooksActivity;
import suciu.alexandru.com.bookwormscommunity.utils.AppSharedPreferences;
import suciu.alexandru.com.bookwormscommunity.utils.CircleTransform;
import suciu.alexandru.com.bookwormscommunity.utils.Constants;
import suciu.alexandru.com.bookwormscommunity.adapters.DrawerAdapter;
import suciu.alexandru.com.bookwormscommunity.adapters.DrawerFragment;
import suciu.alexandru.com.bookwormscommunity.adapters.DrawerItem;
import suciu.alexandru.com.bookwormscommunity.R;

/**
 * Created by Alexandru on 13.05.2016.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final int BOOKS = 7;
    public static final int PROFILE = 1;
    public static final int MYBOOKS = 2;
    public static final int RECOMMENDATIONS = 3;
    public static final int SEARCH = 4;
    public static final int NEWS = 5;
    public static final int LOGOUT = 6;

    private Class nextActivity;
    public static String toolbarTitle = Constants.NEWS;
    public static int currActions = NEWS;
    protected Toolbar toolbar;
    private DrawerFragment drawerFragment;

    //drawer User and Photo
    TextView tvUsernameDrawer;
    ImageView imgUsernamePhoto;

    public static LinearLayout llNews, llMyBooks, llRecommendations, llMyProfileDrawer;

    public static TextView tvMyBooks, tvNews, tvRecommendations, tvNoData;

    public static LinearLayout llContainer;
    public static Spinner spinnerToolbar;

    private View viewMyBooks, viewNews, viewRec;


    protected void initialize() {
        llContainer = (LinearLayout) findViewById(R.id.containerLinearLayout);

        spinnerToolbar = (Spinner) findViewById(R.id.spinnerToolbar);

        tvNoData = (TextView) findViewById(R.id.tvNoDataFound);

        viewMyBooks = (View) findViewById(R.id.viewMyBooks);
        viewNews = (View) findViewById(R.id.viewNews);
        viewRec = (View) findViewById(R.id.viewRec);

        llNews = (LinearLayout) findViewById(R.id.layoutNews);
        llMyBooks = (LinearLayout) findViewById(R.id.layoutMyBooks);
        llRecommendations = (LinearLayout) findViewById(R.id.layoutRecom);
        llMyProfileDrawer = (LinearLayout) findViewById(R.id.llMyProfileDrawer);

        tvMyBooks = (TextView) findViewById(R.id.tvMyBooks);
        tvNews = (TextView) findViewById(R.id.tvNews);
        tvRecommendations = (TextView) findViewById(R.id.tvRecom);

        unsetNoData();

        llNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
                intent.putExtra(Constants.NEWS, true);
                startActivity(intent);
                finish();
            }
        });

        llMyBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BooksActivity.class);
                intent.putExtra(Constants.MY_BOOKS, true);
                startActivity(intent);
                finish();
            }
        });

        llRecommendations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SuggestionsActivity.class);
                intent.putExtra(Constants.RECOMMENDATIONS, true);
                startActivity(intent);
                finish();
            }
        });
    }

    protected Toolbar activateToolbar() {
        if (toolbar == null) {
            toolbar = (Toolbar) findViewById(R.id.app_bar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                if (currActions == PROFILE) {
                    toolbar.setTitle(Constants.PROFILE);
                } else if (currActions == RECOMMENDATIONS) {
                    toolbar.setTitle(Constants.RECOMMENDATIONS);
                } else if (currActions == NEWS) {
                    toolbar.setTitle(Constants.NEWS);
                } else if (currActions == SEARCH) {
                    toolbar.setTitle(Constants.SEARCH_BOOKS);
                } else if (currActions == MYBOOKS) {
                    toolbar.setTitle(Constants.MY_BOOKS);
                } else if (currActions == LOGOUT) {
                    toolbar.setTitle(Constants.LOG_OUT);
                }
            }
        }
        return toolbar;
    }

    protected Toolbar activateToolbarHome() {
        activateToolbar();
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (currActions == MYBOOKS)
                getSupportActionBar().setTitle(Constants.MY_BOOKS);
            else if (currActions == RECOMMENDATIONS)
                getSupportActionBar().setTitle(Constants.RECOMMENDATIONS);
            else if (currActions == NEWS)
                getSupportActionBar().setTitle(Constants.NEWS);
        }
        return toolbar;
    }


    protected void setDrawerItems() {

        tvUsernameDrawer = (TextView) findViewById(R.id.tvProfileUsernameDrawer);
        imgUsernamePhoto = (ImageView) findViewById(R.id.imgProfilePhotoDrawer);

        String username = AppSharedPreferences.getUsername(getApplicationContext(), Constants.USERNAME);
        String user_id = AppSharedPreferences.getUserId(getApplicationContext(), Constants.USER_ID);
        String photo = Constants.PHOTO_URL + user_id + "Photo.jpg";

        ListView drawerListView;

        List<DrawerItem> items = new ArrayList<>();
        items.add(new DrawerItem(Constants.PROFILE, R.drawable.ic_person_white_24dp));
        items.add(new DrawerItem(Constants.MY_BOOKS, R.drawable.book));
        items.add(new DrawerItem(Constants.RECOMMENDATIONS, R.drawable.ic_rec));
        items.add(new DrawerItem(Constants.NEWS, android.R.drawable.ic_menu_today));
        items.add(new DrawerItem(Constants.SEARCH_BOOKS, android.R.drawable.ic_menu_search));
        items.add(new DrawerItem(Constants.LOG_OUT, android.R.drawable.ic_lock_power_off));



        drawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        drawerFragment.initializeDrawer(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar);

        DrawerAdapter drawerAdapter = new DrawerAdapter(getApplicationContext(), items);


        drawerListView = (ListView) findViewById(R.id.fragmentdrawerItems);
        drawerListView.setAdapter(drawerAdapter);

        Picasso.with(this).load(photo)
                .placeholder(R.drawable.placeholder_profile_circle)
                .error(R.drawable.placeholder_profile_circle)
                .transform(new CircleTransform())
                .into(imgUsernamePhoto);

        tvUsernameDrawer.setText(username);

        llMyProfileDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseActivity.this, ProfileActivity.class);
                startActivity(intent);
                drawerFragment.closeDrawer();
            }
        });

        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        nextActivity = ProfileActivity.class;
                        changeActivity();
                        drawerFragment.closeDrawer();
                        break;
                    case 1:
                        nextActivity = MainActivity.class;
                        currActions = MYBOOKS;
                        toolbarTitle = Constants.MY_BOOKS;
                        toolbar.setTitle("");
                        changeActivity();
                        break;
                    case 2:
                        nextActivity = SuggestionsActivity.class;
                        currActions = RECOMMENDATIONS;
                        toolbarTitle = Constants.RECOMMENDATIONS;
                        toolbar.setTitle(Constants.RECOMMENDATIONS);
                        changeActivity();
                        finish();
                        break;
                    case 3:
                        nextActivity = NewsActivity.class;
                        currActions = NEWS;
                        toolbarTitle = Constants.NEWS;
                        toolbar.setTitle(Constants.NEWS);
                        changeActivity();
                        finish();
                        break;
                    case 4:
                        nextActivity = SearchActivity.class;
                        changeActivity();
                        break;
                    case 5:
                        logout();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    protected void activateSpinner() {

        if (currActions == MYBOOKS) {
            toolbar.setTitle("");
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(toolbar.getContext(),
                    R.array.CategoryMyBooks, R.layout.spinner_item_layout);
            spinnerToolbar.setAdapter(adapter);
            spinnerToolbar.setVisibility(View.VISIBLE);
        } else if (currActions == RECOMMENDATIONS) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(toolbar.getContext(),
                    R.array.CategoryMyRec, R.layout.spinner_item_layout);
            spinnerToolbar.setAdapter(adapter);
            spinnerToolbar.setVisibility(View.VISIBLE);
        } else if (currActions == BOOKS) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(toolbar.getContext(),
                    R.array.CategoryMyRec, R.layout.spinner_item_layout);
            spinnerToolbar.setAdapter(adapter);
            spinnerToolbar.setVisibility(View.VISIBLE);
        }
    }

    protected void deactivateSpinner() {
        spinnerToolbar.setVisibility(View.GONE);
    }

    protected void setCurrActions(int action) {
        currActions = action;
    }

    protected void setCurrActionsStyle() {
        switch (currActions) {
            case MYBOOKS :
                viewMyBooks.setVisibility(View.VISIBLE);
                viewNews.setVisibility(View.INVISIBLE);
                viewRec.setVisibility(View.INVISIBLE);
                break;
            case NEWS :
                viewMyBooks.setVisibility(View.INVISIBLE);
                viewNews.setVisibility(View.VISIBLE);
                viewRec.setVisibility(View.INVISIBLE);
                break;
            case RECOMMENDATIONS :
                viewMyBooks.setVisibility(View.INVISIBLE);
                viewNews.setVisibility(View.INVISIBLE);
                viewRec.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void changeActivity() {
        Intent intent = new Intent(BaseActivity.this, nextActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0, 0);
        drawerFragment.closeDrawer();
    }

    public void logout() {
        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_logout);
        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        dialog.show();

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppSharedPreferences.stopSession(getApplicationContext());
                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_searchs) {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra(Constants.SEARCH, "aga");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
