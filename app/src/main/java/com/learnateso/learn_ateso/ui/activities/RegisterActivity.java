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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private MainActivityViewModel mainActivityViewModel;
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mainActivityViewModel = ViewModelProviders.of
                (this).get(MainActivityViewModel.class);

        inputFullName = findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    registerUser(name, email, password);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }//close onCreate()

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String name, final String email,
                              final String password) {
        pDialog.setMessage("Registering ...");
        showDialog();

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit com.emtech.retrofitexample.api service
        APIService service = retrofit.create(APIService.class);

        //defining the call
        Call<Result> call = service.createUser(name, email, password);

        //calling the com.emtech.retrofitexample.api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                hideDialog();

                //displaying the message from the response as toast
                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, response.body().getMessage());

                //if response body is not null, we have some data
                //count what we have in the response
                if (!response.body().getError()) {
                    Log.d(TAG, response.body().getMessage());

                    /*User userDetail = new User();
                    userDetail.setId("");
                    userDetail.setUserId(response.body().getUser().getUserId());
                    userDetail.setUserName(response.body().getUser().getUserName());
                    userDetail.setUserEmail(response.body().getUser().getUserEmail());
                    userDetail.setCreatedAt(response.body().getUser().getCreatedAt());

                    // Inserting row in users table
                    mainActivityViewModel.insert(userDetail);*/

                    Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                    // Launch login activity
                    Intent intent = new Intent(
                            RegisterActivity.this,
                            LoginActivity.class);
                    startActivity(intent);
                    finish();

                    // If the code reaches this point, we have successfully registered
                    Log.d(TAG, "Successful registration");

                    //registerResponse = true;
                }
                else {

                    // Error occurred in registration. Get the error
                    // message
                    Toast.makeText(getApplicationContext(),
                            response.body().getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, response.body().getMessage());
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
