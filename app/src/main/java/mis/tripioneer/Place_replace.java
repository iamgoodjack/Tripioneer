package mis.tripioneer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.sql.StatementEvent;


/**
 * Created by Jenny on 2015/8/7. 行程中景點置換的附近搜尋(配合activity_map 的layout)
 */
public class Place_replace extends FragmentActivity
{
    private  LatLng position;
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
    private static ArrayList<String> rest_place_X = new ArrayList<String>();
    private static ArrayList<String> rest_place_Y = new ArrayList<String>();
    private static ArrayList<String> rest_place_pic = new ArrayList<String>();
    private static ArrayList<String> rest_place_Name = new ArrayList<String>();
    private static ArrayList<String> rest_place_ShortIntro = new ArrayList<String>();
    private static ListView listView;
    private static List<RoadPlanModel> viewModels;
    private static RoadPlanAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Jenny", "uuu");
        setContentView(R.layout.activity_map);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        viewModels = new ArrayList<RoadPlanModel>();
        adapter = new RoadPlanAdapter(this, viewModels);
        listView = (ListView) findViewById(R.id.listView_map);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        Log.d("vvv", "uuu");
        request_place_id_name[0] = "place_id";
        request_place_id_value[0] = "1";
        request_place_id_name[1] = "trip_id";
        request_place_id_value[1] = "1";
        new Thread(get_choose).start();
        //new Thread(get_trip_place).start();
        new Thread(get_Replaced).start();
    }

    Runnable get_choose = new Runnable()
    {
        final String GET_CHOOSE_PLACE = "http://140.115.80.224:8080/group4/get_choose_place.php";
        String ret;
        @Override
        public void run()
        {
            Log.d("Robin,","Robinn");
            ConnectServer connection = new ConnectServer(GET_CHOOSE_PLACE);
            ret = connection.connect(request_place_id_name, request_place_id_value, par_num);
            JsonParser parser = new JsonParser(CASE);
            ret_place_Name = parser.Parse(ret,"place_Name");
            ret_place_X = parser.Parse(ret, "place_X");
            ret_place_Y = parser.Parse(ret, "place_Y");
            Log.d("Jenny",ret_place_Name.get(0)+"\n");
            Log.d("Jenny",ret_place_X.get(0)+"\n");
            Log.d("Jenny",ret_place_Y.get(0)+"\n");

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
            Log.d("marsh", "marsh");
            ConnectServer con = new ConnectServer(GET_REPLACED_PLACE);
            rest = con.connect(request_place_id_name, request_place_id_value, 2);
            JsonParser parser1 = new JsonParser(CASE);
            rest_place_Name = parser1.Parse(rest,"place_Name");
            rest_place_X = parser1.Parse(rest, "place_X");
            rest_place_Y = parser1.Parse(rest, "place_Y");
            rest_place_pic = parser1.Parse(rest,"place_pic");
            rest_place_ShortIntro = parser1.Parse(rest, "place_ShortIntro");
            x = rest_place_Name.size();
            for(int a=0;a<x;a++)
            {
                Log.d("x is ",String.valueOf(x));
                Log.d("Gina",rest_place_Name.get(a)+"\n");
                Log.d("Gina",rest_place_X.get(a)+"\n");
                Log.d("Gina",rest_place_Y.get(a)+"\n");
                Log.d("Gina",rest_place_ShortIntro.get(a)+"\n");
                Log.d("Gina",rest_place_pic.get(a)+"\n");
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
                Log.d("xNumber:", String.valueOf(x));
                MarkerOptions orgin_options = new MarkerOptions();
                orgin_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                for(int b=0;b<x;b++)
                {
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
                position= new LatLng(Double.parseDouble(ret_place_Y.get(0)),Double.parseDouble(ret_place_X.get(0)));
                map.addMarker(new MarkerOptions().position(position).title(ret_place_Name.get(0)));
                //icon(BitmapDescriptorFactory.fromResource(android.R.drawable.)
                // map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
                Log.d("TAG", "get_choose_place_down");
            }
            if (choose_flag && replaced_flag)
            {
                bunds=getBound(rest_place_X, rest_place_Y, ret_place_X, ret_place_Y);
                Log.d("bunds","123456");
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(bunds.getCenter(), 18));
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
}
