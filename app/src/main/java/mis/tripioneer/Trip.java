package mis.tripioneer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;




public class Trip extends Activity
{
    private final static int DOWNLOAD_COMPLETE = 1;
    private static final String TAG ="Trip";
    private static final int TRIP_NUM_PARAM = 1;
    private static int RET_PARAM_NUM;
    private static ArrayList<String> ret_place_Pic = new ArrayList<String>();
    private static ArrayList<String> ret_place_Name = new ArrayList<String>();
    private static ArrayList<String> ret_place_ID = new ArrayList<String>();
    private String[] request_name = new String[TRIP_NUM_PARAM];
    private String[] request_value = new String[TRIP_NUM_PARAM];
    private static ListView listView;
    private static List<ViewModel> viewModels;
    private static ViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tripioneer_tour_listview);

        //bundle參考資料http://tomkuo139.blogspot.tw/2010/01/android-activity-bundle-activity.html
        //http://tomkuo139.blogspot.tw/2010/01/android-activity-bundle-activity.html

        String ID ;
        Bundle tripdata = this.getIntent().getExtras();
        ID = tripdata.getString("tripid");
        request_value[0] = ID;
        request_name[0]="ID";
        Log.d(TAG,"ID in Trip="+ID);


        new Thread(run_Trip).start();
        viewModels = new ArrayList<ViewModel>();
        adapter = new ViewAdapter(this, viewModels);
        listView =(ListView)findViewById(R.id.listView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            final String URL_PREFIX_IMAGE = "http://140.115.80.224:8080/group4/tainan_pic/";
            switch(msg.what)
            {
                case  DOWNLOAD_COMPLETE :
                    try
                    {

                        for(int i=0;i<RET_PARAM_NUM;i++) {
                            if ("".equals( ret_place_Pic.get(i) ) )
                            {
                                ViewModel row = new ViewModel
                                        (
                                                ret_place_Name.get(i),
                                                URL_PREFIX_IMAGE + URLEncoder.encode(ret_place_Pic.get(i), "UTF-8") + ".jpg",
                                                ""
                                        );
                                viewModels.add(row);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    listView.setAdapter(adapter);

                    break;
                default:
                    Log.d(TAG,"Download Failure");

            }
        }
    };


    Runnable run_Trip = new Runnable()
    {
        @Override
        public void run()
        {
            final String URL_TRIP = "http://140.115.80.224:8080/group4/Android_trip.php";
            final String CASE = "TRIP";
            String ret;

            ConnectServer connection = new ConnectServer(URL_TRIP);
            ret = connection.connect(request_name, request_value, 1);

            JsonParser parser = new JsonParser(CASE);
            ret_place_Pic = parser.Parse(ret, "place_Pic");
            ret_place_Name = parser.Parse(ret, "place_Name");
            ret_place_ID = parser.Parse(ret, "place_ID");
            RET_PARAM_NUM = ret_place_Name.size();


            for(int i=0; i<RET_PARAM_NUM;i++)
            {

                Log.d(TAG,ret_place_Name.get(i)+"\n");
                Log.d(TAG, ret_place_Pic.get(i) + "\n");

                Log.d("Jenny",ret_place_Name.get(i)+"\n");
                Log.d("Gina", ret_place_Pic.get(i) + "\n");
                Log.d("Ted",ret_place_ID.get(i)+"\n");

            }
            handler.sendEmptyMessage(DOWNLOAD_COMPLETE);

        }
    };

}
