package suciu.alexandru.com.bookwormscommunity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import suciu.alexandru.com.bookwormscommunity.utils.AppSharedPreferences;
import suciu.alexandru.com.bookwormscommunity.utils.Constants;
import suciu.alexandru.com.bookwormscommunity.utils.DialogConnection;
import suciu.alexandru.com.bookwormscommunity.models.LoginRegisterModel;
import suciu.alexandru.com.bookwormscommunity.R;
import suciu.alexandru.com.bookwormscommunity.services.ServiceManager;
import suciu.alexandru.com.bookwormscommunity.utils.Util;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private TextView tvLoginMessage;
    private Button btnRetry;
    private TextView tvNoInternetMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        DialogConnection dialogConnection = new DialogConnection(this);

        if (!Util.isConnected(this)) {
            dialogConnection.showMessage(this);
        } else {
            initializeComponents();
            if (AppSharedPreferences.getUserStatus(getApplicationContext(), Constants.IS_LOGGED_IN)) {
                Intent intent = new Intent(LoginActivity.this, NewsActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void initializeComponents() {
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvLoginMessage = (TextView) findViewById(R.id.tvLoginMessage);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceManager serviceManager = new ServiceManager();
                Call<LoginRegisterModel> call = serviceManager.getApiInterface().Login(etUsername.getText().toString(), etPassword.getText().toString());

                call.enqueue(new Callback<LoginRegisterModel>() {
                    @Override
                    public void onResponse(Call<LoginRegisterModel> call, Response<LoginRegisterModel> response) {
                        if (response.body().isError()) {
                            tvLoginMessage.setText(response.body().getMessage());
                            tvLoginMessage.setVisibility(View.VISIBLE);
                        } else {
                            AppSharedPreferences.setUserStaus(getApplicationContext(), Constants.IS_LOGGED_IN, true);
                            AppSharedPreferences.setApiKey(getApplicationContext(), Constants.ApiKey, response.body().getApikey());
                            AppSharedPreferences.setUserId(getApplicationContext(), Constants.USER_ID, response.body().getUserId());
                            AppSharedPreferences.setUsername(getApplicationContext(), Constants.USERNAME, etUsername.getText().toString());
                            Intent intent = new Intent(LoginActivity.this, BooksActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginRegisterModel> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, Constants.SERVER_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
