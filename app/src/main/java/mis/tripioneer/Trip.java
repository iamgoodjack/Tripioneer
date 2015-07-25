package mis.tripioneer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;




public class Trip extends Activity
{
    private final static int DOWNLOAD_COMPLETE = 1;
    private final String URL_TRIP = "http://140.115.80.224:8080/group4/Android_trip.php";
    private static final String URL_PREFIX_IMAGE = "http://140.115.80.224:8080/group4/image/";
    private final String CASE = "TRIP";
    private static final int TRIP_NUM_PARAM = 1;
    private static final int RET_PARAM_NUM=7 ;
    private String ret;
    private static int num;
    private static String[] ret_place_Pic = new String[num];
    private static String[] ret_place_Name = new String[num];
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
       /* Bundle b = this.getIntent().getExtras();
                int id = b.getInt("id");   //接收id參數
                String ID = Integer.toString(id);*/
        String ID = Integer.toString( 97);
        request_value[0] = ID;
        request_name[0]="trip_ID";

        new Thread(run_Trip).start();
        viewModels = new ArrayList<ViewModel>();
        adapter = new ViewAdapter(this, viewModels);
        listView =(ListView)findViewById(R.id.listViewXD);
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
            switch(msg.what)
            {
                case  DOWNLOAD_COMPLETE :
                    try
                    {
                        Log.d("haha",Integer.toString(num));
                        for(int i=0;i<7;i++) {
                            if ("".equals(ret_place_Pic[i]))
                            {
                                ViewModel row = new ViewModel(ret_place_Name[i],URL_PREFIX_IMAGE + URLEncoder.encode(ret_place_Pic[i],"UTF-8")+".jpg");
                                viewModels.add(row);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    listView.setAdapter(adapter);
                    Log.d("SET_ADAPTER","OK");
                    break;
                default:
                    Log.d("Jeny","Download Failure");

            }
        }
    };


    Runnable run_Trip = new Runnable()
    {
        @Override
        public void run()
        {
            ConnectServer connection = new ConnectServer(URL_TRIP);
            ret = connection.connect(request_name, request_value, 1);
            Log.d("Gina2",ret);
            JsonParser parser = new JsonParser(RET_PARAM_NUM,CASE);
            ret_place_Pic = parser.Parse(ret,"place_Pic");
            ret_place_Name = parser.Parse(ret,"place_Name");
            num = ret_place_Name.length;
            Log.d("XD",Integer.toString(num));

            for(int i=0; i<num;i++)
            {
                Log.d("Jenny",ret_place_Name[i]+"\n");
                Log.d("Gina", ret_place_Pic[i] + "\n");
            }
            handler.sendEmptyMessage(DOWNLOAD_COMPLETE);
            Log.d("uuu", "zzzz");
        }
    };



}
