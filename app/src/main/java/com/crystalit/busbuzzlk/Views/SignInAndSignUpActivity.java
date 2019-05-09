package com.crystalit.busbuzzlk.Views;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.crystalit.busbuzzlk.Adapters.LoginPagerAdapter;
import com.crystalit.busbuzzlk.R;

/**
 * A login screen that offers login via email/password.
 */
public class SignInAndSignUpActivity extends AppCompatActivity {

    ViewPager view_pager;
    TabLayout tabLayout;
    LoginPagerAdapter pagerAdapter;
    int selected_activity ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_and_sign_up);
        //initialize view components
        view_pager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tab_layout);

        pagerAdapter = new LoginPagerAdapter(getSupportFragmentManager());
        tabLayout.setupWithViewPager(view_pager);
        view_pager.setAdapter(pagerAdapter);

        //When the needed page is selected from the launcher activity we can directly open the
        // needed tab using the saved extras in the first step
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if (extras !=null){
                selected_activity= extras.getInt("selected_activity");
            }else{
                selected_activity = 0;
            }
        }else{
            selected_activity = 0;
        }

        view_pager.setCurrentItem(selected_activity);

    }
}

