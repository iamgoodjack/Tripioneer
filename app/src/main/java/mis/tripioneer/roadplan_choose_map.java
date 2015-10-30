package mis.tripioneer;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.provider.Settings;
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
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jenny on 2015/9/3.
 */
public class roadplan_choose_map  extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    private static GoogleMap map;
    private static String json;
    private static String mode;
    private static int position;
    private static String summary;
    private static String disdur;
    private static TextView from;
    private static TextView end;
    private static TextView sum;
    private static TextView dis_dur;
    private static TextView way;
    private  static LatLng orginLatlng;
    private  static LatLng destinationLatlng;
    private static ListView listView2;
    private SimpleAdapter adapter;
    private ArrayList<String> place_X;
    private ArrayList<String> place_Y;
    private ArrayList<String> place_Name;
    private LocationManager locationMgr;
    Marker loc_marker;


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
    private static final String TAG ="roadplan_choose_map";
    String label;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roadplan_choose_map);
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
        setSupportActionBar(toolbar);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.roadplan_choose_map)).getMap();
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        from = (TextView) findViewById(R.id.from);
        end = (TextView) findViewById(R.id.end);
        way = (TextView) findViewById(R.id.way);
        sum=(TextView) findViewById(R.id.sum);
        dis_dur=(TextView) findViewById(R.id.disdur);
        listView2= (ListView) findViewById(R.id.listView2);
        listView2.setOnItemClickListener(this);
        //Toast.makeText(roadplan_choose_map.this, "123", Toast.LENGTH_LONG).show();
        locationMgr=(LocationManager) getSystemService(LOCATION_SERVICE);

        Bundle bundle = this.getIntent().getExtras();
        json = bundle.getString("json");
        mode = bundle.getString("mode");
        position = bundle.getInt("position");
        summary = bundle.getString("summary");
        disdur =  bundle.getString("disdur");
        place_X = this.getIntent().getExtras().getStringArrayList("place_X");
        place_Y = this.getIntent().getExtras().getStringArrayList("place_Y");
        place_Name = this.getIntent().getExtras().getStringArrayList("place_Name");
        orginLatlng = new LatLng(Double.parseDouble(place_Y.get(0)),Double.parseDouble(place_X.get(0)));
        destinationLatlng = new LatLng(Double.parseDouble(place_Y.get(place_Y.size()-1)),Double.parseDouble(place_X.get(place_X.size()-1))) ;
        initProvider();
        for(int z=0;z<place_X.size();z++)
        {
            Log.d("TAG","z="+String.valueOf(z));
            Log.d("TAG", "place_X" + place_X.get(z));
            Log.d("TAG", "place_Y" + place_Y.get(z));
            Log.d("TAG", "place_Name" + place_Name.get(z));
        }
        Log.d("TAG", "json:" + json);
        Log.d("TAG", "mode:" + mode);
        Log.d("TAG", "position:" + String.valueOf(position));
        //Toast.makeText(roadplan_choose_map.this, mode, Toast.LENGTH_LONG).show();
        getMap(json, position);
        getPlan(json, position);
        drawPoint();
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
                Intent intent = new Intent(roadplan_choose_map.this,Search.class);
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

    private void drawPoint()
    {
        String waytext="";
        from.setText("起點:" + place_Name.get(0));
        end.setText("目的地:" + place_Name.get(place_Name.size() - 1));
        if (place_Name.size()==2)
        {
            way.setText("中間景點:無");
        }
        else
        {
            for(int z=1;z<place_Name.size()-1;z++)
            {
                waytext+=place_Name.get(z)+",";
            }
            way.setText("中間景點:"+waytext);
        }
        sum.setText(summary);
        dis_dur.setText(disdur);
        MarkerOptions orgin_options = new MarkerOptions();
        orgin_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        orgin_options.position(orginLatlng);
        orgin_options.title("起點:"+place_Name.get(0));
        map.addMarker(orgin_options);

        MarkerOptions dest_options = new MarkerOptions();
        dest_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        dest_options.position(destinationLatlng);
        dest_options.title("終點:"+place_Name.get(place_Name.size()-1));
        map.addMarker(dest_options);

        MarkerOptions way_options = new MarkerOptions();
        way_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        for(int c=1;c<place_Name.size()-1;c++)
        {
            way_options.position(new LatLng(Double.parseDouble(place_Y.get(c)),Double.parseDouble(place_X.get(c))));
            way_options.title(place_Name.get(c));
            map.addMarker(way_options);
        }

        //focus 在a -> b 的畫面
        //切換的部分再麻煩阿婕了
        //LatLngBounds( SouthWest corner , NorthEast corner)

        ArrayList<LatLng> center=compareLatLng(place_Y.get(0),place_X.get(0),place_Y.get(1),place_X.get(1));
        LatLngBounds bounds = new LatLngBounds(center.get(1),center.get(0));

       //map.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 15));
 
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 15));

    }

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

    private void getPlan(String json,int position) {
        Log.d("TAG", "getPlan");
        JSONObject jObject;
        ArrayList<ArrayList<String>> plan = null;
        ArrayList<ArrayList<String>> step_dis = null;

        try {
            jObject = new JSONObject(json);
            RoutesTextJSONParser par = new RoutesTextJSONParser();

            //parse 出文字指示路段
            plan = par.parseroad(jObject, position, "HTML_INSTRUCTION");
            Log.d("TAG","HTML_instruction size:"+plan.size());

            //parse出每一個的distance
            step_dis = par.parseroad(jObject, position, "STEPS_DIS");
            Log.d("TAG","step_dis size:"+step_dis.size());
            bindtext(plan,step_dis);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void bindtext(ArrayList<ArrayList<String >> plan , ArrayList<ArrayList<String >> step_dis)
    {
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
        Log.d("TAG","plan length:"+String.valueOf(plan.size()));
        Log.d("TAG","plan0_size:"+String.valueOf(plan.get(0)));

        //a到b、b到c的路線在log中顯示
        for (int p=0;p<plan.size();p++)
        {
            for(int u=0;u<plan.get(p).size();u++)
            {
                Log.d("TAG","plan"+String.valueOf(p)+plan.get(p).get(u));
                Log.d("TAG","step_dis ="+String.valueOf(p)+step_dis.get(p).get(u));
            }
        }

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
        listView2.setAdapter(adapter);


    }

    private void getMap(String json,int position) {
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

        try{
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();
            List<HashMap<String, String>> path = result.get(position);
            // Fetching all the points in i-th route
            for(int j=0;j<path.size();j++)
            {
                HashMap<String,String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng pos = new LatLng(lat, lng);
                points.add(pos);
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

    private void updateWithNewLocation(Location location)
    {
        double lat=0,lng=0;
        Log.d("TAG", "updateNewLocation");
        String where = "";
        if (location != null) {
            //經度
            lng = location.getLongitude();
            //緯度
            lat = location.getLatitude();
            where = "經度: " + lng + "\n緯度: " + lat;
            if(loc_marker!=null)
            {
                loc_marker.remove();
            }
            MarkerOptions o_option = new MarkerOptions();
            Resources resources = this.getResources();
            final int resourceId = resources.getIdentifier("ic_v3", "drawable",
                this.getPackageName());
            o_option.icon(BitmapDescriptorFactory.fromResource(resourceId));
            o_option.position(new LatLng(lat, lng));
            Log.d("TAG", "now place");
            o_option.title("目前位置");
        loc_marker = map.addMarker(o_option);
        } else {
            where = "No location found.";
        }
        Log.d("TAG", where);
    }

    public void initProvider()
    {
        int minTime = 5000;//ms
        int minDist = 1000;//meter
        Log.d("TAG", "iniProvider");
        if(!locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Log.d("TAG","!locmgr GPS");
           // Toast.makeText(roadplan_choose_map.this, "請開啟GPS定位功能並重啟此APP", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        if(locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Log.d("TAG","locmgr GPS");
            locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,minTime,minDist,locationListener);
            locationMgr.addGpsStatusListener(gpsListener);
            locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, locationListener);
            //Toast.makeText(roadplan_choose_map.this, "provider:gps", Toast.LENGTH_SHORT).show();
        }
        else if (locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            Log.d("TAG","locmgr network");
            //Toast.makeText(roadplan_choose_map.this, "provider:network", Toast.LENGTH_SHORT).show();
            locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, locationListener);
        }else
        {
            //Toast.makeText(roadplan_choose_map.this, "請開啟定位功能:", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
            Log.d("TAG", "onLocationChanged");
            //Toast.makeText(roadplan_choose_map.this, "onLocationChanged", Toast.LENGTH_SHORT).show();
        }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("TAG", "onProviderDisabled");
               // Toast.makeText(roadplan_choose_map.this, "定位提供者關閉，請重啟", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("TAG", "onProviderEnabled");
                //Toast.makeText(roadplan_choose_map.this, "onProviderEnabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.d("Jenny", "Status Changed: Out of Service");
                        //Toast.makeText(roadplan_choose_map.this, "Status Changed: Out of Service", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.d("Jenny", "Status Changed: Temporarily Unavailable");
                       // Toast.makeText(roadplan_choose_map.this, "Status Changed: Temporarily Unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationProvider.AVAILABLE:
                        Log.d("Jenny", "Status Changed: Available");
                       // Toast.makeText(roadplan_choose_map.this, "Status Changed: Available", Toast.LENGTH_SHORT).show();
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
                       // Toast.makeText(roadplan_choose_map.this, "GPS_EVENT_STARTED", Toast.LENGTH_SHORT).show();
                        break;
                    case GpsStatus.GPS_EVENT_STOPPED:
                        Log.d("penny", "GPS_EVENT_STOPPED");
                       // Toast.makeText(roadplan_choose_map.this, "GPS_EVENT_STOPPED", Toast.LENGTH_SHORT).show();
                        break;
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        Log.d("penny", "GPS_EVENT_FIRST_FIX");
                       // Toast.makeText(roadplan_choose_map.this, "GPS_EVENT_FIRST_FIX", Toast.LENGTH_SHORT).show();
                        break;
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        Log.d("penny", "GPS_EVENT_SATELLITE_STATUS");
                        break;
                }
            }
        };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    protected void onPause() {
        Log.d("TAG", "onpause");
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

}
