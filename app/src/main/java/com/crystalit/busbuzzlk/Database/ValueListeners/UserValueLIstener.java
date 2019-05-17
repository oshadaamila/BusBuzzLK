package com.crystalit.busbuzzlk.Database.ValueListeners;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crystalit.busbuzzlk.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class UserValueLIstener implements ValueEventListener {

    ProgressDialog pd;

    public UserValueLIstener(ProgressDialog pd) {
        this.pd = pd;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        pd.dismiss();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
    }


}
