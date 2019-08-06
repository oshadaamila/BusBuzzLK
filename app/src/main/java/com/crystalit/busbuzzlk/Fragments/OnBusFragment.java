package com.crystalit.busbuzzlk.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.crystalit.busbuzzlk.R;
import com.crystalit.busbuzzlk.Views.HomeNavigationActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnBusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OnBusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnBusFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button yesButton, noButton;
    private EditText routeNo;
    private static OnBusFragment instance;

    @SuppressLint("ValidFragment")
    private OnBusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnBusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OnBusFragment newInstance(String param1, String param2) {
        if (instance == null) {
            instance = new OnBusFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            instance.setArguments(args);
        }

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_on_bus, container, false);
        yesButton = view.findViewById(R.id.yesButton);
        noButton = view.findViewById(R.id.no_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onYesButtonClicked();
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNoButtonClicked();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void addUserToBus(FragmentManager fm) {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Please wait...");
        pd.show();

        com.crystalit.busbuzzlk.Components.UserManager.getInstance().addUserToaBus(fm, pd);
    }

    //user says he is in a bus
    private void onYesButtonClicked() {

        if (com.crystalit.busbuzzlk.Components.UserManager.getInstance().getLoggedUser().isInBus()) {
            //do nothing
            HomeNavigationActivity activity = (HomeNavigationActivity) getActivity();
            activity.showHomeFragment();
        } else {
            addUserToBus(getFragmentManager());
            HomeNavigationActivity activity = (HomeNavigationActivity) getActivity();
            activity.showHomeFragment();
            activity.startLocationUpdates();
        }

    }

    //user says he is not in a bus
    private void onNoButtonClicked() {

        if (com.crystalit.busbuzzlk.Components.UserManager.getInstance().getLoggedUser().isInBus()) {
            //remove the user from current bus
            com.crystalit.busbuzzlk.Components.UserManager.getInstance().removeUserFromBus();
            HomeNavigationActivity activity = (HomeNavigationActivity) getActivity();
            activity.showHomeFragment();
            activity.slowerLocationUpdates();
        } else {
            //user is not in a bus
            //close the fragment
            HomeNavigationActivity activity = (HomeNavigationActivity) getActivity();
            activity.showHomeFragment();
        }
    }
}
