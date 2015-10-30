package mis.tripioneer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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

/**
 * Created by Jenny on 2015/9/1.
 */
public class roadplan_choose_mode extends FragmentActivity implements AdapterView.OnItemClickListener
{
    private static  String mode="";
    private static String json_String;
    private String url;
    private int par_num;
    private String request_place_id_name[];
    private String request_place_id_value[];
    private static ArrayList<String> summary = new ArrayList<String>();
    private static ArrayList<String> sep_distance = new ArrayList<String>();
    private static ArrayList<String> sep_duration = new ArrayList<String>();
    private static ArrayList<String> ret_place_X = new ArrayList<String>();
    private static ArrayList<String> ret_place_Y = new ArrayList<String>();
    private static ArrayList<String> ret_place_id = new ArrayList<String>();
    private static ArrayList<String> ret_place_Name = new ArrayList<String>();
    private static ArrayList<String> placelist_from_trip = new ArrayList<String>();
    private static TextView orgin;
    private static TextView waypoints;
    private static TextView destination;
    private static ListView routes_listview;
    private static ImageButton driving_mode;
    private static ImageButton walking_mode;
    private SimpleAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roadplan_choose_mode);
        driving_mode = (ImageButton) findViewById(R.id.driving_mode);
        walking_mode = (ImageButton) findViewById(R.id.walking_mode);
        orgin =(TextView) findViewById(R.id.origin);
        waypoints =(TextView) findViewById(R.id.waypoints);
        destination =(TextView) findViewById(R.id.destination);
        routes_listview = (ListView) findViewById(R.id.routes_listview);
        routes_listview.setOnItemClickListener(this);

        placelist_from_trip = this.getIntent().getExtras().getStringArrayList("place_id_list");
        par_num = placelist_from_trip.size();
        request_place_id_name = new String[par_num];
        request_place_id_value = new String[par_num];

        for (int a = 0; a < par_num; a++)
        {

             request_place_id_name[a]="place_id"+String.valueOf(a);
             request_place_id_value[a]=placelist_from_trip.get(a);
            Log.d("Mandy_CC",placelist_from_trip.get(a));
        }
        ret_place_X.clear();
        ret_place_Y.clear();
        ret_place_Name.clear();
        ret_place_id.clear();
        getCheck();
        GetLatLng getlatlng = new GetLatLng();
        getlatlng.execute();
        driving_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = "mode=driving";
                Log.d("TAG", "driving onclick=" + mode);
                url=getDirectionsUrl(mode);
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
            }
        });

        walking_mode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mode="mode=walking";
                Log.d("TAG","walking onclick="+mode);
                url=getDirectionsUrl(mode);
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
            }
        });

    }

    private void getCheck()
    {
        for (int a=0;a<request_place_id_name.length;a++)
        {
            //Log.d("Mandy",request_place_id_name[a]);
            //Log.d("Mandy",request_place_id_value[a]);
        }
    }

    private class GetLatLng extends AsyncTask<String, Void, String>
    {
        final String GET_PLACE_LATLON = "http://140.115.80.224:8080/group4/get_place_latlon.php";
        String ret;
        private final String CASE="MAP";
        @Override
        protected String doInBackground(String... params)
        {
            Log.d("TAG,","GetLatLng");
            ConnectServer connection = new ConnectServer(GET_PLACE_LATLON);
            ret = connection.connect(request_place_id_name, request_place_id_value,request_place_id_value.length);
            JsonParser parser = new JsonParser(CASE);
            ret_place_Name = parser.Parse(ret,"place_Name");
            ret_place_X = parser.Parse(ret, "place_X");
            ret_place_Y = parser.Parse(ret, "place_Y");
            ret_place_id=parser.Parse(ret,"place_ID");
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            for(int i=0;i<ret_place_id.size();i++) {
                String tmp,temp_X,temp_Y,temp_Name;
                for (int j = 0; j < ret_place_id.size(); j++) {
                    if (ret_place_id.get(j).equals(placelist_from_trip.get(i))) {
                        Log.d("Mandy","i="+i+"; j="+j+";");
                        tmp = ret_place_id.get(j);
                        temp_X=ret_place_X.get(j);
                        temp_Y = ret_place_Y.get(j);
                        temp_Name = ret_place_Name.get(j);
                        Log.d("Mandy","tmp="+tmp);
                        ret_place_id.set(j, ret_place_id.get(i));
                        ret_place_X.set(j, ret_place_id.get(i));
                        ret_place_Y.set(j, ret_place_Y.get(i));
                        ret_place_Name.set(j, ret_place_Name.get(i));
                        Log.d("Mandy", "ret_place_id=" + ret_place_id.get(j) + "=?" + ret_place_id.get(i));
                        ret_place_id.set(i, tmp);
                        ret_place_X.set(i, temp_X);
                        ret_place_Y.set(i, temp_Y);
                        ret_place_Name.set(i, temp_Name);
                        Log.d("Mandy", "ret_place_id=" + ret_place_id.get(i) + "=?" + "tmp="+tmp);
                    }
                }
            }
            for (int j=0;j<ret_place_id.size();j++)
            {
                Log.d("Mandy_m",ret_place_id.get(j));
                Log.d("Mandy_m",ret_place_Name.get(j));
            }

            mode="mode=driving";
            orgin.setText("orgin:" + ret_place_Name.get(0));
            Log.d("TAG", "show orgin");
            destination.setText("destination:" + ret_place_Name.get((ret_place_Name.size() - 1)));
            if (ret_place_Name.size()>2)
            {
                String pa="";
                for(int a=1;a<(ret_place_Name.size()-1);a++)
                {
                    pa+=ret_place_Name.get(a)+",";
                }
                waypoints.setText("waypoints:"+pa);
            }
            url=getDirectionsUrl(mode);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        }
    }

    private String getDirectionsUrl(String mode)
    {
        for (int a=0;a<ret_place_X.size();a++)
        {
            Log.d("TAG,","ret_place_name"+ret_place_Name.get(a));
            Log.d("TAG,","ret_place_x"+ret_place_X.get(a));
            Log.d("TAG,","ret_place_y"+ret_place_Y.get(a));
        }
        Log.d("TAG","getDirectionsUrl");
        String parameters="";
        String URL="";
        String str_origin;
        String str_dest;
        String str_waypoints ="waypoints=optimize:true|";
        String sensor = "sensor=false";
        str_origin = "origin="+ret_place_Y.get(0)+","+ret_place_X.get(0);
        if (ret_place_X.size()>=2)
        {
            str_dest = "destination="+ret_place_Y.get((ret_place_Y.size()-1))+","+ret_place_X.get((ret_place_X.size()-1));
            if(ret_place_X.size()!=2)
            {
                for (int i = 1; i < ret_place_X.size() - 1; i++)
                {
                    str_waypoints += ret_place_Y.get(i) + "," + ret_place_X.get(i) + "|";
                }

                parameters = str_origin + "&" + str_dest + "&" + sensor + "&" +str_waypoints+"&"+mode;
            }else
            {
                parameters = str_origin + "&" + str_dest + "&" + sensor + "&" +"&"+mode;
            }
            URL = "http://maps.googleapis.com/maps/api/directions/json?"+parameters+"&language=zh-TW&alternatives=true";
        }else
        {
            Toast.makeText(roadplan_choose_mode.this,"行程中的景點數量小於1",Toast.LENGTH_SHORT).show();
        }
        Log.d("TAG",URL);
        return URL;
    }

    private class DownloadTask extends AsyncTask<String, Void, String>
    {
        String data="";
        InputStream iStream;
        HttpURLConnection urlConnection=null;
        URL Url;
        @Override
        protected String doInBackground(String... params)
        {
            Log.d("TAG","URL: "+params[0]);
            try {
                Url = new URL(params[0]);
                urlConnection = (HttpURLConnection) Url.openConnection();
                Log.d("TAG", "URL connect");
                urlConnection.connect();

                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null)
                {
                    sb.append(line);
                }
                data = sb.toString();
                Log.d("TAG", "DATA: "+data);
                br.close();
                iStream.close();
                urlConnection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("Exception download url", e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("IOException", e.toString());
            }catch (Exception e){
                e.printStackTrace();
                Log.d("Exception ", e.toString());
            }
            finally {
                return data;
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
            JSONObject jObject;
            Log.d("TAG", "onPostExcute: " + result);
            json_String=result;
            switch (mode)
            {
                case "mode=driving":
                    Log.d("TAG", "switch mode=" + mode);
                    try{
                        jObject = new JSONObject(result);
                        RoutesTextJSONParser par = new RoutesTextJSONParser();
                        summary = par.parse(jObject,"SUMMARY");
                        sep_distance = par.parse(jObject,"DISTANCE");  //in meters
                        sep_duration = par.parse(jObject,"DURATION"); //in seconds
                        Log.d("TAG","Size of summary"+String.valueOf(summary.size()));
                        Log.d("TAG","Size of sep_dis"+String.valueOf(sep_distance.size()));
                        Log.d("TAG","Size of sep_dur"+String.valueOf(sep_duration.size()));
                        for (int k=0;k<sep_distance.size();k++)
                        {
                            Log.d("TAG","ll Size of summary"+String.valueOf(summary.size()));
                            Log.d("TAG","ll Size of sep_dis"+String.valueOf(sep_distance.size()));
                            Log.d("TAG","ll Size of sep_dur"+String.valueOf(sep_duration.size()));
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    for(int i=0; i<summary.size(); i++){
                        HashMap<String,String> item = new HashMap<String,String>();
                        Log.d("TAG","summary"+summary.get(i));
                        Log.d("TAG","sep_dis"+ sep_distance.get(i));
                        Log.d("TAG","sep_dur"+sep_duration.get(i));
                        item.put( "summary", summary.get(i));
                        item.put( "disdur",sep_distance.get(i)+"/"+sep_duration.get(i) );
                        list.add( item );
                    }
                   adapter=new SimpleAdapter( roadplan_choose_mode.this ,list, android.R.layout.simple_list_item_2,
                           new String[] { "summary","disdur" },
                           new int[] { android.R.id.text1, android.R.id.text2 } );
                    routes_listview.setAdapter(adapter);
                    break;

                case "mode=walking":
                    Log.d("TAG","switch mode="+mode);
                    try{
                        jObject = new JSONObject(result);
                        RoutesTextJSONParser par = new RoutesTextJSONParser();
                        summary = par.parse(jObject,"SUMMARY");
                        sep_distance = par.parse(jObject,"DISTANCE");  //in meters
                        sep_duration = par.parse(jObject,"DURATION"); //in seconds
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    for(int i=0; i<summary.size(); i++){
                        HashMap<String,String> item = new HashMap<String,String>();
                        item.put( "summary", summary.get(i));
                        item.put( "disdur",sep_distance.get(i)+"/"+sep_duration.get(i) );
                        list.add( item );
                    }
                    adapter=new SimpleAdapter( roadplan_choose_mode.this ,list, android.R.layout.simple_list_item_2,
                            new String[] { "summary","disdur" },
                            new int[] { android.R.id.text1, android.R.id.text2 } );
                    routes_listview.setAdapter(adapter);
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Toast.makeText(roadplan_choose_mode.this, "您選的是第"+position+"個路線", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(roadplan_choose_mode.this, roadplan_choose_map.class);
        Bundle bundle = new Bundle();
        bundle.putString("json", json_String);
        bundle.putString("mode", mode);
        bundle.putInt("position", position);
        bundle.putString("summary",summary.get(position));
        bundle.putString("disdur",sep_distance.get(position)+"/"+sep_duration.get(position));
        intent.putStringArrayListExtra("place_X", ret_place_X);
        intent.putStringArrayListExtra("place_Y", ret_place_Y);
        intent.putStringArrayListExtra("place_Name", ret_place_Name);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
