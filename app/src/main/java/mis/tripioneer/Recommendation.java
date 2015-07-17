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




public class Recommendation extends Activity
{
    private final static int DOWNLOAD_COMPLETE = 1;
    private final String URL_RECOMMENDATION = "http://140.115.80.224:8080/Recommendation.php";
    private static final String URL_PREFIX_IMAGE = "http://140.115.80.224:8080/group4/image/";
    private final String CASE = "RECOMMENDATION";
    private static final int RECOMMENDATION_NUM_PARAM = 1;
    private static final int RET_PARAM_NUM = 20;
    private String ret;
    private static String[] ret_place_Pic = new String[RET_PARAM_NUM];
    private static String[] ret_place_Name = new String[RET_PARAM_NUM];
    private String[] request_name = new String[RECOMMENDATION_NUM_PARAM];
    private String[] request_value = new String[RECOMMENDATION_NUM_PARAM];
    private static ListView listView;
    private static List<ViewModel> viewModels;
    private static ViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        new Thread(run_Recommendation).start();
        viewModels = new ArrayList<ViewModel>();
        adapter = new ViewAdapter(this, viewModels);
        listView =(ListView)findViewById(R.id.list);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recommendation, menu);
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
            switch (msg.what)
            {
                case DOWNLOAD_COMPLETE:
                    Log.d("Gina","Enter download complete");

                    try
                    {
                        for(int i=0; i<RET_PARAM_NUM;i++)
                        {
                            if(!"".equals(ret_place_Pic[i]) )
                            {
                                ViewModel row = new ViewModel(ret_place_Name[i], URL_PREFIX_IMAGE
                                        + URLEncoder.encode(ret_place_Pic[i], "UTF-8") + ".jpg");
                                viewModels.add(row);
                            }
                        }
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }

                    listView.setAdapter(adapter);
                    break;

                default:
                    Log.d("Gina","Download Failed");
                    break;
            }
        }
    };

    Runnable run_Recommendation = new Runnable()
    {
        @Override
        public void run()
        {

            ConnectServer connection = new ConnectServer(URL_RECOMMENDATION);
            ret = connection.connect(request_name, request_value, 0);

            JsonParser parser = new JsonParser(RET_PARAM_NUM,CASE);
            ret_place_Pic = parser.Parse(ret,"place_Pic");
            ret_place_Name = parser.Parse(ret,"place_Name");

            for(int i=0; i<20;i++)
            {
                Log.d("Gina",ret_place_Name[i]+"\n");
                Log.d("Gina", ret_place_Pic[i] + "\n");
            }

            handler.sendEmptyMessage(DOWNLOAD_COMPLETE);

        }
    };
}
