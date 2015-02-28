package mx.ken.devf.uper.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import mx.ken.devf.uper.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@edflink MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragmentUper#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragmentUper extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener, AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SupportMapFragment map;
    private GoogleMap mapa;
    private static View view;
    private LocationManager locMgr;
    private Criteria crit;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private String descriptionAddress;
    private static final String API_KEY = "AIzaSyAXHB4cV21VQR0s8CkcLiisbD-q9rXSnUA";

    private FragmentManager fragmentManager;
    private int containerFragment;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragmentUper newInstance(String param1, String param2) {
        MapFragmentUper fragment = new MapFragmentUper();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public MapFragmentUper() {
        // Required empty public constructor
    }

    public MapFragmentUper(int containePar, FragmentManager fragmentManager) {
        this.containerFragment = containePar;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            if (!mParam1.equals("")) {
                descriptionAddress = mParam1;
            } else {
                descriptionAddress = "";
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        locMgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Aviso");
            builder.setMessage("La aplicación requiere que el GPS este ensendido!!");
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
        if (container == null) {
            return null;
        }

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {

            view = inflater.inflate(R.layout.fragment_map, container, false);

            ImageView imageView = (ImageView) view.findViewById(R.id.image_search);
            AutoCompleteTextView autoCompView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
            autoCompView.dismissDropDown();

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction
                            .setCustomAnimations(R.animator.slide_in_up, R.animator.slide_out_up)
                            .replace(R.id.container, new AutocompleteFragment())
                            .addToBackStack("autocomplete")
                            .commit();
                }
            });


        } catch (InflateException e) {
            Log.wtf("*****", e.getMessage());
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        map = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (map == null) {
            map = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, map).commit();
        }
        map.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap map) {
        double longitude;
        double latitude;
        locMgr = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        //        crit.setAccuracy(Criteria.ACCURACY_FINE);
        //locMgr.requestLocationUpdates(0L, 0.0f, crit, this this, null null);
        Location location = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            longitude = 100;
            latitude = 200;
        } else {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
        LatLng myPosition = new LatLng(latitude, longitude);
        map.setBuildingsEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.addMarker(new MarkerOptions()
                .position(myPosition)
                .title("Marker"));
        LatLng ThePool = new LatLng(19.430686, -99.200764);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ThePool)      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        //map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.moveCamera(camUpdate);
        if (descriptionAddress != null && !descriptionAddress.equals("")) {
            Geocoder geocoder = new Geocoder(getActivity());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocationName(descriptionAddress, 1);
                if (addresses != null && addresses.size() > 0) {
                    double latitude1 = addresses.get(0).getLatitude();
                    double longitude1 = addresses.get(0).getLongitude();
                    LatLng pos = new LatLng(latitude1, longitude1);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, 17);
                    addMarker(pos);
                    map.moveCamera(cameraUpdate);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(ThePool,15));
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(this);

    }

    public void addMarker(LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        mapa.addMarker(markerOptions);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapa == null) {
            mapa = map.getMap();
        }
        if (locMgr != null) {
            //locMgr.requestLocationUpdates(0L, 0.0f, crit, this, null);
        }
        if (mapa != null) {
            //mapa.setLocationSource(getActivity());
        }

    }

    @Override
    public void onMyLocationChange(Location location) {
        Log.i("myLog", location.toString());
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String str = (String) parent.getItemAtPosition(position);
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();

    }


    public void savePreference(String key, String value) {
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPreference(String key, String defaultValue) {
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String name = preferences.getString(key, defaultValue);
        return name;
    }


}