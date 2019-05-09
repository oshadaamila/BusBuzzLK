package com.crystalit.busbuzzlk.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.crystalit.busbuzzlk.Fragments.SignInFragment;
import com.crystalit.busbuzzlk.Fragments.SignUpFragment;

public class LoginPagerAdapter extends FragmentStatePagerAdapter {

    public LoginPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int i) {
        if(i == 0){
            Fragment loginFragment = new SignInFragment();
            return  loginFragment;
        }else{
            Fragment signupFragment = new SignUpFragment();
            return  signupFragment;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position==0){
            return "SIGN IN";
        }else {
            return "SIGN UP";
        }
    }
}
