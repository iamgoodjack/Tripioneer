package mis.tripioneer;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jenny on 2015/8/14. 放大鏡功能
 */
public class Search_frag extends Fragment {

    private final String TAG ="Search_frag";
    private Context context;
    private GoogleMap map;
    private LocationManager locationMgr;
    private  LatLng position;
    private  double lat,lng;
    final String CASE = "MAP";
    private final int par_num = 2;
    private static int x;
    private SimpleAdapter adapter;
    private static ListView listView;
    String request_place_id_name[] = new String[par_num];
    String request_place_id_value[] = new String[par_num];
    private static ArrayList<String> ret_place_X = new ArrayList<String>();
    private static ArrayList<String> ret_place_Y = new ArrayList<String>();
    private static ArrayList<String> ret_place_Name = new ArrayList<String>();
    private static ArrayList<String> ret_place_id = new ArrayList<String>();
    private static ArrayList<String> ret_pttrip_id = new ArrayList<String>();
    private static ArrayList<String> ret_pttrip_name = new ArrayList<String>();
    private static ArrayList<String> ret_total_time = new ArrayList<String>();
    private static ArrayList<String> ret_like_num = new ArrayList<String>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG", "oncreate");
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View  v = inflater.inflate(R.layout.activity_map, container, false);
        //TODO BUG
        //map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.clear();
        listView = (ListView) v.findViewById(R.id.listView_map);
        listView.setOnItemClickListener
                (new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        if (id==0)
                        {
                            /*Toast.makeText(Search.this, "ID：" + position +
                                    " 選單文字："+ parent.getItemAtPosition(position).toString()
                                    , Toast.LENGTH_SHORT).show();*/
                            intent.setClass(context, Trip_mdsign.class);
                            bundle.putString("tripid", ret_pttrip_id.get(0));
                            bundle.putString("title", ret_pttrip_name.get(0));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else if (id==1)
                        {
                            /*Toast.makeText(Search.this, "ID：" + position +
                                    " 選單文字："+ parent.getItemAtPosition(position).toString()
                                    , Toast.LENGTH_SHORT).show();*/
                            intent.setClass(context, Trip_mdsign.class);
                            bundle.putString("tripid", ret_pttrip_id.get(1));
                            bundle.putString("title", ret_pttrip_name.get(1));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else if (id==2)
                        {
                            /*Toast.makeText(Search.this, "ID：" + position +
                                    " 選單文字："+ parent.getItemAtPosition(position).toString()
                                    , Toast.LENGTH_SHORT).show();*/
                            intent.setClass(context, Trip_mdsign.class);
                            bundle.putString("tripid", ret_pttrip_id.get(2));
                            bundle.putString("title", ret_pttrip_name.get(2));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else
                        {
                            ;
                        }

                    }
                });
        locationMgr=(LocationManager) context.getSystemService(context.LOCATION_SERVICE);
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
                        Toast.makeText(context, marker.getTitle(), Toast.LENGTH_SHORT).show();// display toast
                        // changeActivity(ret_place_id.get(u));
                        Get_Releated_Trip get_releated_trip = new Get_Releated_Trip();
                        get_releated_trip.execute(ret_place_id.get(u));
                        Log.d("TAGTAGTAGGG",ret_place_id.get(u));
                    }

                }
                return true;
            }
        });

        return inflater.inflate(R.layout.collect, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(TAG, "onStop");

    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        Log.d(TAG, "onDetach");

    }

    private  class Get_Releated_Trip extends AsyncTask<String, Void, ArrayList<String>>
    {
        final String CONNECT_SERVER = "http://140.115.80.224:8080/group4/releated_trip_search.php";
        String r_place_id_name[] = new String[1];
        String r_place_id_value[] = new String[1];
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
        String ret;
        @Override
        protected ArrayList<String> doInBackground(String... params)
        {
            r_place_id_name[0]="place_id";
            r_place_id_value[0]=params[0];
            ConnectServer connection = new ConnectServer(CONNECT_SERVER);
            ret = connection.connect(r_place_id_name, r_place_id_value, 1);
            Log.d("TAG","TAGTAGTAGTAG"+ret);

            JsonParser parser = new JsonParser(CASE);
            ret_pttrip_id = parser.Parse(ret, "trip_ID");
            ret_pttrip_name=parser.Parse(ret, "trip_Name");
            ret_total_time=parser.Parse(ret, "trip_TotalTime");
            ret_like_num=parser.Parse(ret, "trip_LikeNum");

            for(int i=0;i<ret_pttrip_id.size();i++)
            {
                Log.d("TAGGGGG",ret_pttrip_id.get(i));
                Log.d("TAGGGGG",ret_pttrip_name.get(i));
                Log.d("TAGGGGG",ret_total_time.get(i));
                Log.d("TAGGGGG",ret_like_num.get(i));
            }
            return ret_pttrip_name;
        }

        protected void onPostExecute(ArrayList rett)
        {
            for(int i=0;i<rett.size();i++)
            {
                Log.d("TAG","haha");
                HashMap<String,String> item = new HashMap<String,String>();
                item.put("name", ret_pttrip_name.get(i));
                item.put( "des", "行程總時間:"+ret_total_time.get(i)+"小時\n行程喜愛度:"+ret_like_num.get(i)+"個推薦");
                list.add(item);
            }
            adapter = new SimpleAdapter(context, list, android.R.layout.simple_list_item_2,
                    new String[] { "name","des" },
                    new int[] { android.R.id.text1, android.R.id.text2 } );
            listView.setAdapter(adapter);

        }
    }

    public void onPause() {
        Log.d("TAG", "onpause");
        //locationMgr.removeUpdates(locationListener);
        //locationMgr.removeGpsStatusListener(gpsListener);
        super.onPause();
    }

    public void onResume()
    {
        super.onResume();
        //initProvider();
        Log.d("TAG", "onResume");
    }

    private void updateWithNewLocation(Location location)
    {
        map.clear();
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
        MarkerOptions o_option = new MarkerOptions();
        Resources resources = this.getResources();
        final int resourceId = resources.getIdentifier("ic_v3", "drawable",
                context.getPackageName());
        o_option.icon(BitmapDescriptorFactory.fromResource(resourceId));
        o_option.position(new LatLng(lat,lng));
        Log.d("TAG","now place");
        o_option.title("目前位置");
        map.addMarker(o_option);
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
            Log.d("TAGVVVVV",ret);
            return ret;
        }

        protected void onPostExecute(String ret)
        {
            Log.d("TAG","onpost");
            JsonParser parser = new JsonParser(CASE);
            ret_place_Name = parser.Parse(ret,"place_Name");
            ret_place_X = parser.Parse(ret, "place_X");
            ret_place_Y = parser.Parse(ret, "place_Y");
            ret_place_id = parser.Parse(ret, "place_ID");
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
            map.animateCamera(CameraUpdateFactory.newLatLngZoom((t), 8));
            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(bunds.getCenter(),14));
        }
    }

    public void initProvider()
    {
        int minTime = 5000;//ms
        int minDist = 1000;//meter
        Log.d("TAG", "iniProvider");
        if(!locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Log.d("TAG","!locmgr GPS");
            Toast.makeText(context, "請開啟GPS定位功能並重啟此APP", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        if(locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Log.d("TAG","locmgr GPS");
            locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,minTime,minDist,locationListener);
            locationMgr.addGpsStatusListener(gpsListener);
            locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, locationListener);
            Toast.makeText(context, "provider:gps", Toast.LENGTH_SHORT).show();
        }
        else if (locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            Log.d("TAG","locmgr network");
            Toast.makeText(context, "provider:network", Toast.LENGTH_SHORT).show();
            locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, locationListener);
        }else
        {
            Toast.makeText(context, "請開啟定位功能:", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            updateWithNewLocation(location);
            Log.d("TAG", "onLocationChanged");
            Toast.makeText(context, "onLocationChanged", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("TAG", "onProviderDisabled");
            Toast.makeText(context, "定位提供者關閉，請重啟", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("TAG","onProviderEnabled");
            Toast.makeText(context, "onProviderEnabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("Jenny", "Status Changed: Out of Service");
                    Toast.makeText(context, "Status Changed: Out of Service", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("Jenny", "Status Changed: Temporarily Unavailable");
                    Toast.makeText(context, "Status Changed: Temporarily Unavailable", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.AVAILABLE:
                    Log.d("Jenny", "Status Changed: Available");
                    Toast.makeText(context, "Status Changed: Available", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context, "GPS_EVENT_STARTED", Toast.LENGTH_SHORT).show();
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.d("penny", "GPS_EVENT_STOPPED");
                    Toast.makeText(context, "GPS_EVENT_STOPPED", Toast.LENGTH_SHORT).show();
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.d("penny", "GPS_EVENT_FIRST_FIX");
                    Toast.makeText(context, "GPS_EVENT_FIRST_FIX", Toast.LENGTH_SHORT).show();
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.d("penny", "GPS_EVENT_SATELLITE_STATUS");
                    break;
            }
        }
    };

}
