package suciu.alexandru.com.bookwormscommunity.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import suciu.alexandru.com.bookwormscommunity.test.MyBooksActivity;
import suciu.alexandru.com.bookwormscommunity.utils.AppSharedPreferences;
import suciu.alexandru.com.bookwormscommunity.utils.CircleTransform;
import suciu.alexandru.com.bookwormscommunity.utils.Constants;
import suciu.alexandru.com.bookwormscommunity.R;
import suciu.alexandru.com.bookwormscommunity.services.ServiceManager;
import suciu.alexandru.com.bookwormscommunity.models.User;

public class ProfileActivity extends AppCompatActivity {
    
    private TextView tvFullName, tvUsername, tvBirthdate, tvLocation, tvGender, tvAbout;
    private Button btnBooks, btnBooksRead, btnBooksWanted, btnRatings, btnReviews;
    private ImageView imgProfilePhoto;
    private FloatingActionButton fabEdit;
    private static ServiceManager serviceManager;
    private static User user;
    public static String API_KEY, USER_ID;
    private static Toolbar toolbar;
    public static final int ALL = 0, READ = 1, WANTED = 2;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        serviceManager = new ServiceManager();
        initializeComponents();
        API_KEY = AppSharedPreferences.getApiKey(getApplicationContext(), Constants.ApiKey);
        USER_ID = AppSharedPreferences.getUserId(getApplicationContext(), Constants.USER_ID);
        String currUserId = getIntent().getStringExtra(Constants.USER_ID);
        if (currUserId != null) {
            if (!USER_ID.equals(currUserId)) {
                fabEdit.setVisibility(View.GONE);
                USER_ID = currUserId;
            }
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        getProfileData();
        
        ImageView imageView = (ImageView) findViewById(R.id.imgMyProfilePhoto);

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.show();
        getProfileData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setProfileData() {
        tvFullName.setText(user.getFirstname() + " " + user.getLastname());
        tvUsername.setText(user.getUsername());
        tvBirthdate.setText(user.getBirthdate());
        tvLocation.setText(user.getLocation());
        tvGender.setText(user.getGender());
        tvAbout.setText(user.getAbout());
        btnRatings.setText("Ratings\n" + Integer.toString(user.getNrRatings()));
        btnReviews.setText("Review\n" + Integer.toString(user.getNrReviews()));
        btnBooksRead.setText("Read\n" + Integer.toString(user.getNrReadBooks()));
        btnBooksWanted.setText("Wanted\n" + Integer.toString(user.getNrWantedBooks()));
        String booksRead = Integer.toString(user.getNrWantedBooks() + user.getNrReadBooks());
        btnBooks.setText("Books\n" + booksRead);

        toolbar.setTitle(user.getFirstname() + " " + user.getLastname() + " Profile");

        Picasso.with(this)
                .load(user.getPhoto())
                .error(R.drawable.placeholder_profile_circle)
                .placeholder(R.drawable.placeholder_profile_circle)
                .transform(new CircleTransform()).into(imgProfilePhoto);
    }

    private void initializeComponents() {

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvFullName = (TextView) findViewById(R.id.tvProfileFullName);
        tvUsername = (TextView) findViewById(R.id.tvProfileUsername);
        tvBirthdate = (TextView) findViewById(R.id.tvProfileBirthdate);
        tvGender = (TextView) findViewById(R.id.tvProfileGender);
        tvAbout = (TextView) findViewById(R.id.tvProfileAbout);
        tvLocation = (TextView) findViewById(R.id.tvProfileLocation);
        
        btnBooks = (Button) findViewById(R.id.btnBooks);
        btnBooksRead = (Button) findViewById(R.id.btnBooksRead);
        btnBooksWanted = (Button) findViewById(R.id.btnBooksWanted);
        btnRatings = (Button) findViewById(R.id.btnRatings);
        btnReviews = (Button) findViewById(R.id.btnReviews);

        imgProfilePhoto = (ImageView) findViewById(R.id.imgMyProfilePhoto);
        
        fabEdit = (FloatingActionButton) findViewById(R.id.fabEditProfile);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        setButtons();
    }

    private void setButtons() {
        btnBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.putExtra(Constants.USER_ID, USER_ID);
                intent.putExtra(Constants.SEARCH_TYPE, ALL);
                startActivity(intent);
            }
        });

        btnBooksRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.putExtra(Constants.USER_ID, USER_ID);
                intent.putExtra(Constants.SEARCH_TYPE, READ);
                startActivity(intent);
            }
        });

        btnBooksWanted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.putExtra(Constants.USER_ID, USER_ID);
                intent.putExtra(Constants.SEARCH_TYPE, WANTED);
                startActivity(intent);
            }
        });
    }

    private void getProfileData() {
        Call call  = serviceManager.getApiInterface().getUserInfo(API_KEY, USER_ID);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    user = response.body();
                    setProfileData();
                } else {
                    Toast.makeText(ProfileActivity.this, Constants.REQUEST_FAILED, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
