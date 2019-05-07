package com.crystalit.busbuzzlk.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crystalit.busbuzzlk.R;


//This class will serve as the launcher class
public class LauncherActivity extends AppCompatActivity {

    //Entry point of the application, This method will decide whether the user has logged before
    // or not and proceed to the relevant activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
    }
}
