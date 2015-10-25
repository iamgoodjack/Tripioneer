package mis.tripioneer;

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
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class place_mimic_googlemap extends AppCompatActivity
{
    private final String TAG="place_mimic_googlemap";
    private static final int PLACE_PARAM_NUM = 1;
    private final static int DOWNLOAD_COMPLETE = 1;
    private SlidingUpPanelLayout slidingLayout;
    private ImageView img;
    private TextView place ;
    private TextView address;
    private TextView hours;
    private TextView intro ;
    private FloatingActionButton button;
    private String[] request_name  = new String[PLACE_PARAM_NUM];
    private String[] request_values = new String[PLACE_PARAM_NUM];
    private static ArrayList<String> ret_place  = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_mimic_googlemap);

        final String placeid ;
        Bundle placedata = this.getIntent().getExtras();
        placeid = placedata.getString("specifyid");
        request_name[0] = "place_ID";
        request_values[0] = placeid;

        button = (FloatingActionButton)findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                intent.setClass(place_mimic_googlemap.this,place_navi.class);
                bundle.putString("place_id", placeid);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        img = (ImageView)findViewById(R.id.imageView);
        place = (TextView)findViewById(R.id.place);
        address = (TextView)findViewById(R.id.addr);
        hours = (TextView)findViewById(R.id.hours);
        intro = (TextView)findViewById(R.id.intro);
        slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingLayout.setPanelSlideListener(onSlideListener());
        slidingLayout.setOnClickListener(onShowListener());
        slidingLayout.setAnchorPoint(0.633f);
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);

        new Thread(getdata).start();
    }

    //TODO: ENTER ON CLICK, NOT TO ENTER COLLAPSED STATE
    private View.OnClickListener onShowListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                Log.d(TAG,"Panelstate ="+slidingLayout.getPanelState());
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
                button.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelExpanded(View view) {
                Log.d(TAG, "panel expand");
                button.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPanelAnchored(View view) {
                Log.d(TAG, "panel anchored");
                button.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelHidden(View view) {
                Log.d(TAG, "panel is Hidden");
            }
        };
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_place_mimic_googlemap, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

                    place.setText(ret_place.get(0));
                    hours.setText(ret_place.get(1));
                    address.setText(ret_place.get(2));
                    intro.setText(ret_place.get(3));

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
}
