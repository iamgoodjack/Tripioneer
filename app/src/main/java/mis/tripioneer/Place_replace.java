package mis.tripioneer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.sql.StatementEvent;


/**
 * Created by Jenny on 2015/8/7. 行程中景點置換的附近搜尋(配合activity_map 的layout)
 */
public class Place_replace extends AppCompatActivity
{
    private ArrayList<String> replace_place_ID = new ArrayList<String>();
    private ArrayList<String> replace_place_Name = new ArrayList<String>();
    private ArrayList<String> replace_place_Pic = new ArrayList<String>();
    private  LatLng position,cho;
    private static GoogleMap map;
    final int par_num = 2;
    private  int x=0;
    private boolean choose_flag=false;
    private boolean replaced_flag=false;
    private LatLngBounds center,bunds;
    final String CASE="MAP";
    String request_place_id_name[] = new String[par_num];
    String request_place_id_value[] = new String[par_num];
    private static ArrayList<String> ret_place_X = new ArrayList<String>();
    private static ArrayList<String> ret_place_Y = new ArrayList<String>();
    private static ArrayList<String> ret_place_Name = new ArrayList<String>();
    private static ArrayList<String> rest_place_id = new ArrayList<String>();
    private static ArrayList<String> rest_place_X = new ArrayList<String>();
    private static ArrayList<String> rest_place_Y = new ArrayList<String>();
    private static ArrayList<String> rest_place_pic = new ArrayList<String>();
    private static ArrayList<String> rest_place_Name = new ArrayList<String>();
    private static ArrayList<String> rest_place_ShortIntro = new ArrayList<String>();
    private static ListView listView;
    private static List<RoadPlanModel> viewModels;
    private static RoadPlanAdapter adapter;
    private Toolbar toolbar;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout Drawer;
    String label;
    ActionBarDrawerToggle mDrawerToggle;
    String TITLES[] = {"推薦","訂閱","收藏庫","快選行程"};
    int ICONS[] = {R.drawable.ic_menu_recommand,R.drawable.ic_menu_channel,R.drawable.ic_menu_treasurebox,R.drawable.ic_menu_history};
    String NAME = "Gina";//TODO:GET USER NAME
    String EMAIL = "teemo@gmail.com";//TODO:GET USER EMAIL
    int PROFILE = R.drawable.ic_menu_account;
    private static final String TAG = "Place_replace";

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("Jenny", "uuu");
        setContentView(R.layout.activity_map);
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
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        viewModels = new ArrayList<RoadPlanModel>();
        adapter = new RoadPlanAdapter(this, viewModels);
        listView = (ListView) findViewById(R.id.listView_map);
        //listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        Log.d("vvv", "uuu");
        request_place_id_name[0] = "place_id";
        request_place_id_value[0] = "2";
        request_place_id_name[1] = "trip_id";
        request_place_id_value[1] = "1";

        new Thread(get_choose).start();
        //new Thread(get_trip_place).start();
        new Thread(get_Replaced).start();

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            Marker currentShown;

            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(currentShown)) {
                    marker.hideInfoWindow();
                    currentShown = null;
                } else {
                    marker.showInfoWindow();
                    currentShown = marker;
                }
                for (int u = 0; u < ret_place_Name.size(); u++) {
                    if (marker.getTitle().equals(ret_place_Name.get(u))) // if marker source is clicked
                    {
                        Toast.makeText(Place_replace.this, marker.getTitle(), Toast.LENGTH_SHORT).show();// display toast
                        //changeActivity(ret_place_id.get(u));
                    }

                }
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_place_replace, menu);
        return true;
    }
    Runnable get_choose = new Runnable()
    {
        final String GET_CHOOSE_PLACE = "http://140.115.80.224:8080/group4/get_choose_place.php";
        String ret;
        @Override
        public void run()
        {
            Log.d("TAG,","Robinn");
            ConnectServer connection = new ConnectServer(GET_CHOOSE_PLACE);
            ret = connection.connect(request_place_id_name, request_place_id_value, par_num);
            JsonParser parser = new JsonParser(CASE);
            ret_place_Name = parser.Parse(ret,"place_Name");
            ret_place_X = parser.Parse(ret, "place_X");
            ret_place_Y = parser.Parse(ret, "place_Y");
            Log.d("TAG",ret_place_Name.get(0)+"\n");
            Log.d("TAG",ret_place_X.get(0)+"\n");
            Log.d("TAG",ret_place_Y.get(0)+"\n");
            handler.sendEmptyMessage(0);
        }
    };

    Runnable get_Replaced = new Runnable()
    {
        final String GET_REPLACED_PLACE = "http://140.115.80.224:8080/group4/get_replaced_place.php";
        String rest;
        @Override
        public void run()
        {
            Log.d("TAG", "marsh");
            ConnectServer con = new ConnectServer(GET_REPLACED_PLACE);
            rest = con.connect(request_place_id_name, request_place_id_value, 2);
            JsonParser parser1 = new JsonParser(CASE);
            rest_place_id = parser1.Parse(rest,"place_ID");
            rest_place_Name = parser1.Parse(rest,"place_Name");
            rest_place_X = parser1.Parse(rest, "place_X");
            rest_place_Y = parser1.Parse(rest, "place_Y");
            rest_place_pic = parser1.Parse(rest,"place_pic");
            rest_place_ShortIntro = parser1.Parse(rest, "place_ShortIntro");
            x = rest_place_Name.size();
            Log.d("TAG","x="+String.valueOf(x));
            for(int a=0;a<x;a++)
            {
                Log.d("TAG ","x is"+String.valueOf(x));
                Log.d("TAG",rest_place_Name.get(a)+"\n");
                Log.d("TAG",rest_place_X.get(a)+"\n");
                Log.d("TAG",rest_place_Y.get(a)+"\n");
                Log.d("TAG",rest_place_ShortIntro.get(a)+"\n");
                Log.d("TAG",rest_place_pic.get(a)+"\n");
            }
            handler.sendEmptyMessage(1);
        }
    };

    Handler handler = new Handler()
    {
        private final String URL_PREFIX_IMAGE = "http://140.115.80.224:8080/group4/tainan_pic/";
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (msg.what==1)
            {
                replaced_flag=true;
                Log.d("TAG", "get_Replaced_place");
                Log.d("TAG", "xNumber"+String.valueOf(x));
                MarkerOptions orgin_options = new MarkerOptions();
                orgin_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                for(int b=0;b<x;b++)
                {
                    position=null;
                    position= new LatLng(Double.parseDouble(rest_place_Y.get(b)),Double.parseDouble(rest_place_X.get(b)));
                    orgin_options.position(position);
                    orgin_options.title(rest_place_Name.get(b));
                    map.addMarker(orgin_options);
                    try
                    {
                        if(!"".equals( rest_place_pic.get(b)))
                        {
                            Log.d("Hello","Hello");
                            RoadPlanModel row = new RoadPlanModel
                                    (
                                            rest_place_Name.get(b),
                                            URL_PREFIX_IMAGE + URLEncoder.encode(rest_place_pic.get(b), "UTF-8") + ".jpg",
                                            rest_place_ShortIntro.get(b)
                                    );
                            Log.d("TAG",rest_place_Name.get(b));
                            row.setID(rest_place_id.get(b));
                            viewModels.add(row);
                        }
                    }catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                listView.setAdapter(adapter);
                Log.d("TAG","get_Replaced_place_down");
            }
            else if (msg.what==(0))
            {
                choose_flag=true;
                Log.d("TAG","get_choose_place");
                cho= new LatLng(Double.parseDouble(ret_place_Y.get(0)),Double.parseDouble(ret_place_X.get(0)));
                map.addMarker(new MarkerOptions().position(cho).title(ret_place_Name.get(0)));
                //icon(BitmapDescriptorFactory.fromResource(android.R.drawable.)
                // map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
                Log.d("TAG", "get_choose_place_down");
            }
            if (choose_flag && replaced_flag)
            {
                //bunds=getBound(rest_place_X, rest_place_Y, ret_place_X, ret_place_Y);
                Log.d("TAG", "123456");
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(cho,14));
                //map.animateCamera(CameraUpdateFactory.newLatLngZoom(bunds.getCenter(), 18));
                /*map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    public void onCameraChange(CameraPosition cameraPosition) {
                        Log.d("onCameraChange","onCameraChange");
                        Get_Change get_change = new Get_Change();
                        get_change.execute(cameraPosition);
                    }
                });*/
                Log.d("down","Tag");
            }
        }
    };

    private LatLngBounds getBound(ArrayList<String> rest_place_x, ArrayList<String> rest_place_y, ArrayList<String> ret_place_x, ArrayList<String> ret_place_y)
    {
        rest_place_X.add(ret_place_X.get(0));
        rest_place_Y.add(ret_place_Y.get(0));
        double max_X=Double.parseDouble(rest_place_X.get(0));
        double min_X=Double.parseDouble(rest_place_X.get(0));
        double max_Y=Double.parseDouble(rest_place_Y.get(0));
        double min_Y=Double.parseDouble(rest_place_Y.get(0));
        for (int t=1; t<rest_place_X.size();t++)
        {
            if (Double.parseDouble(rest_place_X.get(t))>max_X)
            {
                max_X=Double.parseDouble(rest_place_X.get(t));
            }
            if (Double.parseDouble(rest_place_X.get(t))<min_X)
            {
                min_X=Double.parseDouble(rest_place_X.get(t));
            }
            if (Double.parseDouble(rest_place_Y.get(t))>max_Y)
            {
                max_Y=Double.parseDouble(rest_place_Y.get(t));
            }
            if (Double.parseDouble(rest_place_Y.get(t))<min_Y)
            {
                min_Y=Double.parseDouble(rest_place_Y.get(t));
            }
        }
        Log.d("minX",String.valueOf(min_X));
        Log.d("maxX",String.valueOf(max_X));
        Log.d("minY",String.valueOf(min_Y));
        Log.d("maxY",String.valueOf(min_Y));
        center = new LatLngBounds(new LatLng(min_Y, min_X), new LatLng(max_Y, max_X));
        return center;
    }

    private class Get_Change extends AsyncTask<CameraPosition,Void,String>
    {

        @Override
        protected String doInBackground(CameraPosition... params)
        {
            final String GET_MOVING_PLACE = "http://140.115.80.224:8080/group4/onCameraChange.php";
            String rett;
            String name []=new String[2];
            String value []=new String[2];
            name[0]="latitude";
            name[1]="longitude";
            name[2]="trip_id";
            value[0]=String.valueOf(params[0].target.latitude);
            value[1]=String.valueOf(params[0].target.longitude);
            value[2]=request_place_id_value[1];
            Log.d("onCameraChange","marsh");
            ConnectServer con = new ConnectServer(GET_MOVING_PLACE);
            rett = con.connect(name, value, 3);

            return rett;
        }

        protected void onPostExecute(String rett)
        {
            rest_place_Name.clear();
            rest_place_X.clear();
            rest_place_Y.clear();
            rest_place_pic.clear();
            rest_place_ShortIntro.clear();
            JsonParser parser1 = new JsonParser(CASE);
            rest_place_Name = parser1.Parse(rett,"place_Name");
            rest_place_X = parser1.Parse(rett, "place_X");
            rest_place_Y = parser1.Parse(rett, "place_Y");
            rest_place_pic = parser1.Parse(rett,"place_pic");
            rest_place_ShortIntro = parser1.Parse(rett, "place_ShortIntro");
            x = rest_place_Name.size();
            for(int a=0;a<x;a++)
            {
                Log.d("x is ",String.valueOf(x));
                Log.d("TED",rest_place_Name.get(a)+"\n");
                Log.d("TED",rest_place_X.get(a)+"\n");
                Log.d("TED",rest_place_Y.get(a)+"\n");
                Log.d("TED",rest_place_ShortIntro.get(a)+"\n");
                Log.d("TED",rest_place_pic.get(a)+"\n");
            }
            handler.sendEmptyMessage(1);
        }
    }

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
            case R.id.action_done:
                //TODO
                Toast.makeText(getBaseContext(), "置換景點完成!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putStringArrayListExtra("replace_id_list", replace_place_ID);
                intent.putStringArrayListExtra("replace_name_list", replace_place_Name);
                intent.putStringArrayListExtra("replace_pic_list", replace_place_Pic);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;

            default:
                //TODO
                Toast.makeText(getBaseContext(),"default", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectItem()
    {
        //Fragment fragment = null;
        //FOR Diff color action bar
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch ( label )
        {
            case "推薦":
                intent.setClass(Place_replace.this, MainActivity_mdsign.class);
                startActivity(intent);
                break;
            case "訂閱":
                /*fragment = new ChannelMain_frag();
                Bundle bundle = new Bundle();
                bundle.putString("channelid", "1");//TODO:SET TO SUBSCRIPTED CHANNEL
                fragment.setArguments(bundle);*/
                intent.setClass(Place_replace.this, ChannelMain.class);
                bundle.putString("channelid", "1");//item.getID()
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case "收藏庫":
                //fragment = new Collect_frag();
                //fragment.setArguments(bundle);
                intent.setClass(Place_replace.this, Collect.class);
                //TODO:SET TO COLLECT
                startActivity(intent);
                break;
            default:
                Drawer.closeDrawer(mRecyclerView);
                return;
            //break;
        }
        getSupportActionBar().setTitle(label);
        /*android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();*/
        Drawer.closeDrawer(mRecyclerView);
    }

    public void add_to_replace(String id)
    {
        replace_place_ID.add(id);
    }

    public void add_Name_to_replace(String name)
    {
        replace_place_Name.add(name);
        Log.d("Gina", "name in replace =" + replace_place_Name.get(replace_place_Name.size() - 1));
    }

    public void add_Pic_to_replace(String pic)
    {
        replace_place_Pic.add(pic);
        Log.d("Gina", "pic in replace =" + replace_place_Pic.get(replace_place_Pic.size() - 1));
    }
}
