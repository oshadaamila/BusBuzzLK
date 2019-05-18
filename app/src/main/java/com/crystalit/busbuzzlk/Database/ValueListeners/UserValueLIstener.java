package com.crystalit.busbuzzlk.Database.ValueListeners;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crystalit.busbuzzlk.ViewModels.SignInViewModel;
import com.crystalit.busbuzzlk.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class UserValueLIstener implements ValueEventListener {

    ProgressDialog pd;
    SignInViewModel signInViewModel;

    public UserValueLIstener(ProgressDialog pd, SignInViewModel signInViewModel) {
        this.pd = pd;
        this.signInViewModel = signInViewModel;

    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        pd.dismiss();
        signInViewModel.signInUser(user);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
    }


}
