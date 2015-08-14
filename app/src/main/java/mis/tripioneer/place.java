package mis.tripioneer;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class place extends ActionBarActivity {

    private final static int DOWNLOAD_COMPLETE = 1;
    private static final int PLACE_PARAM_NUM = 1;
    private static final String TAG = "PLACE";
    private String[] request_name  = new String[PLACE_PARAM_NUM];
    private String[] request_values = new String[PLACE_PARAM_NUM];
    private static ArrayList<String> ret_place  = new ArrayList<String>();
    private TextView place ;
    private TextView address;
    private TextView hours;
    private TextView intro ;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        String placeid ;
        Bundle placedata = this.getIntent().getExtras();
        placeid = placedata.getString("specifyid");
        
        request_name[0] = "place_ID";
        request_values[0] = placeid;
        place = (TextView)findViewById(R.id.titletext);
        address = (TextView)findViewById(R.id.addresstext);
        hours = (TextView)findViewById(R.id.timetext);
        intro = (TextView)findViewById(R.id.infotext);
        photo = (ImageView)findViewById(R.id.imageView);
        new Thread(getdata).start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
                {   case DOWNLOAD_COMPLETE:
                        place.setText(ret_place.get(0));
                        hours.setText(ret_place.get(1));
                        address.setText(ret_place.get(2));
                        intro.setText(ret_place.get(3));

                    try
                    {
                        Picasso.with(getApplicationContext())
                        .load(URL_PREFIX_IMAGE
                            + URLEncoder.encode(ret_place.get(0), "UTF-8") + ".jpg")
                                .into(photo);
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
        public void run() {
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
