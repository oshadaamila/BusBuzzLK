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
import android.widget.TextView;

import com.crystalit.busbuzzlk.Components.UserManager;
import com.crystalit.busbuzzlk.Database.Dao.BusDao;
import com.crystalit.busbuzzlk.Database.Database;
import com.crystalit.busbuzzlk.R;
import com.crystalit.busbuzzlk.models.Bus;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
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
    private ArrayList<Bus> neartBuses;

    private TextView route1, route2, route3;

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
        neartBuses = new ArrayList<Bus>();
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
        for (final String busId : busIds) {
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
                    String bearing = "0.0";
                    if (travellers.size() > 0) {
                        bearing = dataSnapshot.child("travellers").child(travellers.get(0))
                                .child("bearing").getValue().toString();
                    }

                    if (isLatLangsWithingRange(new LatLng(Double.parseDouble(lat), Double
                            .parseDouble(lng)), new
                            LatLng
                            (UserManager
                                    .getInstance().getLoggedUser().getLatitude(), UserManager.getInstance
                                    ().getLoggedUser().getLongitude()), Double.parseDouble(bearing))) {
                        Log.d("tagfordebug", "nearestBusFound " + routeId);
                        busRoutes.add(routeId);
                        addSuggestionsToTextView();
                        Bus bus = new Bus(busId, Double.parseDouble(lat), Double.parseDouble(lng),
                                routeId);
                        neartBuses.add(bus);
                    }

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
        route1 = view.findViewById(R.id.route1_text_view);
        route2 = view.findViewById(R.id.route2_text_view);
        route3 = view.findViewById(R.id.route3_text_view);

        route = view.findViewById(R.id.route_auto_text);
        update = view.findViewById(R.id.update_btn);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBusToDatabase();
            }
        });
        addSuggestionsToTextView();
        return view;
    }

    private void updateBusToDatabase() {
        String selectedRoute = route.getText().toString();
        int selectedIndex = busRoutes.indexOf(selectedRoute);
        BusDao busDao = new BusDao();
        Log.d("tagfordebug", "selectedRoute" + selectedRoute + "selectedIndex" + Integer.toString(selectedIndex));

        if (selectedIndex >= 0) {
            //selected an existing bus
            Bus currentBus = neartBuses.get(selectedIndex);
            Log.d("tagfordebug", "busId" + currentBus.getRouteID() + " " + currentBus.getId());
            busDao.registerTravellerToBus(currentBus.getId(), UserManager.getInstance()
                    .getLoggedUser().getuName(), UserManager.getInstance()
                    .getLoggedUser().getBearing());
            UserManager.getInstance().setCurrentBus(currentBus);
            UserManager.getInstance().getLoggedUser().setInBus(true);
            UserManager.getInstance().getLoggedUser().setRouteNo(currentBus.getRouteID());

        } else {
            //entered a new bus
            Log.d("tagfordebug", "selected a new bus");
            Bus bus = createNewBus(UserManager.getInstance().getLoggedUser().getLatitude(),
                    UserManager.getInstance().getLoggedUser().getLongitude(), selectedRoute);
            //this will add new bus to the database
            busDao.addNewBusToDatabase(bus);
            //register the bus in usermanager
            UserManager.getInstance().setCurrentBus(bus);
            UserManager.getInstance().getLoggedUser().setInBus(true);
            UserManager.getInstance().getLoggedUser().setRouteNo(selectedRoute);

        }
        this.dismiss();


    }

    private void addSuggestionsToTextView() {
        if (busIds != null) {
            Log.d("tagfordebug", "addSuggestionsToTextView: " + Integer.toString(busRoutes.size()));
            ArrayAdapter<String> routeAdapter = new ArrayAdapter<String>(getContext(), android.R
                    .layout.simple_list_item_1, busRoutes);
            route.setAdapter(routeAdapter);
            fillTextiews();
        }

    }

    private void fillTextiews() {
        if (busRoutes.size() >= 3) {
            route1.setText(busRoutes.get(0));
            route1.setText(busRoutes.get(1));
            route1.setText(busRoutes.get(2));
        } else if (busRoutes.size() == 2) {
            route1.setText(busRoutes.get(0));
            route1.setText(busRoutes.get(1));
        } else if (busRoutes.size() == 1) {
            route1.setText(busRoutes.get(0));
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

    //length,width of a bus is taken as 14,2.5 metres
    private boolean isLatLangsWithingRange(LatLng bus, LatLng user, Double bearing) {
        double bearin_rad = Math.toRadians(bearing);
        Double lng_range = 14 * Math.sin(bearin_rad) + 2.5 * Math.cos(bearin_rad);
        Double lat_range = 14 * Math.cos(bearin_rad) + 2.5 * Math.sin(bearin_rad);
        Double dist_between_lats = Math.abs(bus.latitude - user.latitude) * 111000;
        Double dist_between_lngs = Math.abs(bus.longitude - user.longitude) * 110000;
        return (dist_between_lngs <= lng_range) || (dist_between_lats <= lat_range);
    }

    private Bus createNewBus(Double latitude, Double longitude, String routeNo) {
        String busId = createNewBusId();
        Bus bus = new Bus(busId, latitude, longitude, routeNo);
        return bus;
    }

    //current timestamp will be created as bus_id
    private String createNewBusId() {
        Date date = new Date();
        long timeStamp = date.getTime();
        return Long.toString(timeStamp);
    }
}
