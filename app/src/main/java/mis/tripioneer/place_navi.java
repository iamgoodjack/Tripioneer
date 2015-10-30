package mis.tripioneer;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jenny on 2015/9/7.
 */
public class place_navi extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    Marker loc_marker;
    final int DOWNLOAD_COMPLETE=1;
    final int LOCATION_GET=2;
    private static  String mode="";
    private String url;
    private static Double lat=null,lng=null;
    private static LatLng XY;
    private static LatLng pos=null;
    private static boolean Place_Latln_Get=false,Location_Get=false;
    private static GoogleMap map;
    private static TextView end;
    private static TextView sum;
    private static TextView dis_dur;
    private static ListView listView3;
    private static ImageButton driving_mode;
    private static ImageButton walking_mode;
    private SimpleAdapter adapter;
    private String[] request_name = new String[1];
    private String[] request_value = new String[1];
    private LocationManager locationMgr;
    private static ArrayList<String> ret_place_X = new ArrayList<String>();
    private static ArrayList<String> ret_place_Y = new ArrayList<String>();
    private static ArrayList<String> ret_place_Name = new ArrayList<String>();
    private static ArrayList<String> summary = new ArrayList<String>();
    private static ArrayList<String> sep_distance = new ArrayList<String>();
    private static ArrayList<String> sep_duration = new ArrayList<String>();

    String TITLES[] = {"推薦","訂閱","收藏庫","快選行程"};
    int ICONS[] = {R.drawable.ic_menu_recommand,R.drawable.ic_menu_channel,R.drawable.ic_menu_treasurebox,R.drawable.ic_ic_flag_black_32dp};


    String NAME = "Gina";//TODO:GET USER NAME
    String EMAIL = "teemo@gmail.com";//TODO:GET USER EMAIL
    int PROFILE = R.drawable.ic_menu_account;

    private Toolbar toolbar;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout Drawer;

    ActionBarDrawerToggle mDrawerToggle;
    private static final String TAG ="mainactivity_mdsign";
    String label;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_navi);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.place_navi_map)).getMap();
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        end = (TextView) findViewById(R.id.dest);
        sum=(TextView) findViewById(R.id.place_navi_sum);
        dis_dur=(TextView) findViewById(R.id.place_navi_disdur);
        driving_mode = (ImageButton) findViewById(R.id.driving);
        walking_mode = (ImageButton) findViewById(R.id.walking);
        listView3= (ListView) findViewById(R.id.listView3);
        listView3.setOnItemClickListener(this);
        locationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        Toast.makeText(place_navi.this, "123", Toast.LENGTH_LONG).show();


        //接收來自景點的ID
        Bundle data = this.getIntent().getExtras();
        request_value[0] = data.getString("place_id");
        request_name[0]="place_id";
        Toast.makeText(place_navi.this, "request_value[0]"+request_value[0], Toast.LENGTH_LONG).show();

        //request_value[0] ="7";
        //request_name[0]="place_id";
        Log.d("TAG", "request_value[0]:" + request_value[0]);

        driving_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = "mode=driving";
                Log.d("TAG", "driving onclick=" + mode);
                url = getDirectionsUrl(mode);
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
            }
        });

        walking_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = "mode=walking";
                Log.d("TAG", "walking onclick=" + mode);
                url = getDirectionsUrl(mode);
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);

        mRecyclerView.setHasFixedSize(true);// Letting the system know that the list objects are of fixed size

        mAdapter = new DrawerAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE,
                new DrawerAdapter.IMyViewHolderClicks()
                {

                    @Override
                    public void onTitle(View caller, String tag)
                    {
                        Log.d(TAG, "onClick, getTag=" + caller.getTag());
                        label =(String)caller.getTag();
                        selectItem();
                    }

                    @Override
                    public void onIcon(ImageView callerImage, String tag)
                    {
                        Log.d(TAG, "onClick, getTag=" + callerImage.getTag());
                        label =(String)callerImage.getTag();
                        selectItem();
                    }
                });

        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        Drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
            }



        };
        Drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        new Thread(connect_Server).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int id = item.getItemId();
        //home
        if (mDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        //action buttons
        switch (item.getItemId())
        {
            case R.id.action_edit:
                //TODO
                //Toast.makeText(getBaseContext(), "edit", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_search:
                //TODO
                //Toast.makeText(getBaseContext(),"search", Toast.LENGTH_SHORT).show();
                break;

            default:
                //TODO
                //Toast.makeText(getBaseContext(),"default", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**/
    public void selectItem()
    {
        Fragment fragment = null;
        //FOR Diff color action bar
        /*Intent intent = new Intent();
        Bundle bundle = new Bundle();*/
        switch ( label )
        {
            case "推薦":
                fragment = new Recommendation_frag();
                break;
            case "訂閱":
                //FOR FRAGMENT STRUCTURE(Same color action bar)
                fragment = new ChannelMain_frag();
                Bundle bundle = new Bundle();
                bundle.putString("channelid", "1");//TODO:SET TO SUBSCRIPTED CHANNEL
                fragment.setArguments(bundle);
                //FOR Diff color action bar
                /*intent.setClass(MainActivity_mdsign.this, ChannelMain.class);
                bundle.putString("channelid", "1");//item.getID()
                intent.putExtras(bundle);
                startActivity(intent);*/
                break;
            case "收藏庫":
                fragment = new Collect_frag();
                //FOR Diff color action bar
                /*intent.setClass(MainActivity_mdsign.this, Collect.class);
                bundle.putString("channelid", "1");//TODO:SET TO COLLECT
                intent.putExtras(bundle);
                startActivity(intent);*/
                break;
            //TODO:FRAGMENT PLACE_REPLACE
            case "快選行程":
                Intent intent = new Intent(place_navi.this,Search.class);
                Drawer.closeDrawer(mRecyclerView);
                startActivity(intent);
                return;
            //break;
            default:
                Drawer.closeDrawer(mRecyclerView);
                return;
            //break;
        }
        getSupportActionBar().setTitle(label);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        Drawer.closeDrawer(mRecyclerView);
    }

    Runnable connect_Server = new Runnable()
    {
        String CASE="PLACE_NAV";
        final String GET_CHOOSE_PLACE = "http://140.115.80.224:8080/group4/get_choose_place.php";
        String ret;
        public void run ()
        {
            Log.d("TAG,","connect server");
            ConnectServer connect = new ConnectServer(GET_CHOOSE_PLACE);
            ret = connect.connect(request_name, request_value, 1);
            JsonParser parser = new JsonParser(CASE);
            ret_place_Name = parser.Parse(ret,"place_Name");
            ret_place_X = parser.Parse(ret, "place_X");
            ret_place_Y = parser.Parse(ret, "place_Y");
            Log.d("TAG,","name"+ret_place_Name.get(0));
            Log.d("TAG,","X"+ret_place_X.get(0));
            Log.d("TAG,","Y"+ret_place_Y.get(0));
            handler.sendEmptyMessage(DOWNLOAD_COMPLETE);
        }
    };

    Handler handler = new Handler()
    {
        private String url;
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case DOWNLOAD_COMPLETE:
                    Place_Latln_Get=true;
                    Log.d("TAG", "place_latlng_get");
                    Log.d("TAG", "count=" + String.valueOf(ret_place_Name.size()));
                    end.setText("終點: " + ret_place_Name.get(ret_place_Name.size() - 1));
                    XY = new LatLng(Double.parseDouble(ret_place_Y.get(0)),Double.parseDouble(ret_place_X.get(0)));
                    Log.d("TAG", "destination mark");

                    break;
                case LOCATION_GET:
                    Location_Get=true;
                    Log.d("TAG","location_get");
                    break;
                default:
                    break;
            }
            if (Place_Latln_Get && Location_Get)
            {
                if (lat != null && lng !=null)
                {
                    Log.d("TAG", "handler");
                    mode="mode=driving";
                    url = getDirectionsUrl(mode);
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                    ArrayList<LatLng> center = compareLatLng(ret_place_Y.get(0), ret_place_X.get(0), String.valueOf(lat), String.valueOf(lng));
                    LatLngBounds bounds = new LatLngBounds(center.get(1),center.get(0));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 8));
                }
            }
        }
    };

    private ArrayList<LatLng> compareLatLng(String a_Y , String a_X, String b_Y , String b_X )
    {
        ArrayList <LatLng> bound = new ArrayList<LatLng>();
        Log.d("TAG","compareLatLng");
        Double N,S,E,W;
        if (Double.parseDouble(a_Y)>Double.parseDouble(b_Y))
        {
            N=Double.parseDouble(a_Y);
            S=Double.parseDouble(b_Y);
        }else
        {
            N=Double.parseDouble(b_Y);
            S=Double.parseDouble(a_Y);
        }
        if(Double.parseDouble(a_X)>Double.parseDouble(b_X))
        {
            E=Double.parseDouble(a_X);
            W=Double.parseDouble(b_X);
        }else
        {
            E=Double.parseDouble(b_X);
            W=Double.parseDouble(a_X);
        }
        Log.d("TAG", "NorthEast=" + String.valueOf(N) + "," + String.valueOf(E));
        Log.d("TAG","SouthWest="+String.valueOf(S)+","+String.valueOf(W));
        LatLng NorthEast=new LatLng(N,E);
        LatLng SouthWest=new LatLng(S,W);
        bound.add(NorthEast);
        bound.add(SouthWest);
        return bound;
    }

    private String getDirectionsUrl( String mode)
    {
        Log.d("TAG","getDirectionsUrl");
        String parameters;
        String str_origin;
        String str_dest;
        String sensor = "sensor=false";
        str_origin = "origin="+lat+","+lng;
        str_dest = "destination="+ret_place_Y.get(0)+","+ret_place_X.get(0);
        parameters = str_origin + "&" + str_dest + "&" + sensor+"&"+mode+"&"+"alternatives=true";
        String URL = "https://maps.googleapis.com/maps/api/directions/json?"+parameters+"&language=zh-TW";
        Log.d("TAG",URL);
        return URL;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        String data = "";
        InputStream iStream;
        HttpURLConnection urlConnection = null;
        URL Url;

        @Override
        protected String doInBackground(String... params) {
            Log.d("TAG", "URL: " + params[0]);
            try {
                Url = new URL(params[0]);
                urlConnection = (HttpURLConnection) Url.openConnection();
                Log.d("TAG", "URL connect");
                urlConnection.connect();

                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                Log.d("TAG", "DATA: " + data);
                br.close();
                iStream.close();
                urlConnection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("Exception download url", e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("IOException", e.toString());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Exception ", e.toString());
            } finally {
                return data;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("TAG", "Download task onpost");
            JSONObject jObject;
            ArrayList<ArrayList<String>> plan = new ArrayList<>();
            ArrayList<ArrayList<String>> step_dis = new ArrayList<>();
            ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

            Log.d("TAG", "switch mode=" + mode);
            try {
                map.clear();
                jObject = new JSONObject(result);
                RoutesTextJSONParser par = new RoutesTextJSONParser();
                summary = par.parse(jObject, "SUMMARY");
                sep_distance = par.parse(jObject, "PLACE_NAVI_DISTANCE");  //in meters
                sep_duration = par.parse(jObject, "PLACE_NAVI_DURATION"); //in seconds

                Log.d("TAG", "Size of summary" + String.valueOf(summary.size()));
                Log.d("TAG", "Size of sep_dis" + String.valueOf(sep_distance.size()));
                Log.d("TAG", "Size of sep_dur" + String.valueOf(sep_duration.size()));
                for (int i = 0; i < summary.size(); i++) {
                    Log.d("TAG", "summary=" + summary.get(i));
                    Log.d("TAG", "sep_dis=" + sep_distance.get(i));
                    Log.d("TAG", "sep_dur=" + sep_duration.get(i));
                }

                //parse 出文字指示路段
                plan = par.parseroad(jObject, 0, "PLACE_NAVI_TEXT");
                Log.d("TAG", "PLACE_NAVI_TEXT size:" + plan.size());

                //parse出每一個step的distance
                step_dis = par.parseroad(jObject, 0, "PLACE_NAVI_STEPS_DIS");
                Log.d("TAG", "step_dis size:" + step_dis.size());

                bindtext(plan, step_dis);
                getMap(result);

                MarkerOptions dest_options = new MarkerOptions();
                dest_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                dest_options.position(XY);
                dest_options.title("終點:" + ret_place_Name.get(0));
                map.addMarker(dest_options);
                Log.d("TAG", "marker");

                if(loc_marker!=null)
                {
                    loc_marker.remove();
                }
                MarkerOptions pos_options = new MarkerOptions();
                pos_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                pos_options.position(pos);
                pos_options.title("目前位置");
                loc_marker=map.addMarker(pos_options);

            } catch (Exception e) {
                e.printStackTrace();
            }
            sum.setText(summary.get(0));
            dis_dur.setText(sep_distance.get(0) + "/" + sep_duration.get(0));
        }


    }

    private void getMap(String json) {
        Log.d("TAG", "getMap");
        JSONObject jObject;
        List<List<HashMap<String, String>>> result = null;
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        try {
            Log.d("TAG", "TAG");
            jObject = new JSONObject(json);
            DirectionsJSONParser parser = new DirectionsJSONParser();
            //parse出畫在地圖上的經緯度組
            result = parser.parse(jObject);


            Log.d("TAG","routes="+String.valueOf(result.size()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            lineOptions = new PolylineOptions();
            //Fetching all the points in all routes，用2層迴圈
            Log.d("TAG","000");
            //假設印出第一條路
            points = new ArrayList<LatLng>();
            for (int e = 0; e < result.get(0).size(); e++)
            {
                HashMap<String, String> point = result.get(0).get(e);
                double Lat = Double.parseDouble(point.get("lat"));
                double Lng = Double.parseDouble(point.get("lng"));
                LatLng p = new LatLng(Lat, Lng);
                points.add(p);
            }
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
        lineOptions.addAll(points);
        lineOptions.width(8);
        lineOptions.color(Color.RED);
        Log.d("TAG", "mapmove_end");
        map.addPolyline(lineOptions);

    }

    private void bindtext(ArrayList<ArrayList<String >> plan , ArrayList<ArrayList<String >> step_dis)
    {
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
        Log.d("TAG","plan length:"+String.valueOf(plan.size())); //plan長度即是路線有幾種
        Log.d("TAG","plan0_size:"+String.valueOf(plan.get(0)));

        //路線數量即為plan數量
        for (int p=0;p<plan.size();p++)
        {
            for(int u=0;u<plan.get(p).size();u++)
            {
                Log.d("TAG","plan"+String.valueOf(p)+"="+plan.get(p).get(u));
                Log.d("TAG","step_dis ="+String.valueOf(p)+"="+step_dis.get(p).get(u));
            }
        }

        //只顯示第一條路線
        for(int j=0;j <plan.get(0).size();j++)
        {
            HashMap<String,String> item = new HashMap<String,String>();
            item.put( "instruction", plan.get(0).get(j));
            item.put("step_dis", step_dis.get(0).get(j));
            list.add( item );
        }

        //新增SimpleAdapter
        adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, new String[] { "instruction","step_dis" },
                new int[] { android.R.id.text1, android.R.id.text2 } );
        listView3.setAdapter(adapter);
    }

    //You need to create and register the listener on the UI thread.
    public void initProvider()
    {
        int minTime = 50000;//ms
        int minDist = 5000;//meter
        Log.d("TAG","InitProvider ");
        if(!locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Toast.makeText(place_navi.this, "請開啟GPS定位功能", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        if(locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDist, locationListener);
            //locationMgr.addGpsStatusListener(gpsListener);
            locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, locationListener);
            Toast.makeText(place_navi.this, "provider:gps", Toast.LENGTH_SHORT).show();
        }
        else if (locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            Toast.makeText(place_navi.this, "provider:network", Toast.LENGTH_SHORT).show();
            locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, locationListener);
        }else
        {
            Toast.makeText(place_navi.this, "請開啟定位功能:", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    protected void onPause()
    {
        super.onPause();
        Log.d("TAG", "onPause");
        locationMgr.removeUpdates(locationListener);

    }

    protected void onDestroy()
    {
        super.onDestroy();
        Log.d("TAG", "onDestroy");
    }

    protected void onResume()
    {
        super.onResume();
        Log.d("TAG", "onResume");
        initProvider();
    }

    private void updateWithNewLocation(Location location)
    {
        Log.d("TAG","updatelocation");
        String where = "";
        if (location != null)
        {
            //經度
            lng = location.getLongitude();
            //緯度
            lat = location.getLatitude();
            pos=new LatLng(lat,lng);
            where = "經度: " + lng + "\n緯度: " + lat ;
            Log.d("TAG", "Latlng=" + where);
            handler.sendEmptyMessage(LOCATION_GET);

        }
        else {
            where = "No location found.";
            Toast.makeText(place_navi.this, "no pos", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "no position"+where);
        }

    }

    LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            locationMgr.removeUpdates(locationListener);
            Log.d("TAG", "updateWithNewLoc start");
            updateWithNewLocation(location);
            Log.d("TAG", "updateWithNewLoc end");
            initProvider();
            Log.d("TAG", "onLocationChanged");
            Toast.makeText(place_navi.this, "onLocationChanged", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("TAG", "onProviderDisabled");
            Toast.makeText(place_navi.this, "定位提供者關閉，請重啟", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("TAG","onProviderEnabled");
            Toast.makeText(place_navi.this, "onProviderEnabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("TAG", "Status Changed: Out of Service");
                    Toast.makeText(place_navi.this, "Status Changed: Out of Service", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("TAG", "Status Changed: Temporarily Unavailable");
                    Toast.makeText(place_navi.this, "Status Changed: Temporarily Unavailable", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.AVAILABLE:
                    Log.d("TAG", "Status Changed: Available");
                    Toast.makeText(place_navi.this, "Status Changed: Available", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
