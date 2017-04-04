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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

;

public class RegisterActivity extends AppCompatActivity {

    private static final String PASSWORD_ERROR_SPACE = "No spaces are allowed";
    private static final String USERNAME_ERROR_SPACE = "No spaces are allowed";
    private EditText etUsername;
    private EditText etPassword;
    private EditText etFirstname;
    private EditText etLastname;
    private static EditText etBirthdate;
    private ImageView imgProfilePhoto;
    private TextInputLayout tvUsername;
    private TextInputLayout tvPassword;
    private TextInputLayout tvFirstname;
    private TextInputLayout tvLastname;
    private static TextView tvBirthdate;
    private static TextView tvRegisterMessage;
    private Button btnRegister;
    private Button btnLogin;
    private String year, month, day;
    public static int CAMERA_CODE = 10;
    private Bitmap photo;
    public static String encoded_string;
    public static boolean USERNAME_IS_CORRECT = false, FIRSTNAME_IS_CORRECT = false, LASTNAME_IS_CORRECT = false, PASSWORD_IS_CORRECT = false;
    public static String USERNAME_ERROR = "Username must contains between 6 and 20 chars";
    public static String FIRSTNAME_ERROR = "Only letters are allowed";
    public static String LASTNAME_ERROR = "Only letters are allowed";
    public static String BIRTHDATE_ERROR = "Enter a valid date";
    public static String PASSWORD_ERROR = "Password must contains between 6 and 20 characters";
    private static ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        initializeComponents();
        setOnTextChanged();

        etBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BirthdateDialog birthdateDialog = new BirthdateDialog();
                birthdateDialog.show(getSupportFragmentManager(), Constants.BIRTHDATE);
            }
        });

        imgProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, CAMERA_CODE);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allRequiredFieldsAreCompleted() && allInputsAreCorrect()) {
                    ServiceManager serviceManager = new ServiceManager();
                    progressDialog.show();
                    Call<LoginRegisterModel> call = serviceManager.getApiInterface().Register(
                            etUsername.getText().toString(),
                            etPassword.getText().toString(),
                            etFirstname.getText().toString(),
                            etLastname.getText().toString(),
                            etBirthdate.getText().toString(),
                            encoded_string
                    );
                    call.enqueue(new Callback<LoginRegisterModel>() {
                        @Override
                        public void onResponse(Call<LoginRegisterModel> call, Response<LoginRegisterModel> response) {
                            progressDialog.dismiss();
                            if (response.isSuccessful()) {
                                if (response.body().isError()) {
                                    tvRegisterMessage.setText(response.body().getMessage());
                                    tvRegisterMessage.setVisibility(View.VISIBLE);
                                } else {
                                    AppSharedPreferences.setUserStaus(getApplicationContext(), Constants.IS_LOGGED_IN, true);
                                    AppSharedPreferences.setApiKey(getApplicationContext(), Constants.ApiKey, response.body().getApikey());
                                    AppSharedPreferences.setUserId(getApplicationContext(), Constants.USER_ID, response.body().getUserId());
                                    AppSharedPreferences.setUsername(getApplicationContext(), Constants.USERNAME, etUsername.getText().toString());
                                    Intent intent = new Intent(RegisterActivity.this, NewsActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginRegisterModel> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
                            t.printStackTrace();
                        }
                    });
                } else {
                    tvRegisterMessage.setText("Please enter valid data for all fields");
                    tvRegisterMessage.setVisibility(View.VISIBLE);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

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
                    } else if (username.contains(" ")) {
                       tvUsername.setErrorEnabled(true);
                        tvUsername.setError(USERNAME_ERROR_SPACE);
                        USERNAME_IS_CORRECT = false;
                    } else {
                        tvUsername.setError(null);
                        USERNAME_IS_CORRECT = true;
                    }
                }
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String password = editable.toString();
                if (password != null) {
                    if (!(password.length() >= 6 && password.length() <= 20)) {
                        tvPassword.setErrorEnabled(true);
                        tvPassword.setError(PASSWORD_ERROR);
                        PASSWORD_IS_CORRECT = false;
                    } else if (password.contains(" ")) {
                        tvPassword.setErrorEnabled(true);
                        tvPassword.setError(PASSWORD_ERROR_SPACE);
                        PASSWORD_IS_CORRECT = false;
                    } else {
                        tvPassword.setError(null);
                        PASSWORD_IS_CORRECT = true;
                    }
                }
            }
        });
    }

    //initialize all components from Register Activity
    public void initializeComponents() {

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etFirstname = (EditText) findViewById(R.id.etFirstname);
        etLastname = (EditText) findViewById(R.id.etLastname);
        etBirthdate = (EditText) findViewById(R.id.etBirthdate);
        imgProfilePhoto = (ImageView) findViewById(R.id.imgPhotoProfile);

        tvBirthdate = (TextView) findViewById(R.id.tvBirthdate);
        tvFirstname = (TextInputLayout) findViewById(R.id.tilFirstname);
        tvLastname = (TextInputLayout) findViewById(R.id.tilLastname);
        tvPassword = (TextInputLayout) findViewById(R.id.tilPassword);
        tvUsername = (TextInputLayout) findViewById(R.id.tilUsername);
        tvRegisterMessage = (TextView) findViewById(R.id.tvRegisterMessage);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        etBirthdate.setKeyListener(null);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(Constants.LOADING);
    }

    //check if all fileds are completed
    public boolean allRequiredFieldsAreCompleted() {
        if (etUsername.getText().toString().trim().equals("") ||
                etPassword.getText().toString().trim().equals("") ||
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
                tvRegisterMessage.setText("Invalid date");
                tvRegisterMessage.setVisibility(View.VISIBLE);
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
                Picasso.with(this).load(image).placeholder(R.drawable.placeholder_profile_circle)
                        .error(R.drawable.placeholder_profile_circle)
                        .transform(new CircleTransform())
                        .into(imgProfilePhoto);
                encoded_string = getPhotoString(photo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean allInputsAreCorrect() {
        if (USERNAME_IS_CORRECT && FIRSTNAME_IS_CORRECT && LASTNAME_IS_CORRECT && PASSWORD_IS_CORRECT)
            return true;
        return false;

    }
}
