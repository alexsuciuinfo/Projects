package suciu.alexandru.com.bookwormscommunity.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import suciu.alexandru.com.bookwormscommunity.utils.AppSharedPreferences;
import suciu.alexandru.com.bookwormscommunity.models.BookDetails;
import suciu.alexandru.com.bookwormscommunity.models.BookReview;
import suciu.alexandru.com.bookwormscommunity.adapters.BookReviewsAdapter;
import suciu.alexandru.com.bookwormscommunity.utils.CircleTransform;
import suciu.alexandru.com.bookwormscommunity.utils.Constants;
import suciu.alexandru.com.bookwormscommunity.R;
import suciu.alexandru.com.bookwormscommunity.utils.RecyclerItemClickListener;
import suciu.alexandru.com.bookwormscommunity.models.Review;
import suciu.alexandru.com.bookwormscommunity.services.ServiceManager;
import suciu.alexandru.com.bookwormscommunity.models.User;
import suciu.alexandru.com.bookwormscommunity.utils.Util;

public class BookDetailsActivity extends AppCompatActivity {

    private static final int EDIT = 1, ADD = 2, READ = 1, WANTED = 2, NOT_ADDED = 3;
    private static ImageView imgBookDetail, imgMyPhoto, imgUserPhoto;
    private TextView tvTitle, tvAuthor, tvRating, tvMyReviewSetting, tvMyReview, tvMyReviewDate, tvDescription, tvNrRead;
    private RatingBar ratingBarBook, ratingBarMyRating;
    public static Review userReview;
    public static RecyclerView recyclerView;
    Spinner spinner;
    private static BookReviewsAdapter bookReviewsAdapter;
    private static List<BookReview> bookReviewsList;
    private Button btnStatus, btnAddReview, btnCancelReview;
    private static BookDetails bookDetails;
    private static User user;
    private static String API_KEY;
    private static int bookId;
    private static ServiceManager serviceManager;
    private LinearLayout llMyReview, llAddReview;
    private EditText etMyReview;
    public static int ACTION;
    private static Toolbar toolbar;
    public static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_details_activity);

        if (Integer.valueOf(getIntent().getIntExtra(Constants.BOOK_ID, 0)) != null) {
            bookId = getIntent().getIntExtra(Constants.BOOK_ID, 0);
        }

        API_KEY = AppSharedPreferences.getApiKey(getApplicationContext(), Constants.ApiKey);
        serviceManager = new ServiceManager();

        initialize();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.show();

        getBookDetails();
        getUserInfo();
        getMyReviewDetails();

        setRecyclerView();
        setBookReviews();
    }

    private void initialize() {
        imgBookDetail = (ImageView) findViewById(R.id.imgBookDetails);
        tvTitle = (TextView) findViewById(R.id.tvBookDetailsTitle);
        tvAuthor = (TextView) findViewById(R.id.tvBookDetailsAuthor);
        tvRating = (TextView) findViewById(R.id.tvRatingBookDetail);
        tvDescription = (TextView) findViewById(R.id.tvBookDescription);
        tvNrRead = (TextView) findViewById(R.id.tvBookDetailsNrRead) ;
        ratingBarBook = (RatingBar) findViewById(R.id.ratingBarDetailsBook);
        ratingBarMyRating = (RatingBar) findViewById(R.id.ratingBarMyRating);
        btnStatus = (Button) findViewById(R.id.btnStatus);
        tvMyReview = (TextView) findViewById(R.id.tvMyReview);
        tvMyReviewSetting = (TextView) findViewById(R.id.tvReviewSetting);
        imgMyPhoto = (ImageView) findViewById(R.id.imgMyPhoto);
        tvMyReviewDate = (TextView) findViewById(R.id.tvMyReviewDate);
        btnCancelReview = (Button) findViewById(R.id.btnCancelReview);
        btnAddReview = (Button) findViewById(R.id.btnAddReview);
        llMyReview = (LinearLayout) findViewById(R.id.llMyReview);
        llAddReview = (LinearLayout) findViewById(R.id.llAddReview);
        etMyReview = (EditText) findViewById(R.id.etAddReview);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvMyReviewSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvMyReviewSetting.getText().toString().equals(Constants.ADD_REVIEW)) {
                    llMyReview.setVisibility(View.GONE);
                    llAddReview.setVisibility(View.VISIBLE);
                    ACTION = ADD;
                } else if (tvMyReviewSetting.getText().toString().equals(Constants.EDIT_REVIEW)) {
                    llMyReview.setVisibility(View.GONE);
                    llAddReview.setVisibility(View.VISIBLE);
                    etMyReview.setText(userReview.getMessage());
                    ACTION = EDIT;
                }
            }
        });

        btnAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userReview.setMessage(etMyReview.getText().toString());
                llAddReview.setVisibility(View.GONE);
                llMyReview.setVisibility(View.VISIBLE);
                userReview.setDate(Util.getCurrentDate());
                setMyReviewDetails();
                addMyReview(ACTION);
            }
        });

        btnCancelReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llAddReview.setVisibility(View.GONE);
                llMyReview.setVisibility(View.VISIBLE);
            }
        });

        ratingBarMyRating.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    final Dialog dialog = new Dialog(BookDetailsActivity.this);
                    dialog.setContentView(R.layout.rate_book_dialog);
                    RatingBar newRating = (RatingBar) dialog.findViewById(R.id.ratingBarDialog);
                    newRating.setRating(ratingBarMyRating.getRating());
                    Log.d("acum", Float.toString(newRating.getRating()));
                    newRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                            if (b) {
                                ratingBarMyRating.setRating(v);
                                dialog.dismiss();
                                editRating();
                            }
                        }
                    });

                    Button btnRemove = (Button) dialog.findViewById(R.id.btnDialog);
                    if (ratingBarMyRating.getRating() != 0) {
                        btnRemove.setVisibility(View.VISIBLE);
                        btnRemove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ratingBarMyRating.setRating(0);
                                dialog.dismiss();
                                deleteRating();
                            }
                        });
                    }
                    dialog.show();
                }
                return true;
            }
        });

        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(BookDetailsActivity.this, view);
                popupMenu.inflate(R.menu.popup_menu_book_status);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_read :
                                setBookStatus(READ);
                                return true;
                            case R.id.action_wanted:
                                setBookStatus(WANTED);
                                return true;
                            case R.id.action_notAdded :
                                setBookStatus(NOT_ADDED);
                                return true;
                            default:
                                return true;
                        }
                    }
                });
            }
        });

    }


    private void setRecyclerView() {
        bookReviewsAdapter = new BookReviewsAdapter(BookDetailsActivity.this, new ArrayList<BookReview>());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewReview);
        final GridLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(bookReviewsAdapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {

                        view.findViewById(R.id.tvReviewUsername).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(BookDetailsActivity.this, ProfileActivity.class);
                                intent.putExtra(Constants.USER_ID, bookReviewsList.get(position).getUser().getUserId());
                                startActivity(intent);
                            }
                        });

                        view.findViewById(R.id.imgUserPhotoReview).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(BookDetailsActivity.this, ProfileActivity.class);
                                intent.putExtra(Constants.USER_ID, bookReviewsList.get(position).getUser().getUserId());
                                startActivity(intent);
                            }
                        });

                    }
                })
        );
    }

    public void setUserInfo() {
        Picasso.with(this).load(user.getPhoto()).error(R.drawable.placeholder_profile_circle)
                .placeholder(R.drawable.placeholder_profile_circle)
                .transform(new CircleTransform())
                .into(imgMyPhoto);
    }

    private void setBookDetailsComponents() {
        toolbar.setTitle(bookDetails.getBook().getTitle());
        Picasso.with(this).load(bookDetails.getBook().getImageURL()).error(R.drawable.book_loading)
                .placeholder(R.drawable.book_loading)
                .into(imgBookDetail);
        tvTitle.setText(bookDetails.getBook().getTitle());
        if(Double.valueOf(bookDetails.getBook().getRating()) != null) {
            tvRating.setText(Double.toString(bookDetails.getBook().getRating()) + " (" + bookDetails.getBook().getNrRatings() + "no. of ratings)");
        } else {
            tvRating.setText(" 0.0 " + bookDetails.getBook().getNrRatings() + " ratings");
        }
        tvDescription.setText(bookDetails.getBook().getDescription());
        tvNrRead.setText(bookDetails.getBook().getNrRead() + " no. of readers");
        tvAuthor.setText("by " + bookDetails.getBook().getAuthor());
        ratingBarBook.setRating((float) bookDetails.getBook().getRating());
        if (Double.valueOf(bookDetails.getMyRating()) != null)
            ratingBarMyRating.setRating((float) bookDetails.getMyRating());

        if (bookDetails.getReadBook() != 0) {
            btnStatus.setText("Read");
        } else if (bookDetails.getWantBook() != 0) {
            btnStatus.setText("Wanted");
        } else {
            btnStatus.setText("Not Added");
        }
    }

    private void setMyReviewDetails() {
        userReview.setBookId(bookId);

        if (userReview.getMessage() != null && !userReview.getMessage().isEmpty()) {
            tvMyReviewSetting.setText(Constants.EDIT_REVIEW);
            tvMyReviewSetting.setTextColor(getResources().getColor(R.color.textSecondaryColor));
            tvMyReview.setText(userReview.getMessage());
            tvMyReviewDate.setText(userReview.getDate().toString());
        } else {
            tvMyReviewSetting.setText(Constants.ADD_REVIEW);
            tvMyReviewSetting.setTextColor(getResources().getColor(R.color.textSecondaryColor));
            tvMyReview.setText("NO REVIEW");
            tvMyReview.setGravity(Gravity.FILL_HORIZONTAL);
            tvMyReviewDate.setVisibility(View.GONE);
        }
    }


    public void setButtonStatus(int buttonStatus) {
        if (buttonStatus == READ) {
            btnStatus.setText("READ");
        } else if (buttonStatus == WANTED) {
            btnStatus.setText("WANTED");
        } else {
            btnStatus.setText("NOT ADDED");
        }
    }

    private void getUserInfo() {
        Call<User> call = serviceManager.getApiInterface().getUserInfo(API_KEY);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    setUserInfo();
                } else {
                    Toast.makeText(BookDetailsActivity.this, Constants.REQUEST_FAILED, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(BookDetailsActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBookReviews() {
        Call call = serviceManager.getApiInterface().getBookReviews(API_KEY, bookId);
        call.enqueue(new Callback<List<BookReview>>() {
            @Override
            public void onResponse(Call<List<BookReview>> call, Response<List<BookReview>> response) {
                progressDialog.cancel();
                if (response.isSuccessful()) {
                    bookReviewsList = response.body();
                    bookReviewsAdapter.setBookReviewList(bookReviewsList);
                } else {
                    Toast.makeText(BookDetailsActivity.this, Constants.REQUEST_FAILED, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(BookDetailsActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addMyReview(int action) {
        Call call;
        if (action == ADD) {
            call = serviceManager.getApiInterface().addReview(API_KEY, userReview);
        } else {
            call = serviceManager.getApiInterface().editReview(API_KEY, userReview);
        }

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    setButtonStatus(READ);
                } else {
                    Toast.makeText(BookDetailsActivity.this, Constants.REQUEST_FAILED, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(BookDetailsActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings)
            return true;
        return super.onOptionsItemSelected(item);
    }

    private void getBookDetails() {
        Call<BookDetails> call = serviceManager.getApiInterface().getBookInfo(API_KEY, bookId);
        call.enqueue(new Callback<BookDetails>() {
            @Override
            public void onResponse(Call<BookDetails> call, Response<BookDetails> response) {
                if (response.isSuccessful()) {
                    bookDetails = response.body();
                    setBookDetailsComponents();
                } else {
                    Toast.makeText(BookDetailsActivity.this, Constants.REQUEST_FAILED, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<BookDetails> call, Throwable t) {
                Toast.makeText(BookDetailsActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }


    public void getMyReviewDetails() {
        Call<Review> call = serviceManager.getApiInterface().getUserReview(API_KEY, bookId);
        call.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful()) {
                    userReview = response.body();
                    setMyReviewDetails();
                } else {
                    Toast.makeText(BookDetailsActivity.this, Constants.REQUEST_FAILED, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(BookDetailsActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editRating() {
        Call call = serviceManager.getApiInterface().addRating(API_KEY, bookId, ratingBarMyRating.getRating(), Util.getCurrentDate());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    if (response.body().equals("OK"))
                    setButtonStatus(READ);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(BookDetailsActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteRating() {
        Call call = serviceManager.getApiInterface().removeRating(API_KEY, bookId);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(BookDetailsActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setBookStatus(final int action) {
        Call call;
        if (action == READ) {
            call = serviceManager.getApiInterface().addBookToRead(API_KEY, bookId, Util.getCurrentDate());
        } else if (action == WANTED) {
            call = serviceManager.getApiInterface().addBookToWanted(API_KEY, bookId, Util.getCurrentDate());
            ratingBarMyRating.setRating(0);
            setNoReview();
        } else {
            call = serviceManager.getApiInterface().removeBookFromUserBooks(API_KEY, bookId);
            ratingBarMyRating.setRating(0);
            setNoReview();
        }

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    if (response.body().equals("OK")) {
                        setButtonStatus(action);
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                    Toast.makeText(BookDetailsActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setNoReview() {
        userReview.setMessage("");
        etMyReview.setText("");
        setMyReviewDetails();
    }

}

