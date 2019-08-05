package com.crystalit.busbuzzlk.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.crystalit.busbuzzlk.Database.Database;
import com.crystalit.busbuzzlk.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectBusDialog.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectBusDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectBusDialog extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<String> nearestBuses;
    private String[] busIds;
    private ArrayList<String> busRoutes;

    private AutoCompleteTextView route;
    private Button update;

    private OnFragmentInteractionListener mListener;

    public SelectBusDialog() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SelectBusDialog(List<String> busKeyList) {
        this.nearestBuses = busKeyList;
        busRoutes = new ArrayList<String>();
        if (nearestBuses != null) {
            busIds = new String[nearestBuses.size()];
            fillValuesToBusRoutes();
            getBusRoutes();
        }

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectBusDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectBusDialog newInstance(String param1, String param2) {
        SelectBusDialog fragment = new SelectBusDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void getBusRoutes() {
        Log.d("tagfordebug", "getBusRoutes busids: " + Integer.toString(busIds.length));
        Log.d("tagfordebug", "getBusRoutes busroutes: " + Integer.toString(busRoutes.size()));

        Database database = Database.getInstance();
        for (String busId : busIds) {
            DatabaseReference ref = database.getBusReference().child(busId);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String routeId = dataSnapshot.child("routeID").getValue().toString();
                    String lat = dataSnapshot.child("latitude").getValue().toString();
                    String lng = dataSnapshot.child("longitude").getValue().toString();
                    List<String> travellers = new ArrayList<String>();
                    Iterable<DataSnapshot> ds = dataSnapshot.child("travellers").getChildren();
                    for (DataSnapshot child : ds) {
                        travellers.add(child.getKey());
                    }
                    String bearing = dataSnapshot.child("travellers").child(travellers.get(0))
                            .child("bearing").getValue().toString();
                    busRoutes.add(routeId);
                    addSuggestionsToTextView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    private void fillValuesToBusRoutes() {
        for (int i = 0; i < nearestBuses.size(); i++) {
            busIds[i] = nearestBuses.get(i);
        }
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
        View view = inflater.inflate(R.layout.fragment_select_bus_dialog, container, false);
        route = view.findViewById(R.id.route_auto_text);
        update = view.findViewById(R.id.update_btn);
        addSuggestionsToTextView();
        return view;
    }

    private void addSuggestionsToTextView() {
        if (busIds != null) {
            Log.d("tagfordebug", "addSuggestionsToTextView: " + Integer.toString(busIds.length));
            ArrayAdapter<String> routeAdapter = new ArrayAdapter<String>(getContext(), android.R
                    .layout.simple_list_item_1, busRoutes);
            route.setAdapter(routeAdapter);
        }

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
}
