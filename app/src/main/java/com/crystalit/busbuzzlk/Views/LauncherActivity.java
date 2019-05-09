package com.crystalit.busbuzzlk.Views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.crystalit.busbuzzlk.R;


//This class will serve as the launcher class
public class LauncherActivity extends AppCompatActivity {

    Button btn_sign_in, btn_sign_up;
    int SIGNIN = 0;
    int SIGNUP = 1;
    //Entry point of the application, This method will decide whether the user has logged before
    // or not and proceed to the relevant activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        //initialize view components
        btn_sign_in = findViewById(R.id.btn_signin);
        btn_sign_up = findViewById(R.id.btn_signup);

        //set click listeners
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignUpClicked();
            }
        });
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInClicked();
            }
        });

    }

    private void onSignInClicked(){
        Intent intent = new Intent(getApplicationContext(),SignInAndSignUpActivity.class);
        intent.putExtra("selected_activity",SIGNIN);
        startActivity(intent);
    }

    private void onSignUpClicked(){
        Intent intent = new Intent(getApplicationContext(),SignInAndSignUpActivity.class);
        intent.putExtra("selected_activity",SIGNUP);
        startActivity(intent);
    }
}
