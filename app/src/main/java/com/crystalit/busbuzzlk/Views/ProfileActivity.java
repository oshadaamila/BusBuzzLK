package com.crystalit.busbuzzlk.Views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.crystalit.busbuzzlk.Components.UserManager;
import com.crystalit.busbuzzlk.R;

public class ProfileActivity extends AppCompatActivity {

    TextView name, email, status, route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        name = findViewById(R.id.uName_tv);
        email = findViewById(R.id.email_tv);
        status = findViewById(R.id.status_tv);
        route = findViewById(R.id.route_no_tv);
        name.setText(UserManager.getInstance().getLoggedUser().getuName());
        email.setText(UserManager.getInstance().getLoggedUser().getEmail());
        if (UserManager.getInstance().getCurrentBus() != null) {
            status.setText("In a bus");
            route.setText(UserManager.getInstance().getLoggedUser().getRouteNo());
        } else {
            status.setText("Not In a Bus");
        }


    }

}
