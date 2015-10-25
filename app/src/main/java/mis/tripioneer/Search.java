package mis.tripioneer;

import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jenny on 2015/8/14. 放大鏡功能
 */
public class Search extends FragmentActivity  {
    private GoogleMap map;
    private LocationManager locationMgr;
    private  LatLng position;
    private  double lat,lng;
    final String CASE = "MAP";
    private final int par_num = 2;
    private static int x;
    private LatLngBounds center,bunds;
    String request_place_id_name[] = new String[par_num];
    String request_place_id_value[] = new String[par_num];
    private static ArrayList<String> ret_place_X = new ArrayList<String>();
    private static ArrayList<String> ret_place_Y = new ArrayList<String>();
    private static ArrayList<String> ret_place_Name = new ArrayList<String>();
    private static ArrayList<String> ret_place_id = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG", "oncreate");
        setContentView(R.layout.activity_search);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.s_map)).getMap();
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);

        locationMgr=(LocationManager) getSystemService(LOCATION_SERVICE);
        initProvider();
        Log.d("TAG", "uuu");

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            Marker currentShown;
            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(currentShown))
                {
                    marker.hideInfoWindow();
                    currentShown = null;
                } else
                {
                    marker.showInfoWindow();
                    currentShown = marker;
                }
                for(int u=0;u<ret_place_Name.size();u++)
                {
                    if(marker.getTitle().equals(ret_place_Name.get(u))) // if marker source is clicked
                    {
                        Toast.makeText(Search.this, marker.getTitle(), Toast.LENGTH_SHORT).show();// display toast
                        changeActivity(ret_place_id.get(u));
                    }

                }
                return true;
            }
        });
    }

    private void changeActivity(String s)
    {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        intent.setClass(Search.this, place.class);
        //bundle.putString("specifyid", item.getID());
        intent.putExtras(bundle);
        startActivity(intent);

    }

    public void initProvider()
    {
        int minTime = 1000;//ms
        int minDist = 500;//meter
        Log.d("TAG", "iniProvider");
        if(!locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Log.d("TAG","!locmgr GPS");
            Toast.makeText(Search.this, "請開啟GPS定位功能並重啟此APP", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        if(locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Log.d("TAG","locmgr GPS");
            locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,minTime,minDist,locationListener);
            locationMgr.addGpsStatusListener(gpsListener);
            locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, locationListener);
            Toast.makeText(Search.this, "provider:gps", Toast.LENGTH_SHORT).show();
        }
        else if (locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            Log.d("TAG","locmgr network");
            Toast.makeText(Search.this, "provider:network", Toast.LENGTH_SHORT).show();
            locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, locationListener);
        }else
        {
            Toast.makeText(Search.this, "請開啟定位功能:", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    protected void onPause() {
        Log.d("TAG","onpause");
        //locationMgr.removeUpdates(locationListener);
        //locationMgr.removeGpsStatusListener(gpsListener);
        super.onPause();
    }

    protected void onResume()
    {
        super.onResume();
        //initProvider();
        Log.d("TAG", "onResume");
    }

    private void updateWithNewLocation(Location location)
    {
        Log.d("TAG", "updateNewLocation");
        String where = "";
        if (location != null)
        {
            //經度
            lng = location.getLongitude();
            //緯度
            lat = location.getLatitude();

            where = "經度: " + lng + "\n緯度: " + lat ;
        }
        else {
            where = "No location found.";
        }
        Log.d("TAG", where);
        MarkerOptions orgin_option = new MarkerOptions();
        orgin_option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        position= new LatLng(lat,lng);
        orgin_option.position(position);
        orgin_option.title("目前位置");
        map.addMarker(orgin_option);
        Connect_Server connect_Server = new Connect_Server();
        connect_Server.execute(location);
    }

    /*@Override
    public boolean onMarkerClick(Marker marker)
    {
        Marker lastOpened = null;
        if (lastOpened != null) {
            // Close the info window
            lastOpened.hideInfoWindow();

            // Is the marker the same marker that was already open
            if (lastOpened.equals(marker)) {
                // Nullify the lastOpened object
                lastOpened = null;
                // Return so that the info window isn't opened again
                return true;
            }
            marker.showInfoWindow();
            lastOpened = marker;
        }
        return true;
    }*/

    private class Connect_Server extends AsyncTask<Location,Void,String>
    {
        final String CONNECT_SERVER = "http://140.115.80.224:8080/group4/near_search.php";
        private final String URL_PREFIX_IMAGE = "http://140.115.80.224:8080/group4/tainan_pic/";
        String ret;
        @Override
        protected String doInBackground(Location... params) {
            Log.d("TAG","doinbackground");
            Log.d("lat",String.valueOf(params[0].getLatitude()));
            Log.d("lat",String.valueOf(params[0].getLongitude()));
            request_place_id_name[0]="lat";
            request_place_id_name[1]="lng";
            request_place_id_value[0]=String.valueOf(params[0].getLatitude());
            request_place_id_value[1]=String.valueOf(params[0].getLongitude());
            ConnectServer connection = new ConnectServer(CONNECT_SERVER);
            ret = connection.connect(request_place_id_name, request_place_id_value, par_num);
            Log.d("TAG",ret);
            return ret;
        }

        protected void onPostExecute(String ret)
        {
            Log.d("TAG","onpost");
            JsonParser parser = new JsonParser(CASE);
            ret_place_Name = parser.Parse(ret,"place_Name");
            ret_place_X = parser.Parse(ret, "place_X");
            ret_place_Y = parser.Parse(ret, "place_Y");
            ret_place_id = parser.Parse(ret, "place_pic");
            x = ret_place_Name.size();


            for(int a=0;a<x;a++)
            {
                Log.d("x is ",String.valueOf(x));
                Log.d("Gina",ret_place_Name.get(a)+"\n");
                Log.d("TAG",ret_place_X.get(a)+"\n");
                Log.d("TAG",ret_place_Y.get(a)+"\n");
                Log.d("TAG",ret_place_id.get(a)+"\n");
            }
            Log.d("TAG", "x is "+String.valueOf(x));

            MarkerOptions orgin_options = new MarkerOptions();
            orgin_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            for(int b=0;b<x;b++)
            {
                position= new LatLng(Double.parseDouble(ret_place_Y.get(b)),Double.parseDouble(ret_place_X.get(b)));
                orgin_options.position(position);
                orgin_options.title(ret_place_Name.get(b));
                map.addMarker(orgin_options);

            }
            LatLng t=new LatLng(lat,lng);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom((t), 14));
            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(bunds.getCenter(),14));
        }
    }

    LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            updateWithNewLocation(location);
            Log.d("TAG", "onLocationChanged");
            Toast.makeText(Search.this, "onLocationChanged", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("TAG", "onProviderDisabled");
            Toast.makeText(Search.this, "定位提供者關閉，請重啟", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("TAG","onProviderEnabled");
            Toast.makeText(Search.this, "onProviderEnabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("Jenny", "Status Changed: Out of Service");
                    Toast.makeText(Search.this, "Status Changed: Out of Service", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("Jenny", "Status Changed: Temporarily Unavailable");
                    Toast.makeText(Search.this, "Status Changed: Temporarily Unavailable", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.AVAILABLE:
                    Log.d("Jenny", "Status Changed: Available");
                    Toast.makeText(Search.this, "Status Changed: Available", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Search.this, "GPS_EVENT_STARTED", Toast.LENGTH_SHORT).show();
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.d("penny", "GPS_EVENT_STOPPED");
                    Toast.makeText(Search.this, "GPS_EVENT_STOPPED", Toast.LENGTH_SHORT).show();
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.d("penny", "GPS_EVENT_FIRST_FIX");
                    Toast.makeText(Search.this, "GPS_EVENT_FIRST_FIX", Toast.LENGTH_SHORT).show();
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.d("penny", "GPS_EVENT_SATELLITE_STATUS");
                    break;
            }
        }
    };

}
