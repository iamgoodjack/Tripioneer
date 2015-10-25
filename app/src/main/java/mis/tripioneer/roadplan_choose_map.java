package mis.tripioneer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
public class roadplan_choose_map  extends FragmentActivity implements AdapterView.OnItemClickListener
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

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roadplan_choose_map);
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
        Toast.makeText(roadplan_choose_map.this, "123", Toast.LENGTH_LONG).show();

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
        Toast.makeText(roadplan_choose_map.this, mode, Toast.LENGTH_LONG).show();
        getMap(json, position);
        getPlan(json, position);
        drawPoint();
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
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(),15));
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




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
