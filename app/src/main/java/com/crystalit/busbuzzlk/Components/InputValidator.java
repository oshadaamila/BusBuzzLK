package com.crystalit.busbuzzlk.Components;

import android.text.TextUtils;

public class InputValidator {

    public boolean isValidUsername(String username) {
        String regex = "^[a-zA-Z0-9._-]{3,}$";
        return username.matches(regex);
    }

    public boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidPassword(String pwd) {
//        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
//        return pwd.matches(regex);
        return (pwd.length()>=8 && pwd.length()<=16);
    }
}
