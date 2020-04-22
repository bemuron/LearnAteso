/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.learnateso.learn_ateso.ui.activities;

import android.app.ProgressDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.learnateso.learn_ateso.data.network.api.APIService;
import com.learnateso.learn_ateso.data.network.api.APIUrl;
import com.learnateso.learn_ateso.models.Result;
import com.learnateso.learn_ateso.ui.viewmodels.MainActivityViewModel;
import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.helpers.SQLiteHandler;
import com.learnateso.learn_ateso.helpers.SessionManager;
import com.learnateso.learn_ateso.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private MainActivityViewModel mainActivityViewModel;
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mainActivityViewModel = ViewModelProviders.of
                (this).get(MainActivityViewModel.class);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit com.emtech.retrofitexample.api service
        APIService service = retrofit.create(APIService.class);

        //defining the call
        Call<Result> call = service.userLogin(email, password);

        //calling the com.emtech.retrofitexample.api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                //Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                //if response body is not null, we have some data
                //count what we have in the response
                if (!response.body().getError()) {
                    User userDetail = new User();
                    userDetail.setId("1");
                    userDetail.setUserId(response.body().getUser().getUserId());
                    userDetail.setUserName(response.body().getUser().getUserName());
                    userDetail.setUserEmail(response.body().getUser().getUserEmail());
                    userDetail.setCreatedAt(response.body().getUser().getCreatedAt());

                    //create user login session
                    session.createLoginSession(userDetail.getUserName(), userDetail.getUserEmail());

                    // Inserting row in users table
                    mainActivityViewModel.insert(userDetail);

                    // Launch main activity
                    Intent intent = new Intent(LoginActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    finish();

                    // If the code reaches this point, we have successfully posted the job
                    Log.d(TAG, "Successful login");
                }else {
                    hideDialog();
                    // Error in login. Get the error message
                    Log.e(TAG, "Error msg: "+response.body().getMessage());
                    Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                hideDialog();
                //print out any error we may get
                //probably server connection
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
