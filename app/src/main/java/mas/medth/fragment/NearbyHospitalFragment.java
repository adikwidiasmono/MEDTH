package mas.medth.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import mas.medth.main.R;
import mas.medth.service.api.XSightImpl;
import mas.medth.service.model.response.ResAccessToken;
import mas.medth.service.model.response.ResGISFeature;
import mas.medth.service.model.response.ResGISResponse;
import mas.medth.utils.ProgressBarCircularIndeterminate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by adikwidiasmono on 15/11/17.
 */

public class NearbyHospitalFragment extends Fragment
        implements OnMapReadyCallback {

    private static final String EXTRA_TEXT = "text";

    private ProgressBarCircularIndeterminate pbHospital;
    private MapView mapView;
    private GoogleMap mMap;

    private Double currLatitude, currLongitude;

    public static NearbyHospitalFragment createFor(String text) {
        NearbyHospitalFragment fragment = new NearbyHospitalFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nearby_hospital, container, false);

        pbHospital = v.findViewById(R.id.pb_hospital);
        pbHospital.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupMap(savedInstanceState, view);
    }

    private void setupMap(Bundle savedInstanceState, View view) {
        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);


        mapView.getMapAsync(this);

        // Get reference to my location icon
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).
                getParent()).findViewById(Integer.parseInt("2"));

        // and next place it, for example, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 32, 0, 0);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);

        // Move camera to user current location
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            currLatitude = location.getLatitude();
            currLongitude = location.getLongitude();

            getListNearByHospital();

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));

//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
//                    .zoom(15)                   // Sets the zoom
////                    .bearing(90)                // Sets the orientation of the camera to east
////                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
//                    .build();                   // Creates a CameraPosition from the builder
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void getListNearByHospital() {
        pbHospital.setVisibility(View.VISIBLE);
        Call tokenCall = XSightImpl
                .getInstance()
                .getAccessToken();
        tokenCall.enqueue(new Callback<ResAccessToken>() {
            @Override
            public void onResponse(Call<ResAccessToken> call, Response<ResAccessToken> response) {
                if (response.isSuccessful()) {
                    ResAccessToken accessToken = response.body();

                    Call nearbyHosCall = XSightImpl
                            .getInstance()
                            .getNearbyHospitalLocation(accessToken.getAccessToken(), "106.8285978, -6.2380341");
                    nearbyHosCall.enqueue(new Callback<ResGISResponse>() {
                        @Override
                        public void onResponse(Call<ResGISResponse> call, Response<ResGISResponse> response) {
                            pbHospital.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                ResGISResponse res = response.body();
                                List<ResGISFeature> listHospital = res.getFeatures();

                                int i = 0;
                                for (ResGISFeature hos : listHospital) {
                                    i++;
                                    // Y : Latitude, X : Longitude
                                    Log.e("Nearby Hosp", "-> " + hos.getGeometry().getY() + ", " + hos.getGeometry().getX());

                                    // Add marker on map
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .title("Hospital " + i)
                                            .snippet("Hospital " + i)
                                            .position(new LatLng(hos.getGeometry().getY(), hos.getGeometry().getX()))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital_placeholder_24dp)));
                                    marker.setTag("Hospital " + i);
                                }
                            } else {
                                Log.e("Nearby Hosp", "onResponse " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResGISResponse> call, Throwable t) {
                            pbHospital.setVisibility(View.GONE);
                            t.printStackTrace();
                            Log.e("Nearby Hosp", "onFailure " + t.getLocalizedMessage());
                        }
                    });
                } else {
                    pbHospital.setVisibility(View.GONE);
                    Log.e("Get Access Token", "onResponse " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResAccessToken> call, Throwable t) {
                pbHospital.setVisibility(View.GONE);
                t.printStackTrace();
                Log.e("Get Access Token", "onFailure " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}