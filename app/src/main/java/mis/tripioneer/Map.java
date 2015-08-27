package mis.tripioneer;

/**
 * Created by Jenny on 2015/7/28.
 */
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.ArrayList;
import java.util.List;

//地圖上標點
public class Map extends FragmentActivity
{
    private static LatLng position,pos;
    private static GoogleMap map;
    private final static int DOWNLOAD_COMPLETE =1;
    private static ListView listView;
    private static List<ViewModel> viewModels;
    private static ViewAdapter adapter;
    private final String CASE="MAP_GET";
    int RET_PARAM_NUM;
    final int par_num=1;
    double maxLat;
    double minLat;
    double maxLon;
    double minLon;
    private LatLngBounds center;
    String request_name[] = new String[par_num];
    String request_value[] = new String[par_num];
    String request_name1[] = new String[par_num];
    String request_value1[] = new String[par_num];
    private static ArrayList<String> ret_place_X = new ArrayList<String>();
    private static ArrayList<String> ret_place_Y = new ArrayList<String>();
    private static ArrayList<String> ret_place_Name = new ArrayList<String>();
    private static ArrayList<String> rest_place_X = new ArrayList<String>();
    private static ArrayList<String> rest_place_Y = new ArrayList<String>();
    private static ArrayList<String> rest_place_Name = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {


        /*Bundle placedata = this.getIntent().getExtras();
                String placeid = placedata.getString("specifiedid");*/
        request_name[0]="place_id";
        request_value[0]="20874";
        request_name1[0]="trip_id";
        request_value1[0]="97";
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        viewModels = new ArrayList<ViewModel>();
        adapter = new ViewAdapter(this, viewModels);
        listView =(ListView)findViewById(R.id.listView_map);
        Log.d("vvv", "uuu");
        new Thread(connect_Server).start();
    }


    Runnable connect_Server = new Runnable()
    {
        final String MAP_URL="http://140.115.80.224:8080/group4/Map_get.php";
        final String GET_PLACE_FROM_TRIP="http://140.115.80.224:8080/group4/getTripL.php";
        String ret,rest;

        @Override
        public void run()
        {
            Log.d("cccc",request_value[0]);
            ConnectServer connection = new ConnectServer(MAP_URL);
            ret = connection.connect(request_name, request_value, 1);
            Log.d("connecttttt",request_value[0]);
            JsonParser parser = new JsonParser(CASE);
            Log.d("tttt","kakakak");
            ret_place_Name = parser.Parse(ret,"place_Name");
            ret_place_X = parser.Parse(ret, "place_X");
            ret_place_Y = parser.Parse(ret,"place_Y");
            Log.d("Jenny",ret_place_Name.get(0));
            Log.d("Jenny", ret_place_X.get(0));
            Log.d("Jenny", ret_place_Y.get(0));

            ConnectServer con = new ConnectServer( GET_PLACE_FROM_TRIP);
            rest = con.connect(request_name1, request_value1, 1);
            rest_place_Name= parser.Parse(rest, "place_Name");
            rest_place_X = parser.Parse(rest, "place_X");
            rest_place_Y = parser.Parse(rest, "place_Y");
            RET_PARAM_NUM = rest_place_Name.size();
            maxLat = Double.parseDouble(ret_place_Y.get(0));
            minLat = Double.parseDouble(ret_place_Y.get(0));
            maxLon = Double.parseDouble(ret_place_X.get(0));
            minLon = Double.parseDouble(ret_place_X.get(0));
            for(int u=0;u<RET_PARAM_NUM; u++)
            {
                Log.d("maggie", rest_place_Name.get(u));
                Log.d("maggie", rest_place_X.get(u));
                Log.d("maggie",rest_place_Y.get(u));
                if (maxLat < Double.parseDouble(rest_place_Y.get(u)))
                {
                    maxLat =  Double.parseDouble(rest_place_Y.get(u));
                }
                if (minLat > Double.parseDouble(rest_place_Y.get(u)))
                {
                    minLat = Double.parseDouble(rest_place_Y.get(u));
                }
                if (maxLon < Double.parseDouble(rest_place_X.get(u)))
                {
                    maxLon =  Double.parseDouble(rest_place_X.get(u));
                }
                if (minLon > Double.parseDouble(rest_place_X.get(u)))
                {
                    minLon = Double.parseDouble(rest_place_X.get(u));
                }
            }
            Log.d("maxLat",String.valueOf(maxLat));
            Log.d("minLat",String.valueOf(minLat));
            Log.d("maxLon",String.valueOf(maxLon));
            Log.d("minLon",String.valueOf(minLon));
            handler.sendEmptyMessage(DOWNLOAD_COMPLETE);
        }
    };

    Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case DOWNLOAD_COMPLETE:
                    Log.d("end", "group4");
                    position = new LatLng(Double.parseDouble(ret_place_Y.get(0)),Double.parseDouble(ret_place_X.get(0)));
                    map.addMarker(new MarkerOptions().position(position).title(ret_place_Name.get(0)).snippet("good place to go."));

                    Log.d("endddd", "gro444");
                    center = new LatLngBounds(new LatLng(minLat, minLon), new LatLng(maxLat, maxLon));

                    for(int u=0;u<RET_PARAM_NUM; u++)
                    {
                        pos = new LatLng(Double.parseDouble(rest_place_Y.get(u)), Double.parseDouble(rest_place_X.get(u)));
                        map.addMarker(new MarkerOptions().position(pos).title(rest_place_Name.get(u)));
                    }
                    //map.animateCamera(CameraUpdateFactory.newLatLngZoom(position,10));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(center.getCenter(), 12));

                    break;
                default:
                    Log.d("Gina","Download Failed");
                    break;
            }
        }
    };

}


