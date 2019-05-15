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

import com.crystalit.busbuzzlk.R;
import com.crystalit.busbuzzlk.ViewModels.SignUpViewModel;

public class SignUpFragment extends Fragment {

    private SignUpViewModel mViewModel;

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

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
        // TODO: Use the ViewModel
    }

    private void onSignUpBtnClicked() {
        String userName = userNameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String confirmPassword = confirmPasswordET.getText().toString();
        if (validateUserInputs(userName, email, password, confirmPassword)) {
            mViewModel.signUpUser(userName, email, password);
        }
    }

    //TODO complete the code
    private boolean validateUserInputs(String userName, String email, String password, String
            confirmPasswodrd) {
        return true;
    }

}
