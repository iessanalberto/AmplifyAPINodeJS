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
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
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

        Amplify.Auth.fetchAuthSession(
                result -> {
                    AWSCognitoAuthSession cognitoAuthSession = (AWSCognitoAuthSession) result;
                    switch(cognitoAuthSession.getIdentityIdResult().getType()) {
                        case SUCCESS:
                            Log.i("AuthQuickStart", "IdentityId: " + cognitoAuthSession.getIdentityIdResult().getValue());
                            Log.i("AuthQuickStart", "AWSCredentials: " + cognitoAuthSession.getAwsCredentialsResult().getValue());
                            Log.i("AuthQuickStart", "TokenResult: " + cognitoAuthSession.getUserPoolTokensResult().getValue());
                            break;
                        case FAILURE:
                            Log.i("AuthQuickStart", "IdentityId not present because: " + cognitoAuthSession.getIdentityIdResult().getError().toString());
                    }
                },
                error -> Log.e("AuthQuickStart", error.toString())
        );

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignin = findViewById(R.id.btnSignin);
        btnSignout = findViewById(R.id.btnSignout);
        btnSignup = findViewById(R.id.btnSignup);

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

                try {
                    Amplify.Auth.getCurrentUser(
                            result -> Log.i("AuthQuickstart", "Current user details are:" + result.toString()),
                            error -> Log.e("AuthQuickstart", "getCurrentUser failed with an exception: " + error)
                    );
                } catch (Exception error) {
                    Log.e("AuthQuickstart", "unexpected error: " + error);
                }

                AuthUserAttribute userEmail =
                        new AuthUserAttribute(AuthUserAttributeKey.email(), "email@email.com");
                Amplify.Auth.updateUserAttribute(userEmail,
                        result -> Log.i("AuthDemo", "Updated user attribute = " + result.toString()),
                        error -> Log.e("AuthDemo", "Failed to update user attribute.", error)
                );

                Amplify.Auth.fetchAuthSession(
                        result -> {
                            AWSCognitoAuthSession cognitoAuthSession = (AWSCognitoAuthSession) result;
                            switch(cognitoAuthSession.getIdentityIdResult().getType()) {
                                case SUCCESS:
                                    Log.i("AuthQuickStart", "IdentityId: " + cognitoAuthSession.getIdentityIdResult().getValue());
                                    Log.i("AuthQuickStart", "AWSCredentials: " + cognitoAuthSession.getAwsCredentialsResult().getValue());
                                    Log.i("AuthQuickStart", "TokenResult: " + cognitoAuthSession.getUserPoolTokensResult().getValue());
                                    break;
                                case FAILURE:
                                    Log.i("AuthQuickStart", "IdentityId not present because: " + cognitoAuthSession.getIdentityIdResult().getError().toString());
                            }
                        },
                        error -> Log.e("AuthQuickStart", error.toString())
                );

                getTodo();



            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Amplify.Auth.fetchAuthSession(
                        result -> {
                            AWSCognitoAuthSession cognitoAuthSession = (AWSCognitoAuthSession) result;
                            switch(cognitoAuthSession.getIdentityIdResult().getType()) {
                                case SUCCESS:
                                    Log.i("AuthQuickStart", "IdentityId: " + cognitoAuthSession.getIdentityIdResult().getValue());
                                    Log.i("AuthQuickStart", "AWSCredentials: " + cognitoAuthSession.getAwsCredentialsResult().getValue());
                                    Log.i("AuthQuickStart", "TokenResult: " + cognitoAuthSession.getUserPoolTokensResult().getValue());
                                    break;
                                case FAILURE:
                                    Log.i("AuthQuickStart", "IdentityId not present because: " + cognitoAuthSession.getIdentityIdResult().getError().toString());
                            }
                        },
                        error -> Log.e("AuthQuickStart", error.toString())
                );




                getTodo();

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
                .addHeader("TokenNodeJS", "eyJraWQiOiJ2VVlOK2lvd0N4WjBUUENNODFqRTNVNzdcL3pObFI1N1g5YjQrZzVZQVdBbz0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxMmY3NzI5NS0zMGY2LTQ0YzktOTFkZC01ZWI5NTJjNGI5NGQiLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV81Q2RMbGFRb2UiLCJjb2duaXRvOnVzZXJuYW1lIjoic2FudHkiLCJvcmlnaW5fanRpIjoiMzBjZTZmMzktYzI0Ny00ZDk1LWFhNzYtYWJjZTdiMjljMmNkIiwiYXVkIjoiNm9ya2FicG01dnZxNWllM3JsNWIycmo1cjEiLCJldmVudF9pZCI6IjU2NWE4MjdiLTQ2MGItNGMxYS1iZTQ0LTE2ZTU5Y2YyM2U3MSIsInRva2VuX3VzZSI6ImlkIiwiYXV0aF90aW1lIjoxNjg1ODkxNDM1LCJleHAiOjE2ODU4OTUwMzUsImlhdCI6MTY4NTg5MTQzNSwianRpIjoiY2JjMTNlNWYtZGZhYy00MjdmLTgxNjctYTFjYjYzZDMyYjU0IiwiZW1haWwiOiJzYW50eUBjaWVyY28uZXMifQ.07okzlEbhELkRyh__ChK9J_2jmrlx3jEfBB0et7LAQEdFtBooHfjfMNsGxHNSBWSulsmtsf1ge1FVnEeg9Q_YnlIdykGAwjv11w1smhSspVu-AHuPu4pW67YDtYOFPmfqRiRRV9YAA-ssifX1V6H4hJHbHW_UISx5hI5S793yIBIh-Mv9-21jvcPa3NV6W2m2GTfsYr65nd5IU6keCFL_fI3mgyDifmbmjvEJ6yOTn-urmsQVHIQXg50GCemlL46RPDuOW-un0r6KfqK62_kzfp-9H_hfIXFBP95rcOWDKABonnv-xGOelcOs8wm__frfK7eG2OMGD8IyA6B8f04Sw")
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


        Amplify.Auth.fetchAuthSession(
                result -> {
                    AWSCognitoAuthSession cognitoAuthSession = (AWSCognitoAuthSession) result;
                    switch(cognitoAuthSession.getIdentityIdResult().getType()) {
                        case SUCCESS:
                            Log.i("AuthQuickStart", "IdentityId: " + cognitoAuthSession.getIdentityIdResult().getValue());
                            Log.i("AuthQuickStart", "AWSCredentials: " + cognitoAuthSession.getAwsCredentialsResult().getValue());
                            Log.i("AuthQuickStart", "TokenResult: " + cognitoAuthSession.getUserPoolTokensResult().getValue());
                            break;
                        case FAILURE:
                            Log.i("AuthQuickStart", "IdentityId not present because: " + cognitoAuthSession.getIdentityIdResult().getError().toString());
                    }
                },
                error -> Log.e("AuthQuickStart", error.toString())
        );

    }

}