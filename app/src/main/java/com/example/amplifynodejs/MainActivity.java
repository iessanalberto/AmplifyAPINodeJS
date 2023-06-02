package com.example.amplifynodejs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.rest.RestOperation;
import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.core.Amplify;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> ls = new ArrayList<String>();
    EditText etUsername;
    EditText etPassword;
    Button btnSignup;
    Button btnSignin;
    Button btnSignout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // Add these lines to add the `AWSApiPlugin` and `AWSCognitoAuthPlugin`
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(getApplicationContext());

            Log.i("MyAmplifyApp", "Initialized Amplify.");
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify.", error);
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignin = findViewById(R.id.btnSignin);
        btnSignout = findViewById(R.id.btnSignout);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                //amplifyCognito.signIn(username, password);
                Amplify.Auth.signIn(username, password,
                        result ->
                        {
                            Log.i("AuthQuickstart", result.isSignedIn() ? "Sign in succeeded" : "Sign in not complete");


                        },
                        error -> Log.e("AuthQuickstart", error.toString()));

                //


            }
        });






        AuthSignOutOptions options = AuthSignOutOptions.builder()
                .globalSignOut(true)
                .build();

        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //amplifyCognito.signOut();
                Amplify.Auth.signOut(options, signOutResult -> {
                    if (signOutResult instanceof AWSCognitoAuthSignOutResult.CompleteSignOut) {
                        Log.i("AuthQuickstart", "ok.");

                    } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.PartialSignOut) {
                        Log.i("AuthQuickstart", "ok.");
                    } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.FailedSignOut) {
                        Log.i("AuthQuickstart", "error.");
                    }
                });

                getTodo();

            }
        });



        getTodo();

    }


    public void getTodo() {
        RestOptions options = RestOptions.builder()
                //.addPath("/documents")
                .addPath("/users") //añado la ruta de mi api y creo la función lambda y le añado el rol de la función lambda de documents
                .build();

        RestOperation operation = Amplify.API.get(options,
                restResponse ->
                {
                    String rawData = restResponse.getData().asString();
                    ls.add(rawData);
                    Log.i("RESTapiOperation", "GET succeeded1: " + restResponse.getData().asString());
                },
                apiFailure ->
                        Log.e("RESTapiOperation", "GET failed: ", apiFailure)
        );

        Log.i("Data Collection", "Size of list is " + ls.size());


        Amplify.API.get(options,
                restResponse -> Log.i("MyAmplifyApp", "GET succeeded: " + restResponse.getData().asString()),
                apiFailure -> Log.e("MyAmplifyApp", "GET failed.", apiFailure)
        );
    }

}