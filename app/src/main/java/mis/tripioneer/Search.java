package mis.tripioneer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by Jenny on 2015/8/14.
 */
public class Search extends FragmentActivity {
    private GoogleMap map;
    private  LatLng position;
    final String CASE = "MAP";
    private final int par_num = 1;
    private static int x;
    private LatLngBounds center,bunds;
    String request_place_id_name[] = new String[par_num];
    String request_place_id_value[] = new String[par_num];
    private static ArrayList<String> ret_place_X = new ArrayList<String>();
    private static ArrayList<String> ret_place_Y = new ArrayList<String>();
    private static ArrayList<String> ret_place_Name = new ArrayList<String>();
    private static ArrayList<String> rett_place_X = new ArrayList<String>();
    private static ArrayList<String> rett_place_Y = new ArrayList<String>();
    private static ArrayList<String> rett_place_Name = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Jenny", "uuu");
        setContentView(R.layout.activity_nearsearch);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.search_map)).getMap();
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        request_place_id_name[0] = "place_id";
        request_place_id_value[0] = "20";
        new Thread(connect_Server).start();
        new Thread(get_Choose).start();
        Log.d("vvv", "uuu");
    }

    Runnable connect_Server = new Runnable() {
        final String CONNECT_SERVER = "http://140.115.80.224:8080/group4/near_search.php";
        String ret;

        @Override
        public void run() {
            ConnectServer connection = new ConnectServer(CONNECT_SERVER);
            ret = connection.connect(request_place_id_name, request_place_id_value, par_num);
            JsonParser parser = new JsonParser(CASE);
            ret_place_Name = parser.Parse(ret, "place_Name");
            ret_place_X = parser.Parse(ret, "place_X");
            ret_place_Y = parser.Parse(ret, "place_Y");
            x = ret_place_Name.size();
            Log.d("x is ", String.valueOf(x));
            for (int a = 0; a < x; a++) {
                Log.d("Gina", ret_place_Name.get(a) + "\n");
                Log.d("Gina", ret_place_X.get(a) + "\n");
                Log.d("Gina", ret_place_Y.get(a) + "\n");
            }
            Message message;
            String obj="connect_server";
            message = handler.obtainMessage(1,obj);
            handler.sendMessage(message);
        }
    };

    Runnable get_Choose = new Runnable()
    {
        final String GET_CHOOSE_PLACE = "http://140.115.80.224:8080/group4/get_choose_place.php";
        String rett;
        @Override
        public void run()
        {
            Log.d("Robin,","Robinn");
            ConnectServer conn = new ConnectServer(GET_CHOOSE_PLACE);
            rett = conn.connect(request_place_id_name, request_place_id_value, par_num);
            JsonParser p = new JsonParser(CASE);
            rett_place_Name = p.Parse(rett,"place_Name");
            rett_place_X = p.Parse(rett, "place_X");
            rett_place_Y = p.Parse(rett, "place_Y");
            Log.d("Jenny",rett_place_Name.get(0)+"\n");
            Log.d("Jenny",rett_place_X.get(0)+"\n");
            Log.d("Jenny",rett_place_Y.get(0)+"\n");
            Message message;
            String obj="get_choose_place";
            message = handler.obtainMessage(1,obj);
            handler.sendMessage(message);
        }
    };



     Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            double max_x;
            double min_x;
            double max_y;
            double min_y;
            super.handleMessage(msg);
            String MsgString = (String)msg.obj;
            if (MsgString.equals("connect_server"))
            {
                MarkerOptions orgin_options = new MarkerOptions();
                orgin_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                for(int b=0;b<x;b++)
                {
                    position= new LatLng(Double.parseDouble(ret_place_Y.get(b)),Double.parseDouble(ret_place_X.get(b)));
                    orgin_options.position(position);
                    orgin_options.title(ret_place_Name.get(b));
                    map.addMarker(orgin_options);
                }
            }
            else if (MsgString.equals("get_choose_place"))
            {
                position= new LatLng(Double.parseDouble(rett_place_Y.get(0)),Double.parseDouble(rett_place_X.get(0)));
                map.addMarker(new MarkerOptions().position(position).title(rett_place_Name.get(0)));
            }
            bunds=getBound(ret_place_X, ret_place_Y, rett_place_X, rett_place_Y);
            Log.d("bunds","123456");
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(bunds.getCenter(), 16));
            Log.d("down","Tag");
        }
    };

    private LatLngBounds getBound(ArrayList<String> ret_place_X, ArrayList<String> ret_place_Y, ArrayList<String> rett_place_X, ArrayList<String> rett_place_Y)
    {
        ret_place_X.add(rett_place_X.get(0));
        ret_place_Y.add(rett_place_Y.get(0));
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
        Log.d("minX",String.valueOf(min_X));
        Log.d("maxX",String.valueOf(max_X));
        Log.d("minY",String.valueOf(min_Y));
        Log.d("maxY",String.valueOf(min_Y));
        center = new LatLngBounds(new LatLng(min_Y, min_X), new LatLng(max_Y, max_X));
        return center;
        }

}
