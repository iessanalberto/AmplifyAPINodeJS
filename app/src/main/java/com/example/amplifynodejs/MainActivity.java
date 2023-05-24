package com.example.amplifynodejs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.rest.RestOperation;
import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> ls = new ArrayList<String>();

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

        getTodo();

    }


    public void getTodo() {
        RestOptions options = RestOptions.builder()
                .addPath("/documents")
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