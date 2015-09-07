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
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jenny on 2015/8/14. 放大鏡功能
 */
public class Search extends FragmentActivity {
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
    private static ArrayList<String> ret_place_ShortIntro = new ArrayList<String>();
    private static ArrayList<String> ret_place_pic = new ArrayList<String>();
    private static ListView listView;
    private static List<RoadPlanModel> viewModels;
    private static RoadPlanAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG", "oncreate");
        setContentView(R.layout.activity_nearsearch);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.search_map)).getMap();
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        viewModels = new ArrayList<RoadPlanModel>();
        adapter = new RoadPlanAdapter(this, viewModels);
        listView = (ListView) findViewById(R.id.listView_map);
        locationMgr=(LocationManager) getSystemService(LOCATION_SERVICE);
        initProvider();
        Log.d("TAG", "uuu");
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

    private LatLngBounds getBound(ArrayList<String> ret_place_X, ArrayList<String> ret_place_Y, Double lng,Double lat)
    {
        Log.d("TAG","latlngbounds");
        ret_place_X.add(String.valueOf(lng));
        ret_place_Y.add(String.valueOf(lat));
        double max_X=Double.parseDouble(ret_place_X.get(0));
        double min_X=Double.parseDouble(ret_place_X.get(0));
        double max_Y=Double.parseDouble(ret_place_Y.get(0));
        double min_Y=Double.parseDouble(ret_place_Y.get(0));
        for (int t=1; t<ret_place_X.size();t++)
        {
            if (Double.parseDouble(ret_place_X.get(t))>max_X)
            {
                max_X=Double.parseDouble(ret_place_X.get(t));
            }
            if (Double.parseDouble(ret_place_X.get(t))<min_X)
            {
                min_X=Double.parseDouble(ret_place_X.get(t));
            }
            if (Double.parseDouble(ret_place_Y.get(t))>max_Y)
            {
                max_Y=Double.parseDouble(ret_place_Y.get(t));
            }
            if (Double.parseDouble(ret_place_Y.get(t))<min_Y)
            {
                min_Y=Double.parseDouble(ret_place_Y.get(t));
            }
        }
        Log.d("TAG","minX"+String.valueOf(min_X));
        Log.d("TAG","maxX"+String.valueOf(max_X));
        Log.d("TAG","minY"+String.valueOf(min_Y));
        Log.d("TAG","maxY"+String.valueOf(min_Y));
        center = new LatLngBounds(new LatLng(min_Y, min_X), new LatLng(max_Y, max_X));
        return center;
        }

    protected void onPause() {
        Log.d("TAG","onpause");
        locationMgr.removeUpdates(locationListener);
        locationMgr.removeGpsStatusListener(gpsListener);
        super.onPause();
    }

    protected void onResume()
    {
        super.onResume();
        initProvider();
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
            ret_place_pic = parser.Parse(ret, "place_pic");
            ret_place_ShortIntro = parser.Parse(ret, "place_ShortIntro");
            x = ret_place_Name.size();


            for(int a=0;a<x;a++)
            {
                Log.d("x is ",String.valueOf(x));
                Log.d("Gina",ret_place_Name.get(a)+"\n");
                Log.d("TAG",ret_place_X.get(a)+"\n");
                Log.d("TAG",ret_place_Y.get(a)+"\n");
                Log.d("TAG",ret_place_ShortIntro.get(a)+"\n");
                Log.d("TAG",ret_place_pic.get(a)+"\n");
            }
            Log.d("TAG", "x is "+String.valueOf(x));
            //bunds=getBound(ret_place_X, ret_place_Y,lng,lat);
            MarkerOptions orgin_options = new MarkerOptions();
            orgin_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            for(int b=0;b<x;b++)
            {
                position= new LatLng(Double.parseDouble(ret_place_Y.get(b)),Double.parseDouble(ret_place_X.get(b)));
                orgin_options.position(position);
                orgin_options.title(ret_place_Name.get(b));
                map.addMarker(orgin_options);
                try
                {
                    if(!"".equals( ret_place_pic.get(b)))
                    {
                        Log.d("Hello","Hello");
                        RoadPlanModel row = new RoadPlanModel
                                (
                                        ret_place_Name.get(b),
                                        URL_PREFIX_IMAGE + URLEncoder.encode(ret_place_pic.get(b), "UTF-8") + ".jpg",
                                        ret_place_ShortIntro.get(b)
                                );
                        viewModels.add(row);
                    }
                }catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            listView.setAdapter(adapter);
            LatLng t=new LatLng(lat,lng);
            map.animateCamera(CameraUpdateFactory.newLatLng(t));
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
