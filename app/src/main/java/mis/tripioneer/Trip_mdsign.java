package mis.tripioneer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2015/9/4.
 */
public class Trip_mdsign extends AppCompatActivity
{
    private final static int DOWNLOAD_COMPLETE = 1;
    private static final String TAG = "Trip_mdsign";
    private static final int TRIP_NUM_PARAM = 1;
    private String[] request_name = new String[TRIP_NUM_PARAM];
    private String[] request_value = new String[TRIP_NUM_PARAM];
    private static int RET_PARAM_NUM;
    private static ArrayList<String> ret_place_Pic = new ArrayList<String>();
    private static ArrayList<String> ret_place_Name = new ArrayList<String>();
    private static ArrayList<String> ret_place_ID = new ArrayList<String>();
    private static ListView listView;
    private static List<ViewModel> viewModels;
    private static GeneralViewAdapter_tmp adapter;
    private static ImageView imageView;
    private static Context context;
    private FloatingActionButton Navigation;
    private FloatingActionButton Like;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_mdsign);

        context = this;
        imageView = (ImageView) findViewById(R.id.tripPic);
        listView =(ListView)findViewById(R.id.listView);
        Navigation = (FloatingActionButton) findViewById(R.id.fab_navigation);
        Like = (FloatingActionButton) findViewById(R.id.fab_like);
        viewModels = new ArrayList<ViewModel>();
        adapter = new GeneralViewAdapter_tmp(this, viewModels);

        Navigation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setClass(Trip_mdsign.this, main_road.class);
                intent.putStringArrayListExtra("place_id_list", ret_place_ID);
                startActivity(intent);
            }
        });

        Like.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG,"Like button clicked!");
            }
        });


        String ID ;
        Bundle tripdata = this.getIntent().getExtras();
        ID = tripdata.getString("tripid");
        request_value[0] = ID;
        request_name[0]="ID";

        new Thread(run_Trip).start();


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
        if (id == R.id.action_search)
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

                        for(int i=0;i<RET_PARAM_NUM;i++)
                        {
                            if (!"".equals( ret_place_Pic.get(i) ) )
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
                        Picasso.with(context).load(viewModels.get(0).getImageUrl()).into(imageView);//Bind header trip_picture
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    listView.setAdapter(adapter);
                    break;

                default:
                    Log.d(TAG,"Download Failure");
                    break;
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
                Log.d(TAG,ret_place_ID.get(i)+"\n");

            }
            handler.sendEmptyMessage(DOWNLOAD_COMPLETE);

        }
    };
}
