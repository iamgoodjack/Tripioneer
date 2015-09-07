package mis.tripioneer;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;



/**
 * Created by Jenny on 2015/8/18.  路線規劃
 */
public class main_road extends FragmentActivity implements AdapterView.OnItemClickListener
{
    private static GoogleMap map;
    private LatLngBounds bund;
    private static ArrayList<String> text = new ArrayList<String>();
    private static ArrayList<String> sep_distance = new ArrayList<String>();
    private static ArrayList<String> sep_duration = new ArrayList<String>();
    private static ArrayList<String> travel_mode = new ArrayList<String>();
    private String url;
    private int dis=0,dur=0;
    private int par_num;
    private static int x;  //ret_place_Name的大小 我一共要規劃的點個數
    private final static int DOWNLOAD_COMPLETE = 1;
    private static LatLng position[];
    String request_place_id_name[];
    String request_place_id_value[];
    private static ArrayList<String> ret_place_X = new ArrayList<String>();
    private static ArrayList<String> ret_place_Y = new ArrayList<String>();
    private static ArrayList<String> ret_place_Name = new ArrayList<String>();
    private static ImageView from_pic;
    private static ImageView to_pic;
    private static TextView content;
    private static TextView from_text;
    private static TextView to_text;
    private static TextView distance;
    private static TextView time;

    final String GET_PLACE_LATLON = "http://140.115.80.224:8080/group4/get_place_latlon.php";
    ArrayList<String> placelist_from_trip;


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("Jenny", "uuu");
        setContentView(R.layout.activity_roadplan);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.roadplan_map)).getMap();
        from_pic =(ImageView) findViewById(R.id.from_pic);
        to_pic =(ImageView) findViewById(R.id.to_pic);
        from_text =(TextView) findViewById(R.id.from_text);
        to_text =(TextView) findViewById(R.id.to_text);
        distance =(TextView) findViewById(R.id.distance);
        time =(TextView) findViewById(R.id.time);
        content = (TextView) findViewById(R.id.content);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        placelist_from_trip = this.getIntent().getExtras().getStringArrayList("place_id_list");
        par_num = placelist_from_trip.size();
        request_place_id_name = new String[par_num];
        request_place_id_value = new String[par_num];

        Log.d("Gina","size="+par_num);

        //將行程中景點id傳入
       // par_num=3; //景點數量
        //request_place_id_name=new String[par_num];
        //request_place_id_value=new String[par_num];

        for (int u=0;u<par_num;u++)
        {
            request_place_id_name[u] = "place_id"+u;
            request_place_id_value[u] = placelist_from_trip.get(u);
        }
        new Thread(connect_Server).start();
    }

    Runnable connect_Server = new Runnable()
    {
        final String GET_PLACE_LATLON = "http://140.115.80.224:8080/group4/get_place_latlon.php";
        String ret;
        private final String CASE="MAP";
        @Override
        public void run()
        {
            Log.d("Robin,","connect_server");
            ConnectServer connection = new ConnectServer( GET_PLACE_LATLON);
            ret = connection.connect(request_place_id_name, request_place_id_value, request_place_id_value.length);
            JsonParser parser = new JsonParser(CASE);
            ret_place_Name = parser.Parse(ret,"place_Name");
            ret_place_X = parser.Parse(ret, "place_X");
            ret_place_Y = parser.Parse(ret, "place_Y");
            x = ret_place_Name.size();
            position = new LatLng[x];
            for(int a=0;a<x;a++)
            {
                position[a]= new LatLng(Double.parseDouble(ret_place_Y.get(a)),Double.parseDouble(ret_place_X.get(a)));
                Log.d("x is ", String.valueOf(x));
                Log.d("Gina", ret_place_Name.get(a) + "\n");
                Log.d("Gina", ret_place_X.get(a) + "\n");
                Log.d("Gina", ret_place_Y.get(a) + "\n");
            }
            for(int a=0;a<x;a++)
            {
                Log.d("Tag","tag");
                Log.d(String.valueOf(a),String.valueOf(position[a].latitude));
                Log.d(String.valueOf(a),String.valueOf(position[a].longitude));
            }
            handler.sendEmptyMessage(DOWNLOAD_COMPLETE);
        }
    };

    android.os.Handler handler = new android.os.Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case DOWNLOAD_COMPLETE:
                    Log.d("start handler","start handler");
                    MarkerOptions orgin_options = new MarkerOptions();
                    orgin_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    orgin_options.position(position[0]);
                    orgin_options.title(ret_place_Name.get(0)+"是起點");
                    map.addMarker(orgin_options);

                    MarkerOptions des_options = new MarkerOptions();
                    des_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    des_options.position(position[x-1]);
                    des_options.title(ret_place_Name.get(x-1)+"是終點");
                    map.addMarker(des_options);
                    if (x>2)
                    {
                        MarkerOptions waypoint_options = new MarkerOptions();
                        waypoint_options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        for (int d = 1; d < (x - 1); d++)
                        {
                            waypoint_options.position(position[d]);
                            waypoint_options.title(ret_place_Name.get(d));
                            map.addMarker(waypoint_options);
                        }
                    }
                    url = getDirectionsUrl(position);
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                    Log.d("TAG", String.valueOf(text.size()));
                    Log.d("TAG",String.valueOf(sep_duration.size()));
                    Log.d("TAG", String.valueOf(sep_distance.size()));
                    break;
                default:
                    Log.d("down handler","down handler");
                    break;
            }
        }
    };

    private String getDirectionsUrl(LatLng position[])
    {
        Log.d("getDirectionsUrl",String.valueOf(x));
        String parameters;
        String str_origin;
        String str_dest;
        String waypoints;
        String sensor = "sensor=false";

        // Origin of route
        str_origin = "origin="+position[0].latitude+","+position[0].longitude;

        // Destination of route
        str_dest = "destination="+position[x-1].latitude+","+position[x-1].longitude;

        if (x>2)
        {
            // Waypoints
            waypoints = "";
            for (int i = 1; i < x - 1; i++)
            {
                waypoints = "waypoints=";
                waypoints += position[i].latitude + "," + position[i].longitude + "|";
            }
            // Building the parameters to the web service
            parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;
        }else
        {
            parameters = str_origin + "&" + str_dest + "&" + sensor;
        }

        // Building the url to the web service
        String URL = "https://maps.googleapis.com/maps/api/directions/json?"+parameters+"&language=zh-TW";
        Log.d("TAG",URL);
        return URL;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Log.d("TAG_onItemClick","TAG_onItemClick");
        Toast.makeText(this, "編號" + position, Toast.LENGTH_SHORT).show();
    }

    // Fetches data from url passed                       傳入參數, 處理中更新介面參數, 處理後傳出參數
    private class DownloadTask extends AsyncTask<String, Void, String>
    {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url)
        {
            Log.d("DownloadTask in","lili");
            // For storing data from web service
            String data = "";
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e)
            {
                Log.d("Background  exception",e.toString());
            }
            // Executes in UI thread, after the execution of
            // doInBackground()
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result)
        {
            Log.d("after DownloadTask", "lili");
            super.onPostExecute(result);

            JSONObject jObject;
            JSONArray jRoutes;
            String N_lat,N_lng,S_lat,S_lng;

            try {
                jObject = new JSONObject(result);
                jRoutes = jObject.getJSONArray("routes");
                N_lat=jRoutes.getJSONObject(0).getJSONObject("bounds").getJSONObject("northeast").getString("lat");
                N_lng=jRoutes.getJSONObject(0).getJSONObject("bounds").getJSONObject("northeast").getString("lng");
                S_lat=jRoutes.getJSONObject(0).getJSONObject("bounds").getJSONObject("northeast").getString("lat");
                S_lng=jRoutes.getJSONObject(0).getJSONObject("bounds").getJSONObject("northeast").getString("lng");
                bund=new LatLngBounds(new LatLng(Double.parseDouble(N_lat),Double.parseDouble(N_lng)),new LatLng(Double.parseDouble(S_lat),Double.parseDouble(S_lng)));
                Log.d("TAG","TAG");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException
    {
        String data = "";
        InputStream iStream;
        HttpURLConnection urlConnection;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            Log.d("URL connect", "URL connect");
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("Robin", data);
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

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >
    {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                Log.d("TAG","TAG" );
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                RoutesTextJSONParser par = new RoutesTextJSONParser();
                // Starts parsing data
                routes = parser.parse(jObject);
                text = par.parse(jObject,"TEXT");
                sep_distance = par.parse(jObject,"DISTANCE");  //in meters
                sep_duration = par.parse(jObject,"DURATION"); //in seconds
                travel_mode = par.parse(jObject, "TRAVEL_MODE");
                Log.d("TED",String.valueOf(travel_mode.size()));
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            //final String URL_PREFIX_IMAGE = "http://140.115.80.224:8080/group4/travel_mode_pic/";

            // Traversing through all the routes
            for(int i=0;i<result.size();i++)
            {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++)
                {
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.RED);
            }
            // Drawing polyline in the Google Map for the i-th route
            Log.d("TAG", "mapmove_end");
            map.addPolyline(lineOptions);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(bund.getCenter(), 14));
            from_text.setText(ret_place_Name.get(0));
            to_text.setText(ret_place_Name.get(x - 1));
            for(int a=0;a<sep_distance.size();a++)
            {
                int d = Integer.parseInt(sep_distance.get(a));
                int t = Integer.parseInt(sep_duration.get(a));
                dis+=d;
                dur+=t;
            }
            Log.d("TED",convert(dis,"dis"));
            Log.d("TED", convert(dur, "dur"));
            time.setText(convert(dis, "dis"));
            distance.setText(convert(dur, "dur"));
            Log.d("text_size", String.valueOf(text.size()));
            String h="";
            for (int i=0;i<text.size();i++)
            {
                Log.d("Mag","Mag");
                switch (travel_mode.get(i))
                {
                    case "DRIVING":
                        h+=text.get(i)+"   &"+travel_mode.get(i)+"\n";
                        break;
                    case "WALKING":
                        h+=text.get(i)+"   &"+travel_mode.get(i)+"\n";
                        break;
                    case "BICYCLING":
                        h+=text.get(i)+"   &"+travel_mode.get(i)+"\n";
                        break;
                    default:
                        break;
                }
            }
            content.setText(h);
        }
    }

    private String convert(int num,String match)
    {
        int n=num;
        String par="";
        int hr;
        int min;
        int kilo;
        switch(match)
        {
            case "dur":
                hr=n/3600;
                n %=3600;
                min=n/60;
                n %= 60;
                if (hr!=0)
                {
                    par= par+hr+"小時";
                }
                if (min!=0)
                {
                    par=par+min+"分";
                }
                if (n!=0)
                {
                    par=par+n+"秒";
                }
                break;
            case "dis":
                kilo=n/1000;
                n%=1000;
                if (kilo!=0)
                {
                    par= par+kilo+"公里";
                }
                if (n!=0)
                {
                    par=par+n+"公尺";
                }

                break;
            default:
                break;
        }
        return par;
    }

    protected void onResume()
    {
        super.onResume();

    }
       /* private ArrayList<LatLng> decodePolylines(String poly)
        {
            Log.d("decodePolylines","decodePolylines");
            ArrayList<LatLng> pl = new ArrayList<LatLng>();
            int len = poly.length();
            int index = 0;
            int lat = 0;
            int lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = poly.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = poly.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;
                LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
                pl.add(p);
            }
            return pl;
        }*/

}
