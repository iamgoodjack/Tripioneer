package mis.tripioneer;


import android.content.Intent;
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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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

//peter挖貴小舖中的導航(導航自己位置到蛙貴小舖<該景點>的路線)
public class Place_navigation extends FragmentActivity
{
    private LocationManager locationMgr;  //定位提供者的系統管理員
    private static double lat,lng;
    private int dis,dur;
    private boolean Place_Latln_Get=false,Location_Get=false;
    final int par_num=1;
    final int DOWNLOAD_COMPLETE=1;
    final int LOCATION_GET=2;
    final String CASE ="PLACE_NAV";
    private TextView from_text,to_text,time,distance,content;
    String[] request_name = new String[par_num];
    String[] request_value = new String[par_num];
    private static ArrayList<String> ret_place_X = new ArrayList<String>();
    private static ArrayList<String> ret_place_Y = new ArrayList<String>();
    private static ArrayList<String> ret_place_Name = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_navigation);
        from_text = (TextView) findViewById(R.id.from_text);
        to_text = (TextView) findViewById(R.id.to_text);
        time = (TextView) findViewById(R.id.time);
        distance = (TextView) findViewById(R.id.distance);
        content = (TextView)findViewById(R.id.content);
        locationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        Log.d("uuu","uuu");

        //接收來自景點的ID
        Bundle data = this.getIntent().getExtras();
        request_value[0] = data.getString("place_id");
        request_name[0]="place_id";

        initProvider();
        new Thread(connect_Server).start();
    }

    public void initProvider()
    {
        int minTime = 1000;//ms
        int minDist = 500;//meter
        Log.d("uuu", "ppp");
        if(!locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Toast.makeText(Place_navigation.this, "請開啟GPS定位功能並重啟此APP", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        if(locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,minTime,minDist,locationListener);
            locationMgr.addGpsStatusListener(gpsListener);
            locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, locationListener);
            Toast.makeText(Place_navigation.this, "provider:gps", Toast.LENGTH_SHORT).show();
        }
        else if (locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            Toast.makeText(Place_navigation.this, "provider:network", Toast.LENGTH_SHORT).show();
            locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, locationListener);
        }else
        {
            Toast.makeText(Place_navigation.this, "請開啟定位功能:", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    Runnable connect_Server = new Runnable()
    {
        final String GET_CHOOSE_PLACE = "http://140.115.80.224:8080/group4/get_choose_place.php";
        String ret;
        public void run ()
        {
            Log.d("Robin,","Robin");
            ConnectServer connect = new ConnectServer(GET_CHOOSE_PLACE);
            ret = connect.connect(request_name, request_value, par_num);
            JsonParser parser = new JsonParser(CASE);
            ret_place_Name = parser.Parse(ret,"place_Name");
            ret_place_X = parser.Parse(ret, "place_X");
            ret_place_Y = parser.Parse(ret, "place_Y");
            Log.d("Robin,",ret_place_Name.get(0));
            Log.d("Robin,",ret_place_X.get(0));
            Log.d("Robin,",ret_place_Y.get(0));
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
                    break;
                case LOCATION_GET:
                    Location_Get=true;
                    break;
            }
            if (Place_Latln_Get && Location_Get)
            {
                if (String.valueOf(lat)!=null && String.valueOf(lng)!=null)
                {
                    Log.d("got where you are","uuu");
                    from_text.setText("現在位置");
                    to_text.setText(ret_place_Name.get(0));
                    url = getDirectionsUrl();
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                }
            }

        }
    };

    private String getDirectionsUrl()
    {
        Log.d("TAG","getDirectionsUrl");
        String parameters;
        String str_origin;
        String str_dest;
        String sensor = "sensor=false";

        str_origin = "origin="+lat+","+lng;
        str_dest = "destination="+ret_place_Y.get(0)+","+ret_place_X.get(0);
        parameters = str_origin + "&" + str_dest + "&" + sensor;
        String URL = "https://maps.googleapis.com/maps/api/directions/json?"+parameters+"&language=zh-TW";
        Log.d("TAG",URL);
        return URL;
    }

    private class DownloadTask extends AsyncTask<String, Void, String>
    {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url)
        {
            Log.d("DownloadTask in","lili");
            String data = "";
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e)
            {
                Log.d("Background  exception",e.toString());
            }
            return data;
        }
        // Executes in UI thread, after the execution of  doInBackground()
        @Override
        protected void onPostExecute(String result)
        {
            JSONObject jObject;
             ArrayList<String> text = new ArrayList<String>();
             ArrayList<String> dist;
             ArrayList<String> duration;
             ArrayList<String> travel_mode;
            super.onPostExecute(result);
            Log.d("after DownloadTask", "lili");
            try {
                jObject = new JSONObject(result);
                RoutesTextJSONParser par = new RoutesTextJSONParser();
                text = par.parse(jObject,"TEXT");
                dist = par.parse(jObject,"DISTANCE");
                duration = par.parse(jObject,"DURATION");
                travel_mode = par.parse(jObject,"TRAVEL_MODE");
                for(int a=0;a<dist.size();a++)
                {
                    int d = Integer.parseInt(dist.get(a));
                    int t = Integer.parseInt(duration.get(a));
                    dis+=d;
                    dur+=t;
                }
                time.setText(convert(dur, "dur"));
                distance.setText(convert(dis,"dis"));
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
            } catch (JSONException e) {
                e.printStackTrace();
            }


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

    private String downloadUrl(String strUrl) throws IOException
    {
        String data = "";
        InputStream iStream;
        HttpURLConnection urlConnection;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            Log.d("URL connect", "URL connect");

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

    protected void onPause() {
        locationMgr.removeUpdates(locationListener);
        locationMgr.removeGpsStatusListener(gpsListener);
        super.onPause();
    }

    protected void onResume()
    {
        super.onResume();
        initProvider();
        Log.d("uuu", "onResume");
    }



    private void updateWithNewLocation(Location location)
    {
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
        Log.d("latlng", where);
        if ("".equals(content.getText()))
        {
            handler.sendEmptyMessage(LOCATION_GET);
        }

    }

    LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            updateWithNewLocation(location);
            Log.d("TAG", "onLocationChanged");
            Toast.makeText(Place_navigation.this, "onLocationChanged", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("TAG", "onProviderDisabled");
            Toast.makeText(Place_navigation.this, "定位提供者關閉，請重啟", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("TAG","onProviderEnabled");
            Toast.makeText(Place_navigation.this, "onProviderEnabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("Jenny", "Status Changed: Out of Service");
                    Toast.makeText(Place_navigation.this, "Status Changed: Out of Service", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("Jenny", "Status Changed: Temporarily Unavailable");
                    Toast.makeText(Place_navigation.this, "Status Changed: Temporarily Unavailable", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.AVAILABLE:
                    Log.d("Jenny", "Status Changed: Available");
                    Toast.makeText(Place_navigation.this, "Status Changed: Available", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Place_navigation.this, "GPS_EVENT_STARTED", Toast.LENGTH_SHORT).show();
                        break;
                    case GpsStatus.GPS_EVENT_STOPPED:
                        Log.d("penny", "GPS_EVENT_STOPPED");
                        Toast.makeText(Place_navigation.this, "GPS_EVENT_STOPPED", Toast.LENGTH_SHORT).show();
                        break;
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        Log.d("penny", "GPS_EVENT_FIRST_FIX");
                        Toast.makeText(Place_navigation.this, "GPS_EVENT_FIRST_FIX", Toast.LENGTH_SHORT).show();
                        break;
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        Log.d("penny", "GPS_EVENT_SATELLITE_STATUS");
                        break;
                }
            }
        };

    }