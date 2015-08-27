package mis.tripioneer;


import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

//定位功能
public class NearSearch extends FragmentActivity {

    private LocationManager locationMgr;  //定位提供者的系統管理員
    private GoogleMap map;
    private String provider;
    private Marker markerMe;
    private Location gloc,nloc,location;
    int minTime = 1000;//ms
    int minDist = 500;//meter
    int PLACE_PICKER_REQUEST = 1;
    boolean GPS_FLAG=false;
    boolean NETWORK_FLAG=false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearsearch);
        locationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        Log.d("uuu", "ppp");
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.search_map)).getMap();
        Log.d("xxx", "ooo");
        //map.setMyLocationEnabled(true);
        if (initLocationProvider()) {
            if (NETWORK_FLAG) {
                locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, locationListener);
                Log.d("Network", "Network enabled");
                nloc = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.d("nloc", String.valueOf(nloc));
            }
            if (GPS_FLAG) {
                locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDist, locationListener);
                locationMgr.addGpsStatusListener(gpsListener);
                Log.d("GPS", "GPS enabled");
                gloc = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Log.d("gloc", String.valueOf(gloc));
            }
            if (gloc == null && nloc != null) {
                location = nloc;
            } else if (gloc == null && nloc == null) {
                Toast.makeText(this, "請開啟服務", Toast.LENGTH_LONG).show();
            } else {
                location = gloc;
            }
            Log.d("location value", String.valueOf(location));
            updateWithNewLocation(location);

            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Log.d("barney","lisa");
            try {
                Intent intent = intentBuilder.build(this);
                Log.d("lili","lisa");
                Context context = getApplicationContext();
                Log.d("jessica","lisa");
                startActivityForResult(intentBuilder.build(context), PLACE_PICKER_REQUEST);
                Log.d("sunny", "lisa");
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PLACE_PICKER_REQUEST)
        {
            Log.d("june","july");
            if (resultCode == RESULT_OK)
            {
                Log.d("judy","lisa");
                Place place = PlacePicker.getPlace(data, this);
                Log.d("vivian","lisa");
                String toastMsg = String.format("Place: %s", place.getName());
                Log.d("ted","apple");
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onPause() {
        locationMgr.removeUpdates(locationListener);
        super.onPause();
    }

    protected void onResume(){
        super.onResume();
        //locationMgr.requestLocationUpdates(provider,minTime,minDist,locationListener);
    }

    //確認位置提供是哪個提供者
    private boolean initLocationProvider() {
        Log.d("initLocationProvider", "ooo");
        //locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            GPS_FLAG=true;
        }
        if(locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            NETWORK_FLAG=true;
        }
        if (GPS_FLAG||NETWORK_FLAG)
        {
            return true;
        }else
        {
            Log.d("GPS no",provider);
            Toast.makeText(this, "請開啟網路或定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return false;
        }
    }

    private void updateWithNewLocation(Location location) {
        String where = "";
        if (location != null) {
            //經度
            double lng = location.getLongitude();
            //緯度
            double lat = location.getLatitude();

            where = "經度: " + lng +
                    "\n緯度: " + lat +
                    "\nProvider: " + provider;

            //"我"
            showMarkerMe(lat, lng);
            cameraFocusOnMe(lat, lng);
        } else {
            where = "No location found.";
        }
        Log.d("aaa", where);
    }

    private void cameraFocusOnMe(double lat, double lng)
    {
        CameraPosition camPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .zoom(16)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
    }

    private void showMarkerMe(double lat, double lng) {
        if (markerMe != null) {
            markerMe.remove();
        }
        MarkerOptions markerOpt = new MarkerOptions();
        markerOpt.position(new LatLng(lat, lng));
        markerOpt.title("我在這裡");
        markerMe = map.addMarker(markerOpt);
        Toast.makeText(this, "lat:" + lat + ",lng:" + lng, Toast.LENGTH_SHORT).show();
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            updateWithNewLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("Jenny", "Status Changed: Out of Service");
                    Toast.makeText(NearSearch.this, "Status Changed: Out of Service", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("Jenny", "Status Changed: Temporarily Unavailable");
                    Toast.makeText(NearSearch.this, "Status Changed: Temporarily Unavailable", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.AVAILABLE:
                    Log.d("Jenny", "Status Changed: Available");
                    Toast.makeText(NearSearch.this, "Status Changed: Available", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
        GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
                switch (event) {
                    case GpsStatus.GPS_EVENT_STARTED:
                        Log.d("penny", "GPS_EVENT_STARTED");
                        Toast.makeText(NearSearch.this, "GPS_EVENT_STARTED", Toast.LENGTH_SHORT).show();
                        break;
                    case GpsStatus.GPS_EVENT_STOPPED:
                        Log.d("penny", "GPS_EVENT_STOPPED");
                        Toast.makeText(NearSearch.this, "GPS_EVENT_STOPPED", Toast.LENGTH_SHORT).show();
                        break;
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        Log.d("penny", "GPS_EVENT_FIRST_FIX");
                        Toast.makeText(NearSearch.this, "GPS_EVENT_FIRST_FIX", Toast.LENGTH_SHORT).show();
                        break;
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        Log.d("penny", "GPS_EVENT_SATELLITE_STATUS");
                        break;
                }
            }
        };

    }

