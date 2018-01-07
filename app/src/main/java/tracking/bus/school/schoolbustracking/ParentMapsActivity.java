package tracking.bus.school.schoolbustracking;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import tracking.bus.school.schoolbustracking.Models.LatitudeLongtitude;
import tracking.bus.school.schoolbustracking.Models.SOS;

public class ParentMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button btnSave;
    Button btnBack;


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    Button btnSearch;
    EditText etSearch;
    double home_long;
    double home_lat;
    LatLng latLng;
    MarkerOptions markerOptions;
    String addressText;

    String g;
    String latitude;
    String longtitude;

    String parentId;
    LocationManager locationManager;

    MarkerOptions marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        parentId = user.getUid();

        Firebase.setAndroidContext(this);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        etSearch = (EditText) findViewById(R.id.etSearch);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentMapsActivity.this, MainActivity.class);
                ParentMapsActivity.this.startActivity(intent);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                g = etSearch.getText().toString().toLowerCase();


                mFirebaseDatabase = FirebaseDatabase.getInstance();
                myRef = mFirebaseDatabase.getReference().child("MapLocation").child(g);
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild("latitude") && snapshot.hasChild("longtitude")) {
                            latitude = snapshot.child("latitude").getValue().toString();
                            longtitude = snapshot.child("longtitude").getValue().toString();


                            search2(Double.parseDouble(latitude),Double.parseDouble(longtitude));
                        }
                        else{
                            Geocoder geocoder = new Geocoder(getBaseContext());
                            List<Address> addresses = null;

                            try {
                                // Getting a maximum of 3 Address that matches the input
                                // text
                                addresses = geocoder.getFromLocationName(g, 3);
                                if (addresses != null && !addresses.equals(""))
                                    search(addresses);

                            } catch (Exception e) {

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double longtitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    LatLng sydney = new LatLng(latitude, longtitude);
                    marker = new MarkerOptions().position(sydney);
                    mMap.addMarker(new MarkerOptions().position(sydney));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));

                    if (ActivityCompat.checkSelfPermission(ParentMapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ParentMapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.removeUpdates(this);


                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double longtitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    LatLng sydney = new LatLng(latitude, longtitude);
                    marker = new MarkerOptions().position(sydney);
                    mMap.addMarker(new MarkerOptions().position(sydney));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));
                    if (ActivityCompat.checkSelfPermission(ParentMapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ParentMapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.removeUpdates(this);

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }

    }

    protected void search(List<Address> addresses) {
        Address address = (Address) addresses.get(0);
        home_long = address.getLongitude();
        home_lat = address.getLatitude();
        latLng = new LatLng(address.getLatitude(), address.getLongitude());

        addressText = String.format(
                "%s, %s",
                etSearch.getText().toString(), address.getCountryName());

        markerOptions = new MarkerOptions();

        markerOptions.position(latLng);
        markerOptions.title(addressText);

        mMap.clear();
        mMap.addMarker(markerOptions).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    protected void search2(Double latitude, Double longtitude) {

        home_long = longtitude;
        home_lat = latitude;
        latLng = new LatLng(latitude, longtitude);

        addressText = String.format(
                "%s",
                etSearch.getText().toString());

        markerOptions = new MarkerOptions();

        markerOptions.position(latLng);
        markerOptions.title(addressText);

        mMap.clear();
        mMap.addMarker(markerOptions).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void updateLocation() {


        if (marker != null) {

            Firebase ref = new Firebase(Config.FIREBASE_URL);
            LatitudeLongtitude latitudeLongtitude = new LatitudeLongtitude();
            latitudeLongtitude.setLatitude(String.valueOf(marker.getPosition().latitude));
            latitudeLongtitude.setLongtitude(String.valueOf(marker.getPosition().longitude));
            ref.child("ParentLocation").child(parentId).setValue(latitudeLongtitude);

            Toast.makeText(this, "location successfully updated.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "please pick location.", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(14.592743814113977, 120.979442037642);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub
                mMap.clear();
                marker = new MarkerOptions().position(point);
                mMap.addMarker(marker);
            }
        });
    }
}
