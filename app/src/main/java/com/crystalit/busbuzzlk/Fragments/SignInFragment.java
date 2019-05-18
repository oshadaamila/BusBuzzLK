package com.crystalit.busbuzzlk.Fragments;

import android.app.ProgressDialog;
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
import com.crystalit.busbuzzlk.ViewModels.SignInViewModel;

public class SignInFragment extends Fragment {

    private SignInViewModel mViewModel;

    //declaring view components
    EditText uNameET, passwordET;
    Button continueBtn;

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.sign_in_fragment, container, false);

        // initializing view components
        uNameET = fragmentView.findViewById(R.id.email_edit_text);
        passwordET = fragmentView.findViewById(R.id.password_edit_text);
        continueBtn = fragmentView.findViewById(R.id.continue_btn);

        // set click listeners
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContinueBtnClicked();
            }
        });

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SignInViewModel.class);
        // TODO: Use the ViewModel
    }

    private void onContinueBtnClicked() {
        String userName = uNameET.getText().toString();
        String password = passwordET.getText().toString();

        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("loading");
        pd.setCanceledOnTouchOutside(false);
        mViewModel.getUser(userName, password, pd);
        pd.show();
    }

}
