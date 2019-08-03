package com.crystalit.busbuzzlk.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.crystalit.busbuzzlk.Components.InputValidator;
import com.crystalit.busbuzzlk.R;
import com.crystalit.busbuzzlk.ViewModels.SignUpViewModel;

public class SignUpFragment extends Fragment {

    private SignUpViewModel mViewModel;
    private InputValidator inputValidator;

    // declaring view components
    EditText userNameET, emailET, passwordET, confirmPasswordET;
    Button signupBtn;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.sign_up_fragment, container, false);

        // initialize view components
        userNameET = fragmentView.findViewById(R.id.uname_txt_edit);
        emailET = fragmentView.findViewById(R.id.email_edit_text);
        passwordET = fragmentView.findViewById(R.id.password_textedit);
        confirmPasswordET = fragmentView.findViewById(R.id.confirm_password_txtedit);
        signupBtn = fragmentView.findViewById(R.id.signup_btn);

        // set click listeners
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignUpBtnClicked();
            }
        });

        inputValidator = new InputValidator();

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
        // TODO: Use the ViewModel
    }

    private void onSignUpBtnClicked() {
        boolean valid = true;
        String userName = userNameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String confirmPassword = confirmPasswordET.getText().toString();
        if (!inputValidator.isValidUsername(userName)) {
            valid = false;
            userNameET.setError("Invalid username");
        }
        if (!inputValidator.isValidEmail(email)) {
            valid = false;
            emailET.setError("Invalid email");
        }
        if (!inputValidator.isValidPassword(password)) {
            valid = false;
            passwordET.setError("Password should be 8-16 characters");
        }
        if (password!=confirmPassword) {
            valid = false;
            confirmPasswordET.setError("Password mismatch");
        }
        if (valid) {
            mViewModel.signUpUser(userName, email, password);
        }
    }


}
