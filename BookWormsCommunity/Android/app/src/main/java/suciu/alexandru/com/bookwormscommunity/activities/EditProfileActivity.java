package suciu.alexandru.com.bookwormscommunity.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import suciu.alexandru.com.bookwormscommunity.utils.AppSharedPreferences;
import suciu.alexandru.com.bookwormscommunity.utils.CircleTransform;
import suciu.alexandru.com.bookwormscommunity.utils.Constants;
import suciu.alexandru.com.bookwormscommunity.models.LoginRegisterModel;
import suciu.alexandru.com.bookwormscommunity.R;
import suciu.alexandru.com.bookwormscommunity.services.ServiceManager;
import suciu.alexandru.com.bookwormscommunity.models.User;

public class EditProfileActivity extends AppCompatActivity {


    private static TextView tvEditProfileMessage;
    private TextInputLayout tvUsername;
    private TextInputLayout tvPassword;
    private TextInputLayout tvFirstname;
    private TextInputLayout tvLastname;
    private static EditText etUsername, etFirstname, etLastname, etBirthdate, etLocation, etAbout;
    private Spinner spinnerGender;
    private ImageView imgEditProfilePhoto;
    LinearLayout llBirthdate;
    private static ServiceManager serviceManager;
    private static User user;
    public static String API_KEY, USER_ID;
    public static int CAMERA_CODE = 10;
    private Bitmap photo;
    public static String encoded_string;
    public static boolean USERNAME_IS_CORRECT = false, FIRSTNAME_IS_CORRECT = false, LASTNAME_IS_CORRECT = false, PASSWORD_IS_CORRECT = false;
    public static String USERNAME_ERROR = "Username must contains between 6 and 20 chars";
    public static String FIRSTNAME_ERROR = "Only letters are allowed";
    public static String LASTNAME_ERROR = "Only letters are allowed";
    public static String BIRTHDATE_ERROR = "Enter a valid date";
    public static String PASSWORD_ERROR = "Password must contains between 6 and 15 characters";
    public static int NOPHOTO = 1, ADDPHOTO = 2;
    public static String ACTION;
    private ProgressDialog progressDialog;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_profile);
        API_KEY = AppSharedPreferences.getApiKey(getApplicationContext(), Constants.ApiKey);
        USER_ID = AppSharedPreferences.getUserId(getApplicationContext(), Constants.USER_ID);
        serviceManager = new ServiceManager();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        initializeComponents();
        getProfileData();
        setOnTextChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            if (allInputsAreCorrect() && allRequiredFieldsAreCompleted()) {
                progressDialog.show();
                editProfile();
            } else {
                tvEditProfileMessage.setText("Please complete all required data");
                tvEditProfileMessage.setVisibility(View.VISIBLE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void initializeComponents() {

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        if (toolbar != null) {
            toolbar.setTitle("Edit Profile");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etUsername = (EditText) findViewById(R.id.etUsernameEdit);
        etFirstname = (EditText) findViewById(R.id.etFirstnameEdit);
        etLastname = (EditText) findViewById(R.id.etLastnameEdit);
        etBirthdate = (EditText) findViewById(R.id.etBirthdate);
        etBirthdate.setKeyListener(null);
        etLocation = (EditText) findViewById(R.id.etProfileEditLocation);
        etAbout = (EditText) findViewById(R.id.etProfileEditAbout);

        tvFirstname = (TextInputLayout) findViewById(R.id.tilFirstnameEdit);
        tvLastname = (TextInputLayout) findViewById(R.id.tilLastnameEdit);
        tvUsername = (TextInputLayout) findViewById(R.id.tilUsernameEdit);


        imgEditProfilePhoto = (ImageView) findViewById(R.id.imgPhotoProfileEdit);

        tvEditProfileMessage = (TextView) findViewById(R.id.tvEditProfileMessage);

        llBirthdate = (LinearLayout) findViewById(R.id.llBirthdate);

        spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Gender, R.layout.spinner_gender_layout_item);
        spinnerGender.setAdapter(adapter);


        llBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BirthdateDialog birthdateDialog = new BirthdateDialog();
                birthdateDialog.show(getSupportFragmentManager(), Constants.BIRTHDATE);
            }
        });

        imgEditProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(EditProfileActivity.this, view);
                popupMenu.inflate(R.menu.popup_menu_photo_status);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_choose :
                                setPhotoAction(ADDPHOTO);
                                return true;
                            case R.id.action_noPhoto:
                                setPhotoAction(NOPHOTO);
                                return true;
                            default:
                                return true;
                        }
                    }
                });
            }
        });
    }

    private void setPhotoAction(int action) {
        if (action == NOPHOTO) {
            user.setEncryptedPhoto(Constants.NO_PHOTO);
            ACTION = Constants.NO_PHOTO;
            imgEditProfilePhoto.setImageResource(R.drawable.profile_placeholder);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent, CAMERA_CODE);
        }
    }


    private void setOnTextChanged() {

        final String regex = "^[\\p{L} .'-]+$";

        etFirstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String firstname = editable.toString();
                if (!firstname.toString().matches(regex)) {
                    tvFirstname.setErrorEnabled(true);
                    tvFirstname.setError(FIRSTNAME_ERROR);
                    FIRSTNAME_IS_CORRECT = false;
                } else {
                    tvFirstname.setError(null);
                    FIRSTNAME_IS_CORRECT = true;
                }
            }
        });

        etLastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String lastname = editable.toString();
                if (!lastname.toString().matches(regex)) {
                    tvLastname.setErrorEnabled(true);
                    tvLastname.setError(LASTNAME_ERROR);
                    LASTNAME_IS_CORRECT = false;
                    Log.d("Aplica", lastname.toString());
                } else {
                    tvLastname.setError(null);
                    LASTNAME_IS_CORRECT = true;
                }

            }
        });

        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String username = editable.toString();
                if (username != null) {
                    if (!(username.length() >= 6 && username.length() <= 20)) {
                        tvUsername.setErrorEnabled(true);
                        tvUsername.setError(USERNAME_ERROR);
                        USERNAME_IS_CORRECT = false;
                    } else {
                        tvUsername.setError(null);
                        USERNAME_IS_CORRECT = true;
                    }
                }
            }
        });
    }

    public boolean allRequiredFieldsAreCompleted() {
        if (etUsername.getText().toString().trim().equals("") ||
                etFirstname.getText().toString().trim().equals("") ||
                etLastname.getText().toString().trim().equals("") ||
                etBirthdate.getText().toString().trim().equals("")) {
            return false;
        }
        return true;
    }

    public String getPhotoString(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String encoded_string = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encoded_string;
    }

    //BirthDate Dialog
    public static class BirthdateDialog extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private int curr_day, curr_month, curr_year;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            curr_year = calendar.get(Calendar.YEAR);
            curr_month = calendar.get(Calendar.MONTH);
            curr_day = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, curr_year, curr_month, curr_day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String today = curr_year + "-" + curr_month + "-" + curr_day;
            String choosen = year + "-" + month + "-" + day;
            Date todayDate = Date.valueOf(today);
            Date choosenDate = Date.valueOf(choosen);
            if (todayDate.compareTo(choosenDate) < 0) {
                tvEditProfileMessage.setText("Invalid date");
                tvEditProfileMessage.setVisibility(View.VISIBLE);
            } else {
                etBirthdate.setText(choosen);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {
            Uri image = data.getData();
            try {
                photo = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
                Picasso.with(this)
                        .load(image)
                        .error(R.drawable.placeholder_profile_circle)
                        .placeholder(R.drawable.placeholder_profile_circle)
                        .transform(new CircleTransform()).into(imgEditProfilePhoto);
                encoded_string = getPhotoString(photo);
                user.setEncryptedPhoto(encoded_string);
                ACTION = encoded_string;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            user.setEncryptedPhoto(ACTION);
        }
    }

    private boolean allInputsAreCorrect() {
        if (USERNAME_IS_CORRECT && FIRSTNAME_IS_CORRECT && LASTNAME_IS_CORRECT)
            return true;
        return false;
    }

    private void setProfileData() {
        etFirstname.setText(user.getFirstname());
        etLastname.setText(user.getLastname());
        etUsername.setText(user.getUsername());
        etBirthdate.setText(user.getBirthdate());
        etLocation.setText(user.getLocation());
        etAbout.setText(user.getAbout());
        user.setEncryptedPhoto(Constants.NO_CHANGE);
        ACTION = Constants.NO_CHANGE;
        if (user.getGender() == null) {
            spinnerGender.setSelection(0);
        } else if (user.getGender().equals("male")) {
            spinnerGender.setSelection(1);
        } else if (user.getGender().equals("female")) {
            spinnerGender.setSelection(2);
        }

        Picasso.with(this)
                .load(user.getPhoto())
                .error(R.drawable.placeholder_profile_circle)
                .placeholder(R.drawable.placeholder_profile_circle)
                .transform(new CircleTransform()).into(imgEditProfilePhoto);
    }

    public  void setUserNewData() {
        user.setUsername(etUsername.getText().toString());
        user.setLastname(etLastname.getText().toString());
        user.setFirstname(etFirstname.getText().toString());
        if (spinnerGender.getSelectedItemPosition() == 0) {
            user.setGender("");
        } else if (spinnerGender.getSelectedItemPosition() == 1) {
            user.setGender("male");
        } else {
            user.setGender("female");
        }
        user.setBirthdate(etBirthdate.getText().toString());
        user.setAbout(etAbout.getText().toString());
        user.setLocation(etLocation.getText().toString());
    }


    private void getProfileData() {
        Call call  = serviceManager.getApiInterface().getUserInfo(API_KEY, USER_ID);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()) {
                    user = response.body();
                    setProfileData();
                } else {
                    Toast.makeText(EditProfileActivity.this, Constants.REQUEST_FAILED, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(EditProfileActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editProfile() {
        setUserNewData();
        Call<LoginRegisterModel> call = serviceManager.getApiInterface().editUserInfo(API_KEY, user);
        call.enqueue(new Callback<LoginRegisterModel>() {
            @Override
            public void onResponse(Call<LoginRegisterModel> call, Response<LoginRegisterModel> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().isError()) {
                        tvEditProfileMessage.setText(response.body().getMessage());
                        tvEditProfileMessage.setVisibility(View.VISIBLE);
                    } else {
                        finish();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, Constants.POST_FAILED, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginRegisterModel> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });

    }
}
