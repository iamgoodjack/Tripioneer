package mis.tripioneer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class Place_mdsign extends AppCompatActivity
{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ImageView img;
    private String[] TITLES = new String[3];
    private int[] ICONS = {R.drawable.ic_channel_icons,R.drawable.ic_channel_icons,R.drawable.ic_channel_icons,R.drawable.ic_channel_icons};
    private SlidingUpPanelLayout slidingLayout;
    private final String TAG="Place_mdsign";
    private static final int PLACE_PARAM_NUM = 1;
    private final static int DOWNLOAD_COMPLETE = 1;
    private TextView intro;
    private String[] request_name  = new String[PLACE_PARAM_NUM];
    private String[] request_values = new String[PLACE_PARAM_NUM];
    private static ArrayList<String> ret_place  = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        String placeid ;
        Bundle placedata = this.getIntent().getExtras();
        placeid = placedata.getString("specifyid");

        request_name[0] = "place_ID";
        request_values[0] = placeid;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        img = (ImageView)findViewById(R.id.imageView);
        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        intro = (TextView)findViewById(R.id.intro);

        slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingLayout.setPanelSlideListener(onSlideListener());

        new Thread(getdata).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Handler handler =new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            final String URL_PREFIX_IMAGE = "http://140.115.80.224:8080/group4/tainan_pic/";
            super.handleMessage(msg);
            switch(msg.what)
            {
                case DOWNLOAD_COMPLETE:

                    TITLES[0] = (ret_place.get(0));
                    Log.d(TAG,"title="+TITLES[0]);
                    TITLES[1] = (ret_place.get(1));
                    Log.d(TAG,"title="+TITLES[1]);
                    TITLES[2] = (ret_place.get(2));
                    Log.d(TAG,"title="+TITLES[2]);
                    intro.setText(ret_place.get(3));
                    mAdapter = new testAdapter(TITLES,ICONS);
                    mRecyclerView.setAdapter(mAdapter);

                try
                {
                    Picasso.with(getApplicationContext())
                            .load(URL_PREFIX_IMAGE
                                    + URLEncoder.encode(ret_place.get(0), "UTF-8") + ".jpg")
                            .into(img);
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                break;
                default:
                    Log.d(TAG,"Download Failed");
                    break;
            }
        }
    };

    Runnable getdata = new Runnable()
    {
        private String ret ;
        private final String CASE = "PLACE";
        private final String URL_PLACEGRAB = "http://140.115.80.224:8080/Place_Grab.php";
        private int RET_PARAM_NUM;
        @Override
        public void run()
        {
            ConnectServer conncetion = new ConnectServer(URL_PLACEGRAB);
            ret = conncetion.connect(request_name, request_values, PLACE_PARAM_NUM);
            JsonParser parser = new JsonParser(CASE);
            ret_place = parser.Parse(ret,"");
            RET_PARAM_NUM = ret_place.size();

            for(int i=0; i< RET_PARAM_NUM;i++)
                Log.d(TAG, ret_place.get(i));
            handler.sendEmptyMessage(DOWNLOAD_COMPLETE);
        }
    };

    private View.OnClickListener onShowListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //show sliding layout in bottom of screen (not expand it)
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        };
    }

    private SlidingUpPanelLayout.PanelSlideListener onSlideListener()
    {
        return new SlidingUpPanelLayout.PanelSlideListener()
        {
            @Override
            public void onPanelSlide(View view, float v) {
                Log.d(TAG, "panel is sliding");
            }

            @Override
            public void onPanelCollapsed(View view) {
                Log.d(TAG, "panel Collapse");
            }

            @Override
            public void onPanelExpanded(View view) {
                Log.d(TAG, "panel expand");
            }

            @Override
            public void onPanelAnchored(View view) {
                Log.d(TAG, "panel anchored");
            }

            @Override
            public void onPanelHidden(View view) {
                Log.d(TAG, "panel is Hidden");
            }
        };
    }
}
